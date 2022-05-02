package net.minecraft.world.entity.monster;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class Husk extends Zombie {
   public Husk(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public static boolean checkHuskSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      return checkMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, random) && (mobSpawnType == MobSpawnType.SPAWNER || levelAccessor.canSeeSky(blockPos));
   }

   protected boolean isSunSensitive() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.HUSK_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.HUSK_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.HUSK_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.HUSK_STEP;
   }

   public boolean doHurtTarget(Entity entity) {
      boolean var2 = super.doHurtTarget(entity);
      if(var2 && this.getMainHandItem().isEmpty() && entity instanceof LivingEntity) {
         float var3 = this.level.getCurrentDifficultyAt(new BlockPos(this)).getEffectiveDifficulty();
         ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.HUNGER, 140 * (int)var3));
      }

      return var2;
   }

   protected boolean convertsInWater() {
      return true;
   }

   protected void doUnderWaterConversion() {
      this.convertTo(EntityType.ZOMBIE);
      this.level.levelEvent((Player)null, 1041, new BlockPos(this), 0);
   }

   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }
}
