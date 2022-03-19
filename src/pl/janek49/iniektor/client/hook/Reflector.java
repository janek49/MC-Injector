package pl.janek49.iniektor.client.hook;

import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.mapper.ForgeMapper;
import pl.janek49.iniektor.mapper.Mapper;

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

    public List<IWrapper> Wrappers;

    public static WrapperPlayer PLAYER;
    public static WrapperMinecraft MC;

    public Reflector() {
        INSTANCE = this;

        MAPPER = IS_FORGE ? new ForgeMapper(MCP_PATH) : new Mapper(MCP_PATH);
        MAPPER.init();

        MCP_VERSION = Version.valueOf(MCP_VERSION_STRING);

        Wrappers = new ArrayList<>();

        Wrappers.add(Reflector.PLAYER = new WrapperPlayer());
        Wrappers.add(Reflector.MC = new WrapperMinecraft());
        Wrappers.add(new MCC());

        for (IWrapper wrapper : Wrappers) {
            for (Field fd : wrapper.getClass().getDeclaredFields()) {
                if (fd.getType() == String.class) {
                    try {
                        ResolveFieldBase rfb = fd.getAnnotation(ResolveFieldBase.class);
                        if (rfb != null) {
                            for (ResolveField rf : rfb.value())
                                if (iterateVersions(wrapper, fd, rf))
                                    break;
                        } else {
                            ResolveField rf = fd.getAnnotation(ResolveField.class);
                            if (rf != null)
                                iterateVersions(wrapper, fd, rf);
                        }
                    } catch (Exception e) {
                        Logger.log("Reflector ResolveField ERROR:", fd.getName());
                        e.printStackTrace();
                    }
                }
                if (fd.getType() == FieldDefinition.class) {
                    try {
                        ResolveFieldBase rfb = fd.getAnnotation(ResolveFieldBase.class);
                        if (rfb != null) {
                            for (ResolveField rf : rfb.value())
                                if (iterateVersionsField(wrapper, fd, rf))
                                    break;
                            if (fd.get(wrapper) == null)
                                Logger.log("Reflector ResolveField FAILED:", fd.getName());
                        } else {
                            ResolveField rf = fd.getAnnotation(ResolveField.class);
                            if (rf != null)
                                if (!iterateVersionsField(wrapper, fd, rf))
                                    Logger.log("Reflector ResolveField FAILED:", fd.getName());
                        }
                    } catch (Exception e) {
                        Logger.log("Reflector ResolveField ERROR:", fd.getName());
                        e.printStackTrace();
                    }
                } else if (fd.getType() == MethodDefinition.class) {
                    try {
                        ResolveMethodBase rfb = fd.getAnnotation(ResolveMethodBase.class);
                        if (rfb != null) {
                            for (ResolveMethod rm : rfb.value())
                                if (iterateVersionsMethod(wrapper, fd, rm))
                                    break;
                            if (fd.get(wrapper) == null) {
                                Logger.log("Reflector ResolveMethod FAILED:", fd.getName());
                            }
                        } else {
                            ResolveMethod rm = fd.getAnnotation(ResolveMethod.class);
                            if (rm != null)
                                if (!iterateVersionsMethod(wrapper, fd, rm))
                                    Logger.log("Reflector ResolveMethod FAILED:", rm.name(), rm.descriptor());
                        }
                    } catch (Exception e) {
                        Logger.log("Reflector ResolveMethod ERROR:", fd.getName());
                        e.printStackTrace();
                    }
                } else if (fd.getType() == ConstructorDefinition.class) {
                    try {
                        ResolveConstructorBase rfb = fd.getAnnotation(ResolveConstructorBase.class);
                        if (rfb != null) {
                            for (ResolveConstructor rm : rfb.value())
                                if (iterateVersionsConstructor(wrapper, fd, rm))
                                    break;
                            if (fd.get(wrapper) == null)
                                Logger.log("Reflector ResolveConstructor FAILED:", fd.getName());
                        } else {
                            ResolveConstructor rm = fd.getAnnotation(ResolveConstructor.class);
                            if (rm != null)
                                if (!iterateVersionsConstructor(wrapper, fd, rm))
                                    Logger.log("Reflector ResolveConstructor FAILED:", rm.name(), String.join("", rm.params()));
                        }
                    } catch (Exception e) {
                        Logger.log("Reflector ResolveConstructor ERROR:", fd.getName());
                        e.printStackTrace();
                    }
                }
            }
            try {
                wrapper.initWrapper();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean iterateVersionsMethod(IWrapper wrapper, Field fd, ResolveMethod rf) throws Exception {
        for (Version v : rf.version()) {
            if (v == MCP_VERSION || v == Version.DEFAULT) {
                String[] methodName = MAPPER.getObfMethodName(rf.name(), rf.descriptor());
                String mdName = Util.getLastPartOfArray(methodName[0].split("/"));

                String className = Mapper.GetClassNameFromFullMethod(methodName[0]);
                Class klass = Class.forName(className.replace("/", "."));

                for (Method md : klass.getDeclaredMethods()) {
                    if (md.getName().equals(mdName) && getSignature(md).equals(methodName[1])) {
                        md.setAccessible(true);
                        MethodDefinition mdf = new MethodDefinition(wrapper, md);
                        fd.set(wrapper, mdf);
                        Logger.log("Reflector ResolveMethod:", v, rf.name(), String.join(":", methodName));
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }

    private boolean iterateVersionsConstructor(IWrapper wrapper, Field fd, ResolveConstructor rf) throws Exception {
        for (Version v : rf.version()) {
            if (v == MCP_VERSION || v == Version.DEFAULT) {

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

                Class klass = Class.forName(MAPPER.getObfClassName(rf.name()).replace("/", "."));

                for (Constructor ct : klass.getDeclaredConstructors()) {
                    if (getSignature(ct).equals(sig)) {
                        ct.setAccessible(true);
                        ConstructorDefinition mdf = new ConstructorDefinition(ct);
                        fd.set(wrapper, mdf);
                        Logger.log("Reflector ResolveConstructor:", v, rf.name(), sig);
                        return true;
                    }
                }
                break;
            }
        }
        Logger.log("Reflector ResolveConstructor FAILED:", rf.name(), String.join("", rf.params()));
        return false;
    }

    private boolean iterateVersionsField(IWrapper wrapper, Field fd, ResolveField rf) throws Exception {
        for (Version v : rf.version()) {
            if (v == MCP_VERSION || v == Version.DEFAULT) {
                String obfFieldName = MAPPER.getObfFieldName(rf.name());

                String className = Mapper.GetClassNameFromFullMethod(obfFieldName);
                String fieldName = MAPPER.getShortObfFieldName(rf.name());

                Field jfd = Class.forName(className.replace("/", ".")).getDeclaredField(fieldName);
                jfd.setAccessible(true);
                FieldDefinition fdf = new FieldDefinition(jfd);
                fd.set(wrapper, fdf);

                Logger.log("Reflector ResolveField:", v, rf.name(), obfFieldName);
                return true;
            }
        }
        return false;
    }

    private boolean iterateVersions(IWrapper wrapper, Field fd, ResolveField rf) throws IllegalAccessException {
        for (Version v : rf.version()) {
            if (v == MCP_VERSION || v == Version.DEFAULT) {
                String fieldName = MAPPER.getShortObfFieldName(rf.name());
                fd.set(wrapper, fieldName);
                Logger.log("Reflector ResolveField:", v, rf.name(), fieldName);
                return true;
            }
        }
        return false;
    }

    public static String getSignature(Method m) {
        String sig;

        StringBuilder sb = new StringBuilder("(");
        for (Class<?> c : m.getParameterTypes())
            sb.append((sig = Array.newInstance(c, 0).toString())
                    .substring(1, sig.indexOf('@')));
        return sb.append(')')
                .append(
                        m.getReturnType() == void.class ? "V" :
                                (sig = Array.newInstance(m.getReturnType(), 0).toString()).substring(1, sig.indexOf('@'))
                )
                .toString().replace(".", "/");
    }

    public static String getSignature(Constructor ct) {
        String sig;

        StringBuilder sb = new StringBuilder("(");
        for (Class<?> c : ct.getParameterTypes())
            sb.append((sig = Array.newInstance(c, 0).toString())
                    .substring(1, sig.indexOf('@')));
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
