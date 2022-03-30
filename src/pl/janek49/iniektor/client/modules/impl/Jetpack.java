package pl.janek49.iniektor.client.modules.impl;

import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.client.config.Property;
import pl.janek49.iniektor.client.config.RangeProperty;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class Jetpack extends Module implements EventHandler {
    public RangeProperty speed = new RangeProperty("speed", 0.05f, 0.01f, 0.2f, "Speed multiplier");

    public Jetpack() {
        super("Jetpack", Keyboard.KEY_P, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEvent(IEvent event) {
        if (event instanceof EventGameTick) {
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                if (getPlayerObj().motionY < 0)
                    getPlayerObj().motionY = speed.getValue();
                getPlayerObj().motionY += speed.getValue();
            }
        }
    }
}
