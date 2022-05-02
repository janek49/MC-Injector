package net.minecraft.world.entity.monster;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;

public class MagmaCube extends Slime {
   public MagmaCube(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
   }

   public static boolean checkMagmaCubeSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      return levelAccessor.getDifficulty() != Difficulty.PEACEFUL;
   }

   public boolean checkSpawnObstruction(LevelReader levelReader) {
      return levelReader.isUnobstructed(this) && !levelReader.containsAnyLiquid(this.getBoundingBox());
   }

   protected void setSize(int var1, boolean var2) {
      super.setSize(var1, var2);
      this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue((double)(var1 * 3));
   }

   public int getLightColor() {
      return 15728880;
   }

   public float getBrightness() {
      return 1.0F;
   }

   protected ParticleOptions getParticleType() {
      return ParticleTypes.FLAME;
   }

   protected ResourceLocation getDefaultLootTable() {
      return this.isTiny()?BuiltInLootTables.EMPTY:this.getType().getDefaultLootTable();
   }

   public boolean isOnFire() {
      return false;
   }

   protected int getJumpDelay() {
      return super.getJumpDelay() * 4;
   }

   protected void decreaseSquish() {
      this.targetSquish *= 0.9F;
   }

   protected void jumpFromGround() {
      Vec3 var1 = this.getDeltaMovement();
      this.setDeltaMovement(var1.x, (double)(0.42F + (float)this.getSize() * 0.1F), var1.z);
      this.hasImpulse = true;
   }

   protected void jumpInLiquid(Tag tag) {
      if(tag == FluidTags.LAVA) {
         Vec3 var2 = this.getDeltaMovement();
         this.setDeltaMovement(var2.x, (double)(0.22F + (float)this.getSize() * 0.05F), var2.z);
         this.hasImpulse = true;
      } else {
         super.jumpInLiquid(tag);
      }

   }

   public void causeFallDamage(float var1, float var2) {
   }

   protected boolean isDealsDamage() {
      return this.isEffectiveAi();
   }

   protected int getAttackDamage() {
      return super.getAttackDamage() + 2;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return this.isTiny()?SoundEvents.MAGMA_CUBE_HURT_SMALL:SoundEvents.MAGMA_CUBE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isTiny()?SoundEvents.MAGMA_CUBE_DEATH_SMALL:SoundEvents.MAGMA_CUBE_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return this.isTiny()?SoundEvents.MAGMA_CUBE_SQUISH_SMALL:SoundEvents.MAGMA_CUBE_SQUISH;
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.MAGMA_CUBE_JUMP;
   }
}
