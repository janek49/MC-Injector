package pl.janek49.iniektor.client.hook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.util.Timer;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.mapper.Mapper;

import java.lang.reflect.Field;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.List;

public class Reflector {

    public static boolean TRIGGER_HOTSWAP = false;

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
        MAPPER = new Mapper(MCP_PATH);
        MAPPER.init();
        MCP_VERSION = Version.valueOf(MCP_VERSION_STRING);

        Wrappers = new ArrayList<>();

        Wrappers.add(Reflector.PLAYER = new WrapperPlayer());
        Wrappers.add(Reflector.MC = new WrapperMinecraft());

        for (IWrapper wrapper : Wrappers) {
            for (Field fd : wrapper.getClass().getDeclaredFields()) {
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
            try {
                wrapper.initWrapper();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

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
