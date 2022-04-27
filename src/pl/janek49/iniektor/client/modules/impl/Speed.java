package pl.janek49.iniektor.client.modules.impl;

import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class Speed extends Module implements EventHandler {

    public Speed() {
        super("Speed", Keyboard.KEY_H, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEvent(IEvent event) {
        if (event instanceof EventGameTick) {
            if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
                if (getPlayer().isOnGround()) {
                    getPlayer().setMotionX(getPlayer().getMotionX() * 1.2);
                    getPlayer().setMotionZ(getPlayer().getMotionZ() * 1.2);
                }
            }
        }
    }
}
