package net.minecraft.world.entity.animal;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PolarBear extends Animal {
   private static final EntityDataAccessor DATA_STANDING_ID = SynchedEntityData.defineId(PolarBear.class, EntityDataSerializers.BOOLEAN);
   private float clientSideStandAnimationO;
   private float clientSideStandAnimation;
   private int warningSoundTicks;

   public PolarBear(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public AgableMob getBreedOffspring(AgableMob agableMob) {
      return (AgableMob)EntityType.POLAR_BEAR.create(this.level);
   }

   public boolean isFood(ItemStack itemStack) {
      return false;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PolarBear.PolarBearMeleeAttackGoal());
      this.goalSelector.addGoal(1, new PolarBear.PolarBearPanicGoal());
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
      this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new PolarBear.PolarBearHurtByTargetGoal());
      this.targetSelector.addGoal(2, new PolarBear.PolarBearAttackPlayersGoal());
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Fox.class, 10, true, true, (Predicate)null));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
   }

   public static boolean checkPolarBearSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      Biome var5 = levelAccessor.getBiome(blockPos);
      return var5 != Biomes.FROZEN_OCEAN && var5 != Biomes.DEEP_FROZEN_OCEAN?checkAnimalSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, random):levelAccessor.getRawBrightness(blockPos, 0) > 8 && levelAccessor.getBlockState(blockPos.below()).getBlock() == Blocks.ICE;
   }

   protected SoundEvent getAmbientSound() {
      return this.isBaby()?SoundEvents.POLAR_BEAR_AMBIENT_BABY:SoundEvents.POLAR_BEAR_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.POLAR_BEAR_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.POLAR_BEAR_DEATH;
   }

   protected void playStepSound(BlockPos blockPos, BlockState blockState) {
      this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15F, 1.0F);
   }

   protected void playWarningSound() {
      if(this.warningSoundTicks <= 0) {
         this.playSound(SoundEvents.POLAR_BEAR_WARNING, 1.0F, this.getVoicePitch());
         this.warningSoundTicks = 40;
      }

   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_STANDING_ID, Boolean.valueOf(false));
   }

   public void tick() {
      super.tick();
      if(this.level.isClientSide) {
         if(this.clientSideStandAnimation != this.clientSideStandAnimationO) {
            this.refreshDimensions();
         }

         this.clientSideStandAnimationO = this.clientSideStandAnimation;
         if(this.isStanding()) {
            this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, 6.0F);
         } else {
            this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, 6.0F);
         }
      }

      if(this.warningSoundTicks > 0) {
         --this.warningSoundTicks;
      }

   }

   public EntityDimensions getDimensions(Pose pose) {
      if(this.clientSideStandAnimation > 0.0F) {
         float var2 = this.clientSideStandAnimation / 6.0F;
         float var3 = 1.0F + var2;
         return super.getDimensions(pose).scale(1.0F, var3);
      } else {
         return super.getDimensions(pose);
      }
   }

   public boolean doHurtTarget(Entity entity) {
      boolean var2 = entity.hurt(DamageSource.mobAttack(this), (float)((int)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue()));
      if(var2) {
         this.doEnchantDamageEffects(this, entity);
      }

      return var2;
   }

   public boolean isStanding() {
      return ((Boolean)this.entityData.get(DATA_STANDING_ID)).booleanValue();
   }

   public void setStanding(boolean standing) {
      this.entityData.set(DATA_STANDING_ID, Boolean.valueOf(standing));
   }

   public float getStandingAnimationScale(float f) {
      return Mth.lerp(f, this.clientSideStandAnimationO, this.clientSideStandAnimation) / 6.0F;
   }

   protected float getWaterSlowDown() {
      return 0.98F;
   }

   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      if(var4 instanceof PolarBear.PolarBearGroupData) {
         this.setAge(-24000);
      } else {
         var4 = new PolarBear.PolarBearGroupData();
      }

      return (SpawnGroupData)var4;
   }

   class PolarBearAttackPlayersGoal extends NearestAttackableTargetGoal {
      public PolarBearAttackPlayersGoal() {
         super(PolarBear.this, Player.class, 20, true, true, (Predicate)null);
      }

      public boolean canUse() {
         if(PolarBear.this.isBaby()) {
            return false;
         } else {
            if(super.canUse()) {
               for(PolarBear var3 : PolarBear.this.level.getEntitiesOfClass(PolarBear.class, PolarBear.this.getBoundingBox().inflate(8.0D, 4.0D, 8.0D))) {
                  if(var3.isBaby()) {
                     return true;
                  }
               }
            }

            return false;
         }
      }

      protected double getFollowDistance() {
         return super.getFollowDistance() * 0.5D;
      }
   }

   static class PolarBearGroupData implements SpawnGroupData {
      private PolarBearGroupData() {
      }
   }

   class PolarBearHurtByTargetGoal extends HurtByTargetGoal {
      public PolarBearHurtByTargetGoal() {
         super(PolarBear.this, new Class[0]);
      }

      public void start() {
         super.start();
         if(PolarBear.this.isBaby()) {
            this.alertOthers();
            this.stop();
         }

      }

      protected void alertOther(Mob mob, LivingEntity livingEntity) {
         if(mob instanceof PolarBear && !mob.isBaby()) {
            super.alertOther(mob, livingEntity);
         }

      }
   }

   class PolarBearMeleeAttackGoal extends MeleeAttackGoal {
      public PolarBearMeleeAttackGoal() {
         super(PolarBear.this, 1.25D, true);
      }

      protected void checkAndPerformAttack(LivingEntity livingEntity, double var2) {
         double var4 = this.getAttackReachSqr(livingEntity);
         if(var2 <= var4 && this.attackTime <= 0) {
            this.attackTime = 20;
            this.mob.doHurtTarget(livingEntity);
            PolarBear.this.setStanding(false);
         } else if(var2 <= var4 * 2.0D) {
            if(this.attackTime <= 0) {
               PolarBear.this.setStanding(false);
               this.attackTime = 20;
            }

            if(this.attackTime <= 10) {
               PolarBear.this.setStanding(true);
               PolarBear.this.playWarningSound();
            }
         } else {
            this.attackTime = 20;
            PolarBear.this.setStanding(false);
         }

      }

      public void stop() {
         PolarBear.this.setStanding(false);
         super.stop();
      }

      protected double getAttackReachSqr(LivingEntity livingEntity) {
         return (double)(4.0F + livingEntity.getBbWidth());
      }
   }

   class PolarBearPanicGoal extends PanicGoal {
      public PolarBearPanicGoal() {
         super(PolarBear.this, 2.0D);
      }

      public boolean canUse() {
         return !PolarBear.this.isBaby() && !PolarBear.this.isOnFire()?false:super.canUse();
      }
   }
}
