package pl.janek49.iniektor.client.modules.impl;

import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.client.config.Property;
import pl.janek49.iniektor.client.config.RangeProperty;
import pl.janek49.iniektor.client.modules.Module;

public class Cocaine extends Module {

    public RangeProperty timerSpeed = new RangeProperty("speed", 2f, 0.5f, 10f, "Select the timer speed");

    public Cocaine() {
        super("Cocaine", Keyboard.KEY_C, Category.WORLD);
    }

    @Override
    public void onEnable() {
        getMinecraft().setTimerSpeed(timerSpeed.getValue());
    }

    @Override
    public void onDisable() {
        getMinecraft().setTimerSpeed(1);
    }
}
