package net.minecraft.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.BaseAttributeMap;

public class AbsoptionMobEffect extends MobEffect {
   protected AbsoptionMobEffect(MobEffectCategory mobEffectCategory, int var2) {
      super(mobEffectCategory, var2);
   }

   public void removeAttributeModifiers(LivingEntity livingEntity, BaseAttributeMap baseAttributeMap, int var3) {
      livingEntity.setAbsorptionAmount(livingEntity.getAbsorptionAmount() - (float)(4 * (var3 + 1)));
      super.removeAttributeModifiers(livingEntity, baseAttributeMap, var3);
   }

   public void addAttributeModifiers(LivingEntity livingEntity, BaseAttributeMap baseAttributeMap, int var3) {
      livingEntity.setAbsorptionAmount(livingEntity.getAbsorptionAmount() + (float)(4 * (var3 + 1)));
      super.addAttributeModifiers(livingEntity, baseAttributeMap, var3);
   }
}
