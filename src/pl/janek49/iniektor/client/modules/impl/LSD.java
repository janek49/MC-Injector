package pl.janek49.iniektor.client.modules.impl;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.client.PotionEffect;
import pl.janek49.iniektor.api.reflection.Invoker;
import pl.janek49.iniektor.api.reflection.Keys;
import pl.janek49.iniektor.api.reflection.MinimumVersion;
import pl.janek49.iniektor.api.wrapper.WrapperMisc;
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
        getPlayer().addPotionEffect(PotionEffect.NAUSEA, Integer.MAX_VALUE);

        Invoker.fromObj(Minecraft.gameRenderer.get()).
                method(WrapperMisc.entityRenderer_LoadShader).exec(ResourceLocation.constructor.newInstance("shaders/post/wobble.json"));
    }

    @Override
    public void onDisable() {
        getPlayer().removePotionEffect(PotionEffect.NAUSEA);

        Invoker.fromObj(Minecraft.gameRenderer.get()).field(WrapperMisc.entityRenderer_TheShaderGroup).set(null);
    }
}
