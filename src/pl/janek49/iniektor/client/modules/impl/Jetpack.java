package pl.janek49.iniektor.client.modules.impl;

import pl.janek49.iniektor.api.reflection.Keys;
import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.config.RangeProperty;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class Jetpack extends Module implements EventHandler {
    public RangeProperty speed = new RangeProperty("speed", 0.05f, 0.01f, 0.2f, "Speed multiplier");

    public Jetpack() {
        super("Jetpack", Keys.KEY_P, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEvent(IEvent event) {
        if (event instanceof EventGameTick) {
            if (IniektorClient.INSTANCE.keyboardHandler.isKeyDown(Keys.KEY_SPACE)) {
                if (getPlayer().getMotionY() < 0)
                    getPlayer().setMotionY(speed.getValue());
                getPlayer().setMotionY(getPlayer().getMotionY() + speed.getValue());
            }
        }
    }
}
