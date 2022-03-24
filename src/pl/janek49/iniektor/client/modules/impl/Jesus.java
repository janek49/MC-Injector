package pl.janek49.iniektor.client.modules.impl;

import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.client.config.Property;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class Jesus extends Module {

    public enum JesusMode {
        FLAT, JUMP, SPRINT
    }

    public Property<JesusMode> mode = new Property<>("mode", JesusMode.JUMP, "Jesus mode (flat, jump, sprint)");

    public Property<Float> divespeed = new Property<>("divespeed", 0.3f, "Diving speed when pressing shift");
    public Property<Float> jumpspeed = new Property<>("jumpspeed", 0.1f, "Jump mode speed");
    public Property<Boolean> autosprint = new Property<>("autosprint", false, "Sprint mode autopilot");

    public Jesus() {
        super("Jezus", Keyboard.KEY_J, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEvent(IEvent event) {
        if (getPlayerObj().isInWater()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                getPlayerObj().motionY = -divespeed.getValue();
                return;
            }
            if (mode.getValue() == JesusMode.JUMP) {
                getPlayerObj().motionY = jumpspeed.getValue();
            } else if (mode.getValue() == JesusMode.FLAT) {
                getPlayerObj().motionY = 0;
            } else if (mode.getValue() == JesusMode.SPRINT) {
                if (autosprint.getValue())
                    getPlayerObj().setSprinting(true);
                getPlayer().jump.call();
            }
        }
    }
}
