package pl.janek49.iniektor.api;

import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.client.*;
import pl.janek49.iniektor.api.gui.*;
import pl.janek49.iniektor.api.network.CPacketPlayer;
import pl.janek49.iniektor.api.network.Packet;
import pl.janek49.iniektor.api.network.PacketHelper;
import pl.janek49.iniektor.api.network.SPacketEntityVelocity;
import pl.janek49.iniektor.mapper.ForgeMapper;
import pl.janek49.iniektor.mapper.Mapper;
import pl.janek49.iniektor.mapper.MojangMapper;
import pl.janek49.iniektor.mapper.Pre17Mapper;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Reflector {

    public static final String SKIP_MEMBER = "@SKIP_MEMBER@";
    public static boolean IS_FORGE;
    public static Version MCP_VERSION;
    public static String MCP_VERSION_STRING;
    public static String MCP_PATH;
    public static Mapper MAPPER;
    public static Reflector INSTANCE;
    public static boolean TEST_MODE;
    public static boolean USE_NEW_API;

    public List<IWrapper> Wrappers;
    public List<Class<? extends ClassImitator>> Imitators;

    public static WrapperMinecraft MINECRAFT;

    public int errors = 0;

    public static boolean IS_PRE17 = false;

    enum IterationResult {
        FOUND, MISSING, SKIP_TARGET, SKIP_MEMBER
    }

    public Reflector() {
        INSTANCE = this;
        MCP_VERSION = Version.valueOf(MCP_VERSION_STRING);

        if (MCP_VERSION.ordinal() < Version.MC1_7_10.ordinal()) {
            IS_PRE17 = true;
            MAPPER = new Pre17Mapper(MCP_PATH);
        } else if (MCP_VERSION.ordinal() > Version.MC1_12_2.ordinal()) {
            MAPPER = new MojangMapper(MCP_PATH);
        } else {
            MAPPER = IS_FORGE ? new ForgeMapper(MCP_PATH) : new Mapper(MCP_PATH);
        }

        USE_NEW_API = MCP_VERSION.ordinal() >= Version.MC1_14_4.ordinal();

        MAPPER.init();

        Wrappers = new ArrayList<>();

        Wrappers.add(Reflector.MINECRAFT = new WrapperMinecraft());

        Wrappers.add(new WrapperMisc());
        Wrappers.add(new WrapperChat());
        Wrappers.add(new PacketHelper());
        Wrappers.add(new WrapperResolution());

        initializeWrappers(Wrappers);

        Imitators = new ArrayList<>();
        Imitators.add(SPacketEntityVelocity.class);
        Imitators.add(GuiButton.class);
        Imitators.add(Entity.class);
        Imitators.add(EntityPlayerSP.class);
        Imitators.add(GameSettings.class);
        Imitators.add(Minecraft.class);
        Imitators.add(PlayerCapabilities.class);
        Imitators.add(Blaze3DWindow.class);
        Imitators.add(FontRenderer.class);
        Imitators.add(Gui.class);
        Imitators.add(GuiMainMenu.class);
        Imitators.add(ResourceLocation.class);
        Imitators.add(ScaledResolution.class);
        Imitators.add(DynamicTexture.class);
        Imitators.add(TextureManager.class);
        Imitators.add(TextComponent.class);
        Imitators.add(Vec3.class);
        Imitators.add(Packet.class);
        Imitators.add(CPacketPlayer.class);

        initializeImitators();
    }


    public void initializeImitators() {
        List<IWrapper> fakeWrappers = new ArrayList<>();

        for (Class<? extends ClassImitator> imitator : Imitators) {
            try {
                ClassImitator.ResolveClassBase rfb = imitator.getAnnotation(ClassImitator.ResolveClassBase.class);
                ClassImitator.ResolveClass[] annots = rfb != null ? rfb.value() : new ClassImitator.ResolveClass[]{imitator.getAnnotation(ClassImitator.ResolveClass.class)};

                IterationResult ir = null;
                for (ClassImitator.ResolveClass rc : annots) {
                    ir = iterateVersionsClassDefinition(imitator, rc);
                    if (ir == IterationResult.FOUND || ir == IterationResult.MISSING) break;
                }

                ClassImitator.ClassInformation info = (ClassImitator.ClassInformation) imitator.getDeclaredField("target").get(null);

                if (info == null) {
                    if (ir == IterationResult.MISSING) {
                        Logger.err("Reflector ResolveClass FAILED:", imitator.getName());
                        errors++;
                    } else {
                        Logger.log("Reflector ResolveClass SKIP:", imitator.getName());
                    }
                } else {
                    Constructor<? extends ClassImitator> c = imitator.getDeclaredConstructor();
                    c.setAccessible(true);
                    fakeWrappers.add(c.newInstance());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.err("Caused by: " + imitator);
            }
        }

        initializeWrappers(fakeWrappers);
    }

    private void initializeWrappers(List<IWrapper> wrappers) {
        for (IWrapper wrapper : wrappers) {
            for (Field fd : wrapper.getClass().getDeclaredFields()) {
                fd.setAccessible(true);
                try {
                    IterationResult ir = null;

                    if (fd.getType() == FieldDefinition.class) {
                        ResolveFieldBase rfb = fd.getAnnotation(ResolveFieldBase.class);
                        ResolveField[] annots = rfb != null ? rfb.value() : new ResolveField[]{fd.getAnnotation(ResolveField.class)};

                        for (ResolveField rf : annots) {
                            ir = iterateVersionsField(wrapper, fd, rf);
                            if (ir == IterationResult.FOUND || ir == IterationResult.MISSING) break;
                        }

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

                        for (ResolveMethod rf : annots) {
                            ir = iterateVersionsMethod(wrapper, fd, rf);
                            if (ir == IterationResult.FOUND || ir == IterationResult.MISSING) break;
                        }

                        if (fd.get(wrapper) == null) {
                            if (ir == IterationResult.MISSING) {
                                Logger.err("Reflector ResolveMethod FAILED:", wrapper.getClass().getSimpleName(), fd.getName());
                                errors++;
                            } else {
                                Logger.log("Reflector ResolveMethod SKIP:", wrapper.getClass().getSimpleName(), fd.getName());
                            }
                        }
                    } else if (fd.getType() == ConstructorDefinition.class) {

                        ResolveConstructorBase rfb = fd.getAnnotation(ResolveConstructorBase.class);
                        ResolveConstructor[] annots = rfb != null ? rfb.value() : new ResolveConstructor[]{fd.getAnnotation(ResolveConstructor.class)};

                        for (ResolveConstructor rf : annots) {
                            ir = iterateVersionsConstructor(wrapper, fd, rf);
                            if (ir == IterationResult.FOUND || ir == IterationResult.MISSING) break;
                        }

                        if (fd.get(wrapper) == null) {
                            if (ir == IterationResult.MISSING) {
                                Logger.err("Reflector ResolveConstructor FAILED:", wrapper.getClass().getSimpleName(), fd.getName());
                                errors++;
                            } else {
                                Logger.log("Reflector ResolveConstructor SKIP:", wrapper.getClass().getSimpleName(), fd.getName());
                            }
                        }
                    }
                } catch (Exception e) {
                    Logger.err("Reflector ERROR:", wrapper.getClass().getSimpleName(), fd.getName());
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


    private IterationResult iterateVersionsClassDefinition(Class<? extends ClassImitator> imitator, ClassImitator.ResolveClass rc) throws Exception {
        if (rc == null)
            return IterationResult.MISSING;

        if (rc.value().equals(Reflector.SKIP_MEMBER))
            return IterationResult.FOUND;

        for (Version v : rc.version()) {
            if (isOnVersion(v) || (rc.andAbove() && isOnOrAbvVersion(v))) {

                String obfName = MAPPER.getObfClassName(rc.value());
                if (obfName == null)
                    return IterationResult.MISSING;//v == Version.DEFAULT ? IterationResult.MISSING : IterationResult.SKIP_TARGET;

                Class klass = Class.forName(obfName.replace("/", "."));

                ClassImitator.ClassInformation info = new ClassImitator.ClassInformation();
                info.javaClass = klass;
                info.deobfClassName = rc.value();
                info.obfClassName = obfName;

                imitator.getField("target").set(null, info);
                Logger.log("Reflector ResolveClass:", v, rc.value(), "->", obfName);
                return IterationResult.FOUND;
            }
        }
        return IterationResult.SKIP_TARGET;
    }

    private IterationResult iterateVersionsMethod(IWrapper wrapper, Field fd, ResolveMethod rf) throws Exception {
        if (rf == null) return IterationResult.MISSING;

        for (Version v : rf.version()) {
            if (isOnVersion(v) || (rf.andAbove() && isOnOrAbvVersion(v))) {
                String name = rf.name();

                if (name.equals(SKIP_MEMBER))
                    return IterationResult.FOUND;

                if (wrapper instanceof ClassImitator && !name.contains("/")) {
                    name = ((ClassImitator) wrapper).getTarget().deobfClassName + "/" + name;
                }

                String[] methodName = MAPPER.getObfMethodName(name, rf.descriptor());
                if (methodName == null)
                    return IterationResult.MISSING;//v == Version.DEFAULT ? IterationResult.MISSING : IterationResult.SKIP_TARGET;

                String mdName = Util.getLastPartOfArray(methodName[0].split("/"));

                String className = Mapper.GetClassNameFromFullMethod(methodName[0]);
                Class klass = Class.forName(className.replace("/", "."));

                for (Method md : klass.getDeclaredMethods()) {
                    if (md.getName().equals(mdName) && getSignature(md).equals(methodName[1])) {
                        md.setAccessible(true);
                        MethodDefinition mdf = new MethodDefinition(wrapper, md);
                        fd.set(wrapper, mdf);
                        Logger.log("Reflector ResolveMethod:", v, name, String.join(":", methodName));
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
                String name = rf.name();

                if (name.equals(SKIP_MEMBER))
                    return IterationResult.FOUND;

                if (wrapper instanceof ClassImitator) {
                    name = ((ClassImitator) wrapper).getTarget().deobfClassName;
                }

                String[] ctx = new String[rf.params().length];
                int i = 0;
                for (String str : rf.params()) {
                    if (str.contains("/")) {
                        ctx[i] = ("L" + MAPPER.getObfClassNameIfExists(str) + ";");
                    } else {
                        ctx[i] = (str);
                    }
                    i++;
                }
                String sig = "(" + String.join("", ctx) + ")";

                String obfName = MAPPER.getObfClassName(name);

                if (obfName == null)
                    return IterationResult.MISSING;// v == Version.DEFAULT ? IterationResult.MISSING : IterationResult.SKIP_TARGET;

                try {
                    Class klass = Class.forName(obfName.replace("/", "."));
                    for (Constructor ct : klass.getDeclaredConstructors()) {
                        if (getSignature(ct).equals(sig)) {
                            ct.setAccessible(true);
                            ConstructorDefinition mdf = new ConstructorDefinition(ct);
                            mdf.javaClass = klass;
                            fd.set(wrapper, mdf);
                            Logger.log("Reflector ResolveConstructor:", v, name, sig);
                            return IterationResult.FOUND;
                        }
                    }
                    return IterationResult.MISSING;
                } catch (Throwable ex) {
                    Logger.err(wrapper.getClass(), fd.getName());
                    ex.printStackTrace();
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
                String name = rf.value();

                if (name.equals(SKIP_MEMBER))
                    return IterationResult.FOUND;

                if (wrapper instanceof ClassImitator && !name.contains("/")) {
                    name = ((ClassImitator) wrapper).getTarget().deobfClassName + "/" + name;
                }

                String obfFieldName = MAPPER.getObfFieldName(name);

                if (obfFieldName == null)
                    return IterationResult.MISSING;//v == Version.DEFAULT ? IterationResult.MISSING : IterationResult.SKIP_TARGET;

                String className = Mapper.GetClassNameFromFullMethod(obfFieldName);
                String fieldName = MAPPER.getShortObfFieldName(name);

                try {
                    Field jfd = Class.forName(className.replace("/", ".")).getDeclaredField(fieldName);
                    jfd.setAccessible(true);
                    FieldDefinition fdf = new FieldDefinition(wrapper, jfd);
                    fd.setAccessible(true);
                    fd.set(wrapper, fdf);

                    Logger.log("Reflector ResolveField:", v, name, obfFieldName);
                    return IterationResult.FOUND;
                } catch (NoSuchFieldException | NoSuchFieldError ex) {
                    return IterationResult.MISSING;
                }
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
