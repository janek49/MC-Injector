package pl.janek49.iniektor.client.modules.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.hook.Reflector;
import pl.janek49.iniektor.client.hook.WrapperPlayer;
import pl.janek49.iniektor.client.modules.Module;

import java.sql.Ref;

public class VanillaFly extends Module implements EventHandler {

    public boolean stateBefore;

    public VanillaFly() {
        super("VanillaFly", Keyboard.KEY_F, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    private PlayerCapabilities caps;

    @Override
    public void onEnable() {
        caps = Reflector.PLAYER.getPlayerCapabilities();
        stateBefore = caps.allowFlying;
        caps.allowFlying = true;
    }

    @Override
    public void onDisable() {
        caps.allowFlying = stateBefore;
        caps.isFlying = false;
    }

    @Override
    public void onEvent(IEvent event) {
        if (event instanceof EventGameTick) {
            caps.isFlying = true;
        }
    }
}
