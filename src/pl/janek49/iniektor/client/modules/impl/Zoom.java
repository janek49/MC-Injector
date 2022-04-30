package pl.janek49.iniektor.client.modules.impl;

import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.Keys;
import pl.janek49.iniektor.api.client.Minecraft;
import pl.janek49.iniektor.client.config.RangeProperty;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.api.Reflector;
import pl.janek49.iniektor.client.modules.Module;

public class Zoom extends Module {
    public Zoom() {
        super("Zoom", Keys.KEY_LMENU, Category.RENDER);
        RegisterEvent(EventGameTick.class);
    }

    private float fov;

    public RangeProperty factor = new RangeProperty("fov", Reflector.isOnOrBlwVersion(Version.MC1_6_4) ? 0 : 30f,
            Reflector.isOnOrBlwVersion(Version.MC1_6_4) ? 0 : 30f, Reflector.isOnOrBlwVersion(Version.MC1_6_4) ? 1f : 110f,"Zoom FOV");

    @Override
    public void onEnable() {
        fov = Minecraft.getGameSettings().getFOV();
        Minecraft.getGameSettings().setFOV(factor.getValue());
    }

    @Override
    public void onEvent(IEvent event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
            Minecraft.getGameSettings().setFOV(factor.getValue());
            return;
        }
        isEnabled = false;
        onDisable();
    }

    @Override
    public void onDisable() {
        Minecraft.getGameSettings().setFOV(fov);
    }
}
