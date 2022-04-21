package pl.janek49.iniektor.api;

import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.mapper.ForgeMapper;
import pl.janek49.iniektor.mapper.Mapper;
import pl.janek49.iniektor.mapper.Pre17Mapper;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Reflector {

    public static boolean TRIGGER_HOTSWAP = false;

    public static boolean IS_FORGE;
    public static Version MCP_VERSION;
    public static String MCP_VERSION_STRING;
    public static String MCP_PATH;
    public static Mapper MAPPER;
    public static Reflector INSTANCE;
    public static boolean TEST_MODE;

    public List<IWrapper> Wrappers;

    public static WrapperPlayer PLAYER;
    public static WrapperMinecraft MINECRAFT;

    public int errors = 0;

    enum IterationResult {
        FOUND, MISSING, SKIP_TARGET
    }

    public Reflector() {
        INSTANCE = this;
        MCP_VERSION = Version.valueOf(MCP_VERSION_STRING);

        if (MCP_VERSION.ordinal() < Version.MC1_7_10.ordinal()) {
            MAPPER = new Pre17Mapper(MCP_PATH);
        } else {
            MAPPER = IS_FORGE ? new ForgeMapper(MCP_PATH) : new Mapper(MCP_PATH);
        }
        MAPPER.init();

        Wrappers = new ArrayList<>();

        Wrappers.add(Reflector.PLAYER = new WrapperPlayer());
        Wrappers.add(Reflector.MINECRAFT = new WrapperMinecraft());

        Wrappers.add(new WrapperMisc());
        Wrappers.add(new WrapperChat());
        Wrappers.add(new WrapperPacket());

        for (IWrapper wrapper : Wrappers) {
            for (Field fd : wrapper.getClass().getDeclaredFields()) {
                try {
                    IterationResult ir = null;

                    if (fd.getType() == FieldDefinition.class) {
                        ResolveFieldBase rfb = fd.getAnnotation(ResolveFieldBase.class);
                        ResolveField[] annots = rfb != null ? rfb.value() : new ResolveField[]{fd.getAnnotation(ResolveField.class)};

                        for (ResolveField rf : annots)
                            if ((ir = iterateVersionsField(wrapper, fd, rf)) == IterationResult.FOUND) break;

                        if (fd.get(wrapper) == null) {
                            if (ir == IterationResult.MISSING) {
                                Logger.err("Reflector ResolveField FAILED:", wrapper.getClass().getSimpleName(), fd.getName());
                                errors++;
                            } else {
                                Logger.log("Reflector ResolveField SKIP:", wrapper.getClass().getSimpleName(), fd.getName());
                            }
                        }

                    } else if (fd.getType() == MethodDefinition.class) {
                        ResolveMethodBase rfb = fd.getAnnotation(ResolveMethodBase.class);
                        ResolveMethod[] annots = rfb != null ? rfb.value() : new ResolveMethod[]{fd.getAnnotation(ResolveMethod.class)};

                        for (ResolveMethod rf : annots)
                            if ((ir = iterateVersionsMethod(wrapper, fd, rf)) == IterationResult.FOUND) break;

                        if (fd.get(wrapper) == null) {
                            if (ir == IterationResult.MISSING) {
                                Logger.err("Reflector ResolveMethod FAILED:", wrapper.getClass().getSimpleName(),fd.getName());
                                errors++;
                            } else {
                                Logger.log("Reflector ResolveMethod SKIP:",wrapper.getClass().getSimpleName(), fd.getName());
                            }
                        }
                    } else if (fd.getType() == ConstructorDefinition.class) {

                        ResolveConstructorBase rfb = fd.getAnnotation(ResolveConstructorBase.class);
                        ResolveConstructor[] annots = rfb != null ? rfb.value() : new ResolveConstructor[]{fd.getAnnotation(ResolveConstructor.class)};

                        for (ResolveConstructor rf : annots)
                            if ((ir = iterateVersionsConstructor(wrapper, fd, rf)) == IterationResult.FOUND) break;

                        if (fd.get(wrapper) == null) {
                            if (ir == IterationResult.MISSING) {
                                Logger.err("Reflector ResolveConstructor FAILED:",wrapper.getClass().getSimpleName(), fd.getName());
                                errors++;
                            } else {
                                Logger.log("Reflector ResolveConstructor SKIP:", wrapper.getClass().getSimpleName(),fd.getName());
                            }
                        }
                    }
                } catch (Exception e) {
                    Logger.err("Reflector ERROR:",wrapper.getClass().getSimpleName(), fd.getName());
                    errors++;
                    e.printStackTrace();
                }
            }
            try {
                if (!TEST_MODE)
                    wrapper.initWrapper();
            } catch (Exception ex) {
                Logger.err("Reflector ERROR: Initializing Wrapper:", wrapper);
                ex.printStackTrace();
            }
        }
    }

    public static boolean isOnOrAbvVersion(Version v) {
        return MCP_VERSION.ordinal() >= v.ordinal();
    }

    public static boolean isOnOrBlwVersion(Version v) {
        return MCP_VERSION.ordinal() <= v.ordinal();
    }

    public static boolean isOnVersion(Version v) {
        return MCP_VERSION == v || v == Version.DEFAULT;
    }

    private IterationResult iterateVersionsMethod(IWrapper wrapper, Field fd, ResolveMethod rf) throws Exception {
        if (rf == null) return IterationResult.MISSING;

        for (Version v : rf.version()) {
            if (isOnVersion(v) || (rf.andAbove() && isOnOrAbvVersion(v))) {
                String[] methodName = MAPPER.getObfMethodName(rf.name(), rf.descriptor());
                if (methodName == null)
                    return v == Version.DEFAULT ? IterationResult.MISSING : IterationResult.SKIP_TARGET;

                String mdName = Util.getLastPartOfArray(methodName[0].split("/"));

                String className = Mapper.GetClassNameFromFullMethod(methodName[0]);
                Class klass = Class.forName(className.replace("/", "."));

                for (Method md : klass.getDeclaredMethods()) {
                    if (md.getName().equals(mdName) && getSignature(md).equals(methodName[1])) {
                        md.setAccessible(true);
                        MethodDefinition mdf = new MethodDefinition(wrapper, md);
                        fd.set(wrapper, mdf);
                        Logger.log("Reflector ResolveMethod:", v, rf.name(), String.join(":", methodName));
                        return IterationResult.FOUND;
                    }
                }
                break;
            }
        }
        return IterationResult.SKIP_TARGET;
    }

    private IterationResult iterateVersionsConstructor(IWrapper wrapper, Field fd, ResolveConstructor rf) throws Exception {
        if (rf == null) return IterationResult.MISSING;

        for (Version v : rf.version()) {
            if (isOnVersion(v) || (rf.andAbove() && isOnOrAbvVersion(v))) {

                String[] ctx = new String[rf.params().length];
                int i = 0;
                for (String str : rf.params()) {
                    if (str.contains("/")) {
                        if (str.startsWith("net/minecraft")) {
                            ctx[i] = ("L" + MAPPER.getObfClassName(str) + ";");
                        } else {
                            ctx[i] = ("L" + str + ";");
                        }
                    } else {
                        ctx[i] = (str);
                    }
                    i++;
                }
                String sig = "(" + String.join("", ctx) + ")";

                String obfName = MAPPER.getObfClassName(rf.name());

                if (obfName == null)
                    return v == Version.DEFAULT ? IterationResult.MISSING : IterationResult.SKIP_TARGET;
                ;

                Class klass = Class.forName(obfName.replace("/", "."));

                for (Constructor ct : klass.getDeclaredConstructors()) {
                    if (getSignature(ct).equals(sig)) {
                        ct.setAccessible(true);
                        ConstructorDefinition mdf = new ConstructorDefinition(ct);
                        fd.set(wrapper, mdf);
                        Logger.log("Reflector ResolveConstructor:", v, rf.name(), sig);
                        return IterationResult.FOUND;
                    }
                }
                break;
            }
        }
        return IterationResult.SKIP_TARGET;
    }

    private IterationResult iterateVersionsField(IWrapper wrapper, Field fd, ResolveField rf) throws Exception {
        if (rf == null) return IterationResult.MISSING;

        for (Version v : rf.version()) {
            if (isOnVersion(v) || (rf.andAbove() && isOnOrAbvVersion(v))) {
                String obfFieldName = MAPPER.getObfFieldName(rf.name());

                if (obfFieldName == null)
                    return v == Version.DEFAULT ? IterationResult.MISSING : IterationResult.SKIP_TARGET;

                String className = Mapper.GetClassNameFromFullMethod(obfFieldName);
                String fieldName = MAPPER.getShortObfFieldName(rf.name());

                Field jfd = Class.forName(className.replace("/", ".")).getDeclaredField(fieldName);
                jfd.setAccessible(true);
                FieldDefinition fdf = new FieldDefinition(wrapper, jfd);
                fd.set(wrapper, fdf);

                Logger.log("Reflector ResolveField:", v, rf.name(), obfFieldName);
                return IterationResult.FOUND;
            }
        }
        return IterationResult.SKIP_TARGET;
    }

    public static String getSignature(Method m) {
        String sig;

        StringBuilder sb = new StringBuilder("(");
        for (Class<?> c : m.getParameterTypes())
            sb.append((sig = Array.newInstance(c, 0).toString()).substring(1, sig.indexOf('@')));
        return sb.append(')').append(m.getReturnType() == void.class ? "V" : (sig = Array.newInstance(m.getReturnType(), 0).toString()).substring(1, sig.indexOf('@'))).toString().replace(".", "/");
    }

    public static String getSignature(Constructor ct) {
        String sig;

        StringBuilder sb = new StringBuilder("(");
        for (Class<?> c : ct.getParameterTypes())
            sb.append((sig = Array.newInstance(c, 0).toString()).substring(1, sig.indexOf('@')));
        return sb.append(')').toString().replace(".", "/");
    }

    public static <T> T getDeclaredFieldValue(Class clazz, Object instance, String fieldName) {
        try {
            Field fd = clazz.getDeclaredField(fieldName);
            return (T) fd.get(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getPrivateFieldValue(Class clazz, Object instance, String fieldName) {
        try {
            Field fd = clazz.getDeclaredField(fieldName);
            fd.setAccessible(true);
            return (T) fd.get(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
