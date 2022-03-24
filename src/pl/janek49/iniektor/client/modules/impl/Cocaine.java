package pl.janek49.iniektor.client.modules.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.client.config.Property;
import pl.janek49.iniektor.client.hook.Reflector;
import pl.janek49.iniektor.client.modules.Module;

public class Cocaine extends Module {

    public Property<Float> timerSpeed = new Property<Float>("speed", 2f, "Select the timer speed");

    public Cocaine() {
        super("Cocaine", Keyboard.KEY_C, Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        ((Timer)Reflector.MC.timer.get(Minecraft.getMinecraft())).timerSpeed = timerSpeed.getValue();
    }

    @Override
    public void onDisable() {
        ((Timer)Reflector.MC.timer.get(Minecraft.getMinecraft())).timerSpeed = 1;
    }
}
