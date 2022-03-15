package pl.janek49.iniektor.client.modules.impl;

import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.client.hook.Reflector;
import pl.janek49.iniektor.client.modules.Module;

public class Cocaine extends Module {
    public Cocaine() {
        super("Cocaine", Keyboard.KEY_C, Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        Reflector.INSTANCE.minecraftTimer.timerSpeed = 2;
    }

    @Override
    public void onDisable() {
        Reflector.INSTANCE.minecraftTimer.timerSpeed = 1;
    }
}
