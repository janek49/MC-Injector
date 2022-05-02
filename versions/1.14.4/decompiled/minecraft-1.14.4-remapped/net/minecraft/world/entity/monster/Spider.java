package net.minecraft.world.entity.monster;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Spider extends Monster {
   private static final EntityDataAccessor DATA_FLAGS_ID = SynchedEntityData.defineId(Spider.class, EntityDataSerializers.BYTE);

   public Spider(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(4, new Spider.SpiderAttackGoal(this));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new Spider.SpiderTargetGoal(this, Player.class));
      this.targetSelector.addGoal(3, new Spider.SpiderTargetGoal(this, IronGolem.class));
   }

   public double getRideHeight() {
      return (double)(this.getBbHeight() * 0.5F);
   }

   protected PathNavigation createNavigation(Level level) {
      return new WallClimberNavigation(this, level);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, Byte.valueOf((byte)0));
   }

   public void tick() {
      super.tick();
      if(!this.level.isClientSide) {
         this.setClimbing(this.horizontalCollision);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SPIDER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.SPIDER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SPIDER_DEATH;
   }

   protected void playStepSound(BlockPos blockPos, BlockState blockState) {
      this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
   }

   public boolean onLadder() {
      return this.isClimbing();
   }

   public void makeStuckInBlock(BlockState blockState, Vec3 vec3) {
      if(blockState.getBlock() != Blocks.COBWEB) {
         super.makeStuckInBlock(blockState, vec3);
      }

   }

   public MobType getMobType() {
      return MobType.ARTHROPOD;
   }

   public boolean canBeAffected(MobEffectInstance mobEffectInstance) {
      return mobEffectInstance.getEffect() == MobEffects.POISON?false:super.canBeAffected(mobEffectInstance);
   }

   public boolean isClimbing() {
      return (((Byte)this.entityData.get(DATA_FLAGS_ID)).byteValue() & 1) != 0;
   }

   public void setClimbing(boolean climbing) {
      byte var2 = ((Byte)this.entityData.get(DATA_FLAGS_ID)).byteValue();
      if(climbing) {
         var2 = (byte)(var2 | 1);
      } else {
         var2 = (byte)(var2 & -2);
      }

      this.entityData.set(DATA_FLAGS_ID, Byte.valueOf(var2));
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      var4 = super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
      if(levelAccessor.getRandom().nextInt(100) == 0) {
         Skeleton var6 = (Skeleton)EntityType.SKELETON.create(this.level);
         var6.moveTo(this.x, this.y, this.z, this.yRot, 0.0F);
         var6.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, (SpawnGroupData)null, (CompoundTag)null);
         levelAccessor.addFreshEntity(var6);
         var6.startRiding(this);
      }

      if(var4 == null) {
         var4 = new Spider.SpiderEffectsGroupData();
         if(levelAccessor.getDifficulty() == Difficulty.HARD && levelAccessor.getRandom().nextFloat() < 0.1F * difficultyInstance.getSpecialMultiplier()) {
            ((Spider.SpiderEffectsGroupData)var4).setRandomEffect(levelAccessor.getRandom());
         }
      }

      if(var4 instanceof Spider.SpiderEffectsGroupData) {
         MobEffect var6 = ((Spider.SpiderEffectsGroupData)var4).effect;
         if(var6 != null) {
            this.addEffect(new MobEffectInstance(var6, Integer.MAX_VALUE));
         }
      }

      return var4;
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return 0.65F;
   }

   static class SpiderAttackGoal extends MeleeAttackGoal {
      public SpiderAttackGoal(Spider spider) {
         super(spider, 1.0D, true);
      }

      public boolean canUse() {
         return super.canUse() && !this.mob.isVehicle();
      }

      public boolean canContinueToUse() {
         float var1 = this.mob.getBrightness();
         if(var1 >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
            this.mob.setTarget((LivingEntity)null);
            return false;
         } else {
            return super.canContinueToUse();
         }
      }

      protected double getAttackReachSqr(LivingEntity livingEntity) {
         return (double)(4.0F + livingEntity.getBbWidth());
      }
   }

   public static class SpiderEffectsGroupData implements SpawnGroupData {
      public MobEffect effect;

      public void setRandomEffect(Random randomEffect) {
         int var2 = randomEffect.nextInt(5);
         if(var2 <= 1) {
            this.effect = MobEffects.MOVEMENT_SPEED;
         } else if(var2 <= 2) {
            this.effect = MobEffects.DAMAGE_BOOST;
         } else if(var2 <= 3) {
            this.effect = MobEffects.REGENERATION;
         } else if(var2 <= 4) {
            this.effect = MobEffects.INVISIBILITY;
         }

      }
   }

   static class SpiderTargetGoal extends NearestAttackableTargetGoal {
      public SpiderTargetGoal(Spider spider, Class class) {
         super(spider, class, true);
      }

      public boolean canUse() {
         float var1 = this.mob.getBrightness();
         return var1 >= 0.5F?false:super.canUse();
      }
   }
}
