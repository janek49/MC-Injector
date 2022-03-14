package pl.janek49.iniektor.client.modules.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class VanillaFly extends Module implements EventHandler {

    public boolean stateBefore;

    public VanillaFly() {
        super("VanillaFly", Keyboard.KEY_F, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEnable() {
        stateBefore = getPlayer().capabilities.allowFlying;
        getPlayer().capabilities.allowFlying = true;
    }

    @Override
    public void onDisable() {
        getPlayer().capabilities.allowFlying = stateBefore;
        getPlayer().capabilities.isFlying = false;
    }

    @Override
    public void onEvent(IEvent event) {
        if (event instanceof EventGameTick) {
            getPlayer().capabilities.isFlying = true;
        }
    }
}
