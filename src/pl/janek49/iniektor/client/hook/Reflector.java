package pl.janek49.iniektor.client.hook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Timer;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.mapper.Mapper;

import java.lang.reflect.Field;

public class Reflector {

    public static Version MCP_VERSION;
    public static String MCP_VERSION_STRING;
    public static String MCP_PATH;
    public static Mapper MAPPER;
    public static Reflector INSTANCE;

    public Reflector() {
        INSTANCE = this;
        MAPPER = new Mapper(MCP_PATH);
        MAPPER.init();
        MCP_VERSION = Version.valueOf(MCP_VERSION_STRING);

        minecraftTimer = getPrivateFieldValue(Minecraft.class, Minecraft.getMinecraft(), MAPPER.getShortObfFieldName("net/minecraft/client/Minecraft/timer"));

        String fontRendererDeobf = MCP_VERSION == Version.MC1_7_10 ? "fontRenderer" : "fontRendererObj";
        fontRenderer = getDeclaredFieldValue(Minecraft.class, Minecraft.getMinecraft(), MAPPER.getShortObfFieldName("net/minecraft/client/Minecraft/" + fontRendererDeobf));
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

    public Timer minecraftTimer;
    public FontRenderer fontRenderer;

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
