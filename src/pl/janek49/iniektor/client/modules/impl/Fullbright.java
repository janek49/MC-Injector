package pl.janek49.iniektor.client.modules.impl;

import pl.janek49.iniektor.api.client.PotionEffect;
import pl.janek49.iniektor.api.reflection.Keys;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;


public class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", Keys.KEY_B, Category.RENDER);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEvent(IEvent event) {
        getPlayer().addPotionEffect(PotionEffect.NIGHT_VISION, 1200);
        getPlayer().addPotionEffect(PotionEffect.WATER_BREATHING, 1200);
    }

    @Override
    public void onDisable() {
        getPlayer().removePotionEffect(PotionEffect.NIGHT_VISION);
        getPlayer().removePotionEffect(PotionEffect.WATER_BREATHING);

    }
}
