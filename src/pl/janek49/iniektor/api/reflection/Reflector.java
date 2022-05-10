package pl.janek49.iniektor.api.reflection;

import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.asm.AsmUtil;
import pl.janek49.iniektor.api.client.*;
import pl.janek49.iniektor.api.gui.*;
import pl.janek49.iniektor.api.network.CPacketPlayer;
import pl.janek49.iniektor.api.network.Packet;
import pl.janek49.iniektor.api.wrapper.WrapperPacket;
import pl.janek49.iniektor.api.network.SPacketEntityVelocity;
import pl.janek49.iniektor.api.wrapper.WrapperChat;
import pl.janek49.iniektor.api.wrapper.WrapperMinecraft;
import pl.janek49.iniektor.api.wrapper.WrapperMisc;
import pl.janek49.iniektor.api.wrapper.WrapperResolution;
import pl.janek49.iniektor.mapper.SeargeMapper;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reflector {

    public static final String SKIP_MEMBER = "@SKIP_MEMBER@";
    public static boolean IS_FORGE;
    public static Version MCP_VERSION;
    public static String MCP_VERSION_STRING;
    public static String MCP_PATH;
    public static SeargeMapper MAPPER;
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
        IS_PRE17 = isOnOrBlwVersion(Version.MC1_6_4);
        USE_NEW_API = isOnOrAbvVersion(Version.MC1_14_4);

        MAPPER = AgentMain.createMapper(MCP_VERSION, IS_FORGE, MCP_PATH);
        MAPPER.init();

        Wrappers = new ArrayList<>();

        Wrappers.add(Reflector.MINECRAFT = new WrapperMinecraft());

        Wrappers.add(new WrapperMisc());
        Wrappers.add(new WrapperChat());
        Wrappers.add(new WrapperPacket());
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
        Imitators.add(EntityLiving.class);


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
                        Logger.warn("Reflector ResolveClass SKIP:", imitator.getName());
                    }
                } else {
                    fakeWrappers.add((IWrapper) AsmUtil.getUnsafe().allocateInstance(imitator));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.err("Caused by: " + imitator);
            }
        }

        initializeWrappers(fakeWrappers);
    }

    private void verifyDefinition(Field field, IWrapper wrapper, IterationResult ir) throws IllegalAccessException {
        if (field.get(wrapper) == null) {
            if (ir == IterationResult.MISSING) {
                Logger.err("Reflector ResolveMember FAILED:", wrapper.getClass().getSimpleName(), field.getName());
                errors++;
            } else {
                Logger.warn("Reflector ResolveMember SKIP:", wrapper.getClass().getSimpleName(), field.getName());
            }
        }
    }

    private void initializeWrappers(List<IWrapper> wrappers) {
        for (IWrapper wrapper : wrappers) {
            for (Field fd : wrapper.getClass().getDeclaredFields()) {
                fd.setAccessible(true);
                try {
                    IterationResult ir = null;
                    List<Object> annotations = new ArrayList<>();

                    if (fd.getType() == FieldDefinition.class) {
                        ResolveFieldBase rfb = fd.getAnnotation(ResolveFieldBase.class);
                        annotations.addAll(Arrays.asList(rfb != null ? rfb.value() : new ResolveField[]{fd.getAnnotation(ResolveField.class)}));
                    } else if (fd.getType() == MethodDefinition.class) {
                        ResolveMethodBase rfb = fd.getAnnotation(ResolveMethodBase.class);
                        annotations.addAll(Arrays.asList(rfb != null ? rfb.value() : new ResolveMethod[]{fd.getAnnotation(ResolveMethod.class)}));
                    } else if (fd.getType() == ConstructorDefinition.class) {
                        ResolveConstructorBase rfb = fd.getAnnotation(ResolveConstructorBase.class);
                        annotations.addAll(Arrays.asList(rfb != null ? rfb.value() : new ResolveConstructor[]{fd.getAnnotation(ResolveConstructor.class)}));
                    }

                    for (Object annot : annotations) {
                        if (annot instanceof ResolveField) {
                            ir = iterateVersionsField(wrapper, fd, (ResolveField) annot);
                        } else if (annot instanceof ResolveMethod) {
                            ir = iterateVersionsMethod(wrapper, fd, (ResolveMethod) annot);
                        } else if (annot instanceof ResolveConstructor) {
                            ir = iterateVersionsConstructor(wrapper, fd, (ResolveConstructor) annot);
                        }

                        if (ir == IterationResult.FOUND || ir == IterationResult.MISSING)
                            break;
                    }

                    //check if member was resolved and log possible error
                    verifyDefinition(fd, wrapper, ir);

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

        for (Version v : rc.version()) {
            if (isOnVersion(v) || (rc.andAbove() && isOnOrAbvVersion(v))) {

                if (rc.value().equals(Reflector.SKIP_MEMBER))
                    return IterationResult.FOUND;

                String obfName = MAPPER.getObfClassName(rc.value());
                if (obfName == null)
                    return IterationResult.MISSING;

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
                    ClassImitator.ClassInformation target = null;

                    if (rf.parent() != ClassImitator.EmptyImitator.class)
                        target = ClassImitator.getTarget(rf.parent());
                    else
                        target = ((ClassImitator) wrapper).getTarget();

                    name = target.deobfClassName + "/" + name;
                }

                SeargeMapper.MethodMatch methodMapping = MAPPER.findMethodMappingByDeobf(name, rf.descriptor());

                if (methodMapping == null)
                    return IterationResult.MISSING;

                Class klass = Class.forName(methodMapping.getObfuscatedOwnerDotted());

                for (Method md : klass.getDeclaredMethods()) {
                    if (md.getName().equals(methodMapping.obfName) && getSignature(md).equals(methodMapping.obfDesc)) {
                        md.setAccessible(true);
                        MethodDefinition mdf = new MethodDefinition(wrapper, md);
                        fd.set(wrapper, mdf);
                        Logger.log("Reflector ResolveMethod:", v, methodMapping.toString());
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
                    return IterationResult.MISSING;

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

                SeargeMapper.FieldMatch fieldMatch = MAPPER.findFieldByDeobf(name);

                if (fieldMatch == null)
                    return IterationResult.MISSING;

                try {
                    Field jfd = Class.forName(fieldMatch.getObfuscatedOwnerDotted()).getDeclaredField(fieldMatch.obfName);
                    jfd.setAccessible(true);
                    FieldDefinition fdf = new FieldDefinition(wrapper, jfd);
                    fd.setAccessible(true);
                    fd.set(wrapper, fdf);

                    Logger.log("Reflector ResolveField:", v, fieldMatch);
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