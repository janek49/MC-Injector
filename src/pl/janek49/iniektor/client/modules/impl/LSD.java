package pl.janek49.iniektor.client.modules.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.hook.MCC;
import pl.janek49.iniektor.client.hook.Reflector;
import pl.janek49.iniektor.client.hook.MiscFunctions;
import pl.janek49.iniektor.client.modules.Module;

public class LSD extends Module {
    public LSD() {
        super("LSD", Keyboard.KEY_L, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEvent(IEvent event) {

    }

    @Override
    public void onEnable() {
        if (Reflector.MCP_VERSION.ordinal() > Version.MC1_8_8.ordinal()) {
            getPlayer().addPotionEffect.call(MCC.PotionEffect.newInstance(MiscFunctions.getPotionById.invokeSt(9), Integer.MAX_VALUE));
        } else {
            getPlayer().addPotionEffect.call(MCC.PotionEffect.newInstance(9, Integer.MAX_VALUE));

            MiscFunctions.entityRenderer_LoadShader.invoke(Minecraft.getMinecraft().entityRenderer, new ResourceLocation("shaders/post/wobble.json"));
        }
    }

    @Override
    public void onDisable() {
        if (Reflector.MCP_VERSION.ordinal() > Version.MC1_8_8.ordinal()) {
            getPlayer().removePotionEffect.call(MiscFunctions.getPotionById.invokeSt(9));
        } else {
            getPlayer().removePotionEffect.call(9);

            MiscFunctions.entityRenderer_TheShaderGroup.set(Minecraft.getMinecraft().entityRenderer, null);
        }
    }
}
