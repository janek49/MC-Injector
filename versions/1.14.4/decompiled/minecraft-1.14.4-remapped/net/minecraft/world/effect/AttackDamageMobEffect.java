package net.minecraft.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttackDamageMobEffect extends MobEffect {
   protected final double multiplier;

   protected AttackDamageMobEffect(MobEffectCategory mobEffectCategory, int var2, double multiplier) {
      super(mobEffectCategory, var2);
      this.multiplier = multiplier;
   }

   public double getAttributeModifierValue(int var1, AttributeModifier attributeModifier) {
      return this.multiplier * (double)(var1 + 1);
   }
}
