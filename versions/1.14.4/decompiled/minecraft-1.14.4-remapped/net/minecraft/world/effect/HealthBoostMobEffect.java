package net.minecraft.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.BaseAttributeMap;

public class HealthBoostMobEffect extends MobEffect {
   public HealthBoostMobEffect(MobEffectCategory mobEffectCategory, int var2) {
      super(mobEffectCategory, var2);
   }

   public void removeAttributeModifiers(LivingEntity livingEntity, BaseAttributeMap baseAttributeMap, int var3) {
      super.removeAttributeModifiers(livingEntity, baseAttributeMap, var3);
      if(livingEntity.getHealth() > livingEntity.getMaxHealth()) {
         livingEntity.setHealth(livingEntity.getMaxHealth());
      }

   }
}
