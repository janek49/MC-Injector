package net.minecraft.world.entity.monster;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class Endermite extends Monster {
   private int life;
   private boolean playerSpawned;

   public Endermite(EntityType entityType, Level level) {
      super(entityType, level);
      this.xpReward = 3;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return 0.1F;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
   }

   protected boolean makeStepSound() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENDERMITE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.ENDERMITE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENDERMITE_DEATH;
   }

   protected void playStepSound(BlockPos blockPos, BlockState blockState) {
      this.playSound(SoundEvents.ENDERMITE_STEP, 0.15F, 1.0F);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.life = compoundTag.getInt("Lifetime");
      this.playerSpawned = compoundTag.getBoolean("PlayerSpawned");
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("Lifetime", this.life);
      compoundTag.putBoolean("PlayerSpawned", this.playerSpawned);
   }

   public void tick() {
      this.yBodyRot = this.yRot;
      super.tick();
   }

   public void setYBodyRot(float yBodyRot) {
      this.yRot = yBodyRot;
      super.setYBodyRot(yBodyRot);
   }

   public double getRidingHeight() {
      return 0.1D;
   }

   public boolean isPlayerSpawned() {
      return this.playerSpawned;
   }

   public void setPlayerSpawned(boolean playerSpawned) {
      this.playerSpawned = playerSpawned;
   }

   public void aiStep() {
      super.aiStep();
      if(this.level.isClientSide) {
         for(int var1 = 0; var1 < 2; ++var1) {
            this.level.addParticle(ParticleTypes.PORTAL, this.x + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth(), this.y + this.random.nextDouble() * (double)this.getBbHeight(), this.z + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth(), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
         }
      } else {
         if(!this.isPersistenceRequired()) {
            ++this.life;
         }

         if(this.life >= 2400) {
            this.remove();
         }
      }

   }

   public static boolean checkEndermiteSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      if(checkAnyLightMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, random)) {
         Player var5 = levelAccessor.getNearestPlayer((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, 5.0D, true);
         return var5 == null;
      } else {
         return false;
      }
   }

   public MobType getMobType() {
      return MobType.ARTHROPOD;
   }
}
