package pl.janek49.iniektor.client.hook;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

import java.lang.reflect.Field;

public class Reflector {

    @ResolveFieldName
    public static String FIELD_MC_TIMER = "net/minecraft/client/Minecraft/timer";

    public static Timer getMcTimer() {
        try {
            Field fd = Minecraft.class.getDeclaredField(FIELD_MC_TIMER);
            fd.setAccessible(true);
            Timer timer = (Timer) fd.get(Minecraft.getMinecraft());
            return timer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
