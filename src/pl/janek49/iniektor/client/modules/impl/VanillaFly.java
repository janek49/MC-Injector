package pl.janek49.iniektor.client.modules.impl;

import pl.janek49.iniektor.api.client.PlayerCapabilities;
import pl.janek49.iniektor.api.reflection.Keys;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class VanillaFly extends Module implements EventHandler {

    public boolean stateBefore;

    public VanillaFly() {
        super("VanillaFly", Keys.KEY_F, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    private PlayerCapabilities capabilities;

    @Override
    public void onEnable() {
        capabilities = getPlayer().getCapabilities();
        stateBefore = capabilities.getAllowFlying();
        capabilities.setAllowFlying(true);
    }

    @Override
    public void onDisable() {
        capabilities.setAllowFlying(stateBefore);
        capabilities.setIsFlying(false);
    }

    @Override
    public void onEvent(IEvent event) {
        capabilities.setIsFlying(true);
    }
}
