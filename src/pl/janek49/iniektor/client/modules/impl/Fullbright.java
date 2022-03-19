package pl.janek49.iniektor.client.modules.impl;

import net.minecraft.potion.PotionEffect;
import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.hook.Reflector;
import pl.janek49.iniektor.client.modules.Module;

import static pl.janek49.iniektor.client.hook.MCC.PotionEffect;

public class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", Keyboard.KEY_B, Category.RENDER);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEvent(IEvent event) {
        if (Reflector.MCP_VERSION == Version.MC1_9_4) {
            getPlayer().addPotionEffect.call(PotionEffect.newInstance(getPlayer().getPotionById.invokeSt(16), Integer.MAX_VALUE));
            getPlayer().addPotionEffect.call(PotionEffect.newInstance(getPlayer().getPotionById.invokeSt(13), Integer.MAX_VALUE));
        } else {
            getPlayer().addPotionEffect.call(new PotionEffect(16, Integer.MAX_VALUE));
            getPlayer().addPotionEffect.call(new PotionEffect(13, Integer.MAX_VALUE));
        }
    }

    @Override
    public void onDisable() {
        if (Reflector.MCP_VERSION == Version.MC1_9_4) {
            getPlayer().removePotionEffect.call(getPlayer().getPotionById.invokeSt(16));
            getPlayer().removePotionEffect.call(getPlayer().getPotionById.invokeSt(13));
        } else {
            getPlayer().removePotionEffect.call(16);
            getPlayer().removePotionEffect.call(13);
        }

    }
}
