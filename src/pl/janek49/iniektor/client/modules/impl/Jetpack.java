package pl.janek49.iniektor.client.modules.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class Jetpack extends Module implements EventHandler {
    public Jetpack() {
        super("Jetpack", Keyboard.KEY_L, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onEvent(IEvent event) {
        if (event instanceof EventGameTick) {
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                if (getPlayer().motionY < 0)
                    getPlayer().motionY = 0.05;
                getPlayer().motionY += 0.05;
            }
        }
    }
}
