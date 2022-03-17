package pl.janek49.iniektor.client.hook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.util.Timer;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.mapper.Mapper;

import java.lang.reflect.Field;

public class Reflector {

    public static Version MCP_VERSION;
    public static String MCP_VERSION_STRING;
    public static String MCP_PATH;
    public static Mapper MAPPER;
    public static Reflector INSTANCE;


    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/client/Minecraft/timer")
    public String TIMER_FIELD;
    public Timer minecraftTimer;

    @ResolveField(version = Version.MC1_7_10, name = "net/minecraft/client/Minecraft/fontRenderer")
    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/client/Minecraft/fontRendererObj")
    public String FONTRENDER_FIELD;
    public FontRenderer fontRenderer;


    public Reflector() {
        INSTANCE = this;
        MAPPER = new Mapper(MCP_PATH);
        MAPPER.init();
        MCP_VERSION = Version.valueOf(MCP_VERSION_STRING);

        for (Field fd : getClass().getDeclaredFields()) {
            try {
                ResolveFieldBase rfb = fd.getAnnotation(ResolveFieldBase.class);
                if (rfb != null) {
                    for (ResolveField rf : rfb.value())
                        if (iterateVersions(fd, rf))
                            break;
                } else {
                    ResolveField rf = fd.getAnnotation(ResolveField.class);
                    if (rf != null)
                        iterateVersions(fd, rf);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        initFields();
    }

    private void initFields() {
        Minecraft mc = Minecraft.getMinecraft();

        minecraftTimer = getPrivateFieldValue(Minecraft.class, mc, TIMER_FIELD);
        fontRenderer = getDeclaredFieldValue(Minecraft.class, mc, FONTRENDER_FIELD);
    }

    private boolean iterateVersions(Field fd, ResolveField rf) throws IllegalAccessException {
        for (Version v : rf.version()) {
            if (v == MCP_VERSION || v == Version.DEFAULT) {
                String fieldName = MAPPER.getShortObfFieldName(rf.name());
                fd.set(this, fieldName);
                Logger.log("Reflector ResolveField:", v, rf.name(), fieldName);
                return true;
            }
        }
        return false;
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

    public ScaledResolution getScaledResolution() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (MCP_VERSION == Version.MC1_7_10) {
                return ScaledResolution.class.getDeclaredConstructor(Minecraft.class, int.class, int.class).newInstance(mc, mc.displayWidth, mc.displayHeight);
            } else {
                return new ScaledResolution(mc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
