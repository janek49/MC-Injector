package net.minecraft.world.effect;

import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public final class MobEffectUtil {
   public static String formatDuration(MobEffectInstance mobEffectInstance, float var1) {
      if(mobEffectInstance.isNoCounter()) {
         return "**:**";
      } else {
         int var2 = Mth.floor((float)mobEffectInstance.getDuration() * var1);
         return StringUtil.formatTickDuration(var2);
      }
   }

   public static boolean hasDigSpeed(LivingEntity livingEntity) {
      return livingEntity.hasEffect(MobEffects.DIG_SPEED) || livingEntity.hasEffect(MobEffects.CONDUIT_POWER);
   }

   public static int getDigSpeedAmplification(LivingEntity livingEntity) {
      int var1 = 0;
      int var2 = 0;
      if(livingEntity.hasEffect(MobEffects.DIG_SPEED)) {
         var1 = livingEntity.getEffect(MobEffects.DIG_SPEED).getAmplifier();
      }

      if(livingEntity.hasEffect(MobEffects.CONDUIT_POWER)) {
         var2 = livingEntity.getEffect(MobEffects.CONDUIT_POWER).getAmplifier();
      }

      return Math.max(var1, var2);
   }

   public static boolean hasWaterBreathing(LivingEntity livingEntity) {
      return livingEntity.hasEffect(MobEffects.WATER_BREATHING) || livingEntity.hasEffect(MobEffects.CONDUIT_POWER);
   }
}
