package pl.janek49.iniektor.client.modules.impl;

import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.api.Keys;
import pl.janek49.iniektor.client.config.Property;
import pl.janek49.iniektor.client.config.RangeProperty;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class Jesus extends Module {

    public enum JesusMode {
        FLAT, JUMP, SPRINT
    }

    public Property<JesusMode> mode = new Property<>("mode", JesusMode.JUMP, "Jesus mode (flat, jump, sprint)");

    public RangeProperty divespeed = new RangeProperty("divespeed", 0.3f, 0.1f,2f,"Diving speed when pressing shift");
    public RangeProperty jumpspeed = new RangeProperty("jumpspeed", 0.1f, 0.05f, 1f, "Jump mode speed");
    public Property<Boolean> autosprint = new Property<>("autosprint", false, "Sprint mode autopilot");

    public Jesus() {
        super("Jezus", Keys.KEY_J, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEvent(IEvent event) {
        if (getPlayer().isInWater()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                getPlayer().setMotionY(-divespeed.getValue());
                return;
            }
            if (mode.getValue() == JesusMode.JUMP) {
                getPlayer().setMotionY(jumpspeed.getValue());
            } else if (mode.getValue() == JesusMode.FLAT) {
                getPlayer().setMotionY(0);
            } else if (mode.getValue() == JesusMode.SPRINT) {
                if (autosprint.getValue())
                    getPlayer().setSprinting(true);
                getPlayer().jump();
            }
        }
    }
}
