package pl.janek49.iniektor.client.modules.impl;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.Invoker;
import pl.janek49.iniektor.api.Keys;
import pl.janek49.iniektor.api.MinimumVersion;
import pl.janek49.iniektor.api.WrapperMisc;
import pl.janek49.iniektor.api.client.Minecraft;
import pl.janek49.iniektor.api.gui.ResourceLocation;
import pl.janek49.iniektor.client.modules.Module;

@MinimumVersion(version = Version.MC1_8_8)
public class LSD extends Module {
    public LSD() {
        super("LSD", Keys.KEY_L, Category.RENDER);
    }

    @Override
    public void onEnable() {
        getPlayer().addPotionEffect(9, Integer.MAX_VALUE);

        Invoker.fromObj(Minecraft.gameRenderer.get()).
                method(WrapperMisc.entityRenderer_LoadShader).exec(ResourceLocation.constructor.newInstance("shaders/post/wobble.json"));
    }

    @Override
    public void onDisable() {
        getPlayer().removePotionEffect(9);

        Invoker.fromObj(Minecraft.gameRenderer.get()).field(WrapperMisc.entityRenderer_TheShaderGroup).set(null);
    }
}
