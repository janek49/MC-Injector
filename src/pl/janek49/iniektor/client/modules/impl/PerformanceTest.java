package pl.janek49.iniektor.client.modules.impl;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.api.Reflector;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventRender2D;
import pl.janek49.iniektor.client.modules.Module;

public class PerformanceTest extends Module {
    public PerformanceTest() {
        super("PerformanceTest", Keyboard.KEY_NONE, Category.MISC);
        RegisterEvent(EventRender2D.class);
    }

    @Override
    public void onEvent(IEvent event) {

        long now = System.nanoTime();

        for (int i = 0; i < 100; i++) {
            Object o = Reflector.PLAYER.getInstance();
        }

        long reflection = System.nanoTime();

        for (int i = 0; i < 100; i++) {
            Object o = Minecraft.getMinecraft().thePlayer;
        }

        long direct = System.nanoTime();

        for (int i = 0; i < 100; i++) {
            //Object o = Test.getPlayer(Minecraft.getMinecraft());
        }

        long end = System.nanoTime();

        long msRefl = reflection - now;
        long msDirect = direct - reflection;
        long msInj = end - direct;
        long total = end - now;

        Logger.log(String.format("Total: %s | Reflection: %s | Direct: %s | Injected: %s", total, msRefl, msDirect, msInj));

    }
}
