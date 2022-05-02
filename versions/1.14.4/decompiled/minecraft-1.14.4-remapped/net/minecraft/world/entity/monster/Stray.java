package net.minecraft.world.entity.monster;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class Stray extends AbstractSkeleton {
   public Stray(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public static boolean checkStraySpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      return checkMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, random) && (mobSpawnType == MobSpawnType.SPAWNER || levelAccessor.canSeeSky(blockPos));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.STRAY_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.STRAY_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.STRAY_DEATH;
   }

   SoundEvent getStepSound() {
      return SoundEvents.STRAY_STEP;
   }

   protected AbstractArrow getArrow(ItemStack itemStack, float var2) {
      AbstractArrow abstractArrow = super.getArrow(itemStack, var2);
      if(abstractArrow instanceof Arrow) {
         ((Arrow)abstractArrow).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600));
      }

      return abstractArrow;
   }
}
