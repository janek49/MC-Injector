package pl.janek49.iniektor.client.modules.impl;

import net.minecraft.potion.PotionEffect;
import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", Keyboard.KEY_B, Category.RENDER);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEvent(IEvent event) {
        getPlayer().addPotionEffect(new PotionEffect(16, Integer.MAX_VALUE));
        getPlayer().addPotionEffect(new PotionEffect(13, Integer.MAX_VALUE));
    }

    @Override
    public void onDisable() {
        getPlayer().removePotionEffect(16);
        getPlayer().removePotionEffect(13);
    }
}
