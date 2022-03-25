package pl.janek49.iniektor.client.modules.impl;

import net.minecraft.entity.player.PlayerCapabilities;
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

    private PlayerCapabilities capabilities;

    @Override
    public void onEnable() {
        capabilities = getPlayer().capabilities.get();
        stateBefore = capabilities.allowFlying;
        capabilities.allowFlying = true;
    }

    @Override
    public void onDisable() {
        capabilities.allowFlying = stateBefore;
        capabilities.isFlying = false;
    }

    @Override
    public void onEvent(IEvent event) {
        capabilities.isFlying = true;
    }
}
