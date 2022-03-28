package pl.janek49.iniektor.client.modules.impl;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.client.config.Property;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.hook.Reflector;
import pl.janek49.iniektor.client.modules.Module;

public class Zoom extends Module {
    public Zoom() {
        super("Zoom", Keyboard.KEY_LMENU, Category.RENDER);
        RegisterEvent(EventGameTick.class);
    }

    private float fov;

    public Property<Float> factor = new Property<>("fov", Reflector.isOnOrBlwVersion(Version.MC1_6_4) ? 0 : 30f, "Zoom FOV");

    @Override
    public void onEnable() {
        fov = Minecraft.getMinecraft().gameSettings.fovSetting;
        Minecraft.getMinecraft().gameSettings.fovSetting = factor.getValue();
    }

    @Override
    public void onEvent(IEvent event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
            Minecraft.getMinecraft().gameSettings.fovSetting = factor.getValue();
            return;
        }
        isEnabled = false;
        onDisable();
    }

    @Override
    public void onDisable() {
        Minecraft.getMinecraft().gameSettings.fovSetting = fov;
    }
}
