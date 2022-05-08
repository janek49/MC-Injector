package pl.janek49.iniektor.client.modules.impl;

import pl.janek49.iniektor.api.client.PotionEffect;
import pl.janek49.iniektor.api.reflection.Keys;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class AntiPotion extends Module {
    public AntiPotion() {
        super("AntiPotion", Keys.KEY_NONE, Category.PLAYER);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEvent(IEvent event) {
        getPlayer().removePotionEffect(PotionEffect.NAUSEA);
        getPlayer().removePotionEffect(PotionEffect.BLINDNESS);
        getPlayer().removePotionEffect(PotionEffect.DIG_SLOWDOWN);
        getPlayer().removePotionEffect(PotionEffect.MOVEMENT_SLOWDOWN);
        getPlayer().removePotionEffect(PotionEffect.LEVITATION);
        getPlayer().removePotionEffect(PotionEffect.SLOW_FALLING);
    }
}
