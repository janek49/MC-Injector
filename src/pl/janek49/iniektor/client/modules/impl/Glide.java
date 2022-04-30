package pl.janek49.iniektor.client.modules.impl;

import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.api.Keys;
import pl.janek49.iniektor.client.config.Property;
import pl.janek49.iniektor.client.config.RangeProperty;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class Glide extends Module implements EventHandler {
    public RangeProperty speed = new RangeProperty("speed", 0.01f, 0f, 0.3f, "Sinking speed multiplier");

    public Glide() {
        super("Glide", Keys.KEY_G, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEvent(IEvent event) {
        if (event instanceof EventGameTick) {
            getPlayer().setMotionY(-speed.getValue());
        }
    }
}
