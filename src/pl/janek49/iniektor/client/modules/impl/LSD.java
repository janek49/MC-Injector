package pl.janek49.iniektor.client.modules.impl;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.Invoker;
import pl.janek49.iniektor.api.MinimumVersion;
import pl.janek49.iniektor.api.WrapperMisc;
import pl.janek49.iniektor.client.modules.Module;

@MinimumVersion(version = Version.MC1_8_8)
public class LSD extends Module {
    public LSD() {
        super("LSD", Keyboard.KEY_L, Category.RENDER);
    }

    @Override
    public void onEnable() {
        getPlayer().addPotionEffect(9, Integer.MAX_VALUE);

        Invoker.fromObj(getMinecraft().getInstance().entityRenderer).
                method(WrapperMisc.entityRenderer_LoadShader).exec(new ResourceLocation("shaders/post/wobble.json"));
    }

    @Override
    public void onDisable() {
        getPlayer().removePotionEffect(9);

        Invoker.fromObj(getMinecraft().getInstance().entityRenderer).field(WrapperMisc.entityRenderer_TheShaderGroup).set(null);
    }
}
