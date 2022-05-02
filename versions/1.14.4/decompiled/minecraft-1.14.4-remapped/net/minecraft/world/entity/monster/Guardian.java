package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

public class Guardian extends Monster {
   private static final EntityDataAccessor DATA_ID_MOVING = SynchedEntityData.defineId(Guardian.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor DATA_ID_ATTACK_TARGET = SynchedEntityData.defineId(Guardian.class, EntityDataSerializers.INT);
   protected float clientSideTailAnimation;
   protected float clientSideTailAnimationO;
   protected float clientSideTailAnimationSpeed;
   protected float clientSideSpikesAnimation;
   protected float clientSideSpikesAnimationO;
   private LivingEntity clientSideCachedAttackTarget;
   private int clientSideAttackTime;
   private boolean clientSideTouchedGround;
   protected RandomStrollGoal randomStrollGoal;

   public Guardian(EntityType entityType, Level level) {
      super(entityType, level);
      this.xpReward = 10;
      this.moveControl = new Guardian.GuardianMoveControl(this);
      this.clientSideTailAnimation = this.random.nextFloat();
      this.clientSideTailAnimationO = this.clientSideTailAnimation;
   }

   protected void registerGoals() {
      MoveTowardsRestrictionGoal var1 = new MoveTowardsRestrictionGoal(this, 1.0D);
      this.randomStrollGoal = new RandomStrollGoal(this, 1.0D, 80);
      this.goalSelector.addGoal(4, new Guardian.GuardianAttackGoal(this));
      this.goalSelector.addGoal(5, var1);
      this.goalSelector.addGoal(7, this.randomStrollGoal);
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Guardian.class, 12.0F, 0.01F));
      this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
      this.randomStrollGoal.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      var1.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, LivingEntity.class, 10, true, false, new Guardian.GuardianAttackSelector(this)));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
   }

   protected PathNavigation createNavigation(Level level) {
      return new WaterBoundPathNavigation(this, level);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_MOVING, Boolean.valueOf(false));
      this.entityData.define(DATA_ID_ATTACK_TARGET, Integer.valueOf(0));
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public MobType getMobType() {
      return MobType.WATER;
   }

   public boolean isMoving() {
      return ((Boolean)this.entityData.get(DATA_ID_MOVING)).booleanValue();
   }

   private void setMoving(boolean moving) {
      this.entityData.set(DATA_ID_MOVING, Boolean.valueOf(moving));
   }

   public int getAttackDuration() {
      return 80;
   }

   private void setActiveAttackTarget(int activeAttackTarget) {
      this.entityData.set(DATA_ID_ATTACK_TARGET, Integer.valueOf(activeAttackTarget));
   }

   public boolean hasActiveAttackTarget() {
      return ((Integer)this.entityData.get(DATA_ID_ATTACK_TARGET)).intValue() != 0;
   }

   @Nullable
   public LivingEntity getActiveAttackTarget() {
      if(!this.hasActiveAttackTarget()) {
         return null;
      } else if(this.level.isClientSide) {
         if(this.clientSideCachedAttackTarget != null) {
            return this.clientSideCachedAttackTarget;
         } else {
            Entity var1 = this.level.getEntity(((Integer)this.entityData.get(DATA_ID_ATTACK_TARGET)).intValue());
            if(var1 instanceof LivingEntity) {
               this.clientSideCachedAttackTarget = (LivingEntity)var1;
               return this.clientSideCachedAttackTarget;
            } else {
               return null;
            }
         }
      } else {
         return this.getTarget();
      }
   }

   public void onSyncedDataUpdated(EntityDataAccessor entityDataAccessor) {
      super.onSyncedDataUpdated(entityDataAccessor);
      if(DATA_ID_ATTACK_TARGET.equals(entityDataAccessor)) {
         this.clientSideAttackTime = 0;
         this.clientSideCachedAttackTarget = null;
      }

   }

   public int getAmbientSoundInterval() {
      return 160;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWaterOrBubble()?SoundEvents.GUARDIAN_AMBIENT:SoundEvents.GUARDIAN_AMBIENT_LAND;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return this.isInWaterOrBubble()?SoundEvents.GUARDIAN_HURT:SoundEvents.GUARDIAN_HURT_LAND;
   }

   protected SoundEvent getDeathSound() {
      return this.isInWaterOrBubble()?SoundEvents.GUARDIAN_DEATH:SoundEvents.GUARDIAN_DEATH_LAND;
   }

   protected boolean makeStepSound() {
      return false;
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return entityDimensions.height * 0.5F;
   }

   public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
      return levelReader.getFluidState(blockPos).is(FluidTags.WATER)?10.0F + levelReader.getBrightness(blockPos) - 0.5F:super.getWalkTargetValue(blockPos, levelReader);
   }

   public void aiStep() {
      if(this.isAlive()) {
         if(this.level.isClientSide) {
            this.clientSideTailAnimationO = this.clientSideTailAnimation;
            if(!this.isInWater()) {
               this.clientSideTailAnimationSpeed = 2.0F;
               Vec3 var1 = this.getDeltaMovement();
               if(var1.y > 0.0D && this.clientSideTouchedGround && !this.isSilent()) {
                  this.level.playLocalSound(this.x, this.y, this.z, this.getFlopSound(), this.getSoundSource(), 1.0F, 1.0F, false);
               }

               this.clientSideTouchedGround = var1.y < 0.0D && this.level.loadedAndEntityCanStandOn((new BlockPos(this)).below(), this);
            } else if(this.isMoving()) {
               if(this.clientSideTailAnimationSpeed < 0.5F) {
                  this.clientSideTailAnimationSpeed = 4.0F;
               } else {
                  this.clientSideTailAnimationSpeed += (0.5F - this.clientSideTailAnimationSpeed) * 0.1F;
               }
            } else {
               this.clientSideTailAnimationSpeed += (0.125F - this.clientSideTailAnimationSpeed) * 0.2F;
            }

            this.clientSideTailAnimation += this.clientSideTailAnimationSpeed;
            this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;
            if(!this.isInWaterOrBubble()) {
               this.clientSideSpikesAnimation = this.random.nextFloat();
            } else if(this.isMoving()) {
               this.clientSideSpikesAnimation += (0.0F - this.clientSideSpikesAnimation) * 0.25F;
            } else {
               this.clientSideSpikesAnimation += (1.0F - this.clientSideSpikesAnimation) * 0.06F;
            }

            if(this.isMoving() && this.isInWater()) {
               Vec3 var1 = this.getViewVector(0.0F);

               for(int var2 = 0; var2 < 2; ++var2) {
                  this.level.addParticle(ParticleTypes.BUBBLE, this.x + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() - var1.x * 1.5D, this.y + this.random.nextDouble() * (double)this.getBbHeight() - var1.y * 1.5D, this.z + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() - var1.z * 1.5D, 0.0D, 0.0D, 0.0D);
               }
            }

            if(this.hasActiveAttackTarget()) {
               if(this.clientSideAttackTime < this.getAttackDuration()) {
                  ++this.clientSideAttackTime;
               }

               LivingEntity var1 = this.getActiveAttackTarget();
               if(var1 != null) {
                  this.getLookControl().setLookAt(var1, 90.0F, 90.0F);
                  this.getLookControl().tick();
                  double var2 = (double)this.getAttackAnimationScale(0.0F);
                  double var4 = var1.x - this.x;
                  double var6 = var1.y + (double)(var1.getBbHeight() * 0.5F) - (this.y + (double)this.getEyeHeight());
                  double var8 = var1.z - this.z;
                  double var10 = Math.sqrt(var4 * var4 + var6 * var6 + var8 * var8);
                  var4 = var4 / var10;
                  var6 = var6 / var10;
                  var8 = var8 / var10;
                  double var12 = this.random.nextDouble();

                  while(var12 < var10) {
                     var12 += 1.8D - var2 + this.random.nextDouble() * (1.7D - var2);
                     this.level.addParticle(ParticleTypes.BUBBLE, this.x + var4 * var12, this.y + var6 * var12 + (double)this.getEyeHeight(), this.z + var8 * var12, 0.0D, 0.0D, 0.0D);
                  }
               }
            }
         }

         if(this.isInWaterOrBubble()) {
            this.setAirSupply(300);
         } else if(this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F), 0.5D, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F)));
            this.yRot = this.random.nextFloat() * 360.0F;
            this.onGround = false;
            this.hasImpulse = true;
         }

         if(this.hasActiveAttackTarget()) {
            this.yRot = this.yHeadRot;
         }
      }

      super.aiStep();
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.GUARDIAN_FLOP;
   }

   public float getTailAnimation(float f) {
      return Mth.lerp(f, this.clientSideTailAnimationO, this.clientSideTailAnimation);
   }

   public float getSpikesAnimation(float f) {
      return Mth.lerp(f, this.clientSideSpikesAnimationO, this.clientSideSpikesAnimation);
   }

   public float getAttackAnimationScale(float f) {
      return ((float)this.clientSideAttackTime + f) / (float)this.getAttackDuration();
   }

   public boolean checkSpawnObstruction(LevelReader levelReader) {
      return levelReader.isUnobstructed(this);
   }

   public static boolean checkGuardianSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      return (random.nextInt(20) == 0 || !levelAccessor.canSeeSkyFromBelowWater(blockPos)) && levelAccessor.getDifficulty() != Difficulty.PEACEFUL && (mobSpawnType == MobSpawnType.SPAWNER || levelAccessor.getFluidState(blockPos).is(FluidTags.WATER));
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(!this.isMoving() && !damageSource.isMagic() && damageSource.getDirectEntity() instanceof LivingEntity) {
         LivingEntity var3 = (LivingEntity)damageSource.getDirectEntity();
         if(!damageSource.isExplosion()) {
            var3.hurt(DamageSource.thorns(this), 2.0F);
         }
      }

      if(this.randomStrollGoal != null) {
         this.randomStrollGoal.trigger();
      }

      return super.hurt(damageSource, var2);
   }

   public int getMaxHeadXRot() {
      return 180;
   }

   public void travel(Vec3 vec3) {
      if(this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(0.1F, vec3);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
         if(!this.isMoving() && this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
         }
      } else {
         super.travel(vec3);
      }

   }

   static class GuardianAttackGoal extends Goal {
      private final Guardian guardian;
      private int attackTime;
      private final boolean elder;

      public GuardianAttackGoal(Guardian guardian) {
         this.guardian = guardian;
         this.elder = guardian instanceof ElderGuardian;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity var1 = this.guardian.getTarget();
         return var1 != null && var1.isAlive();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && (this.elder || this.guardian.distanceToSqr(this.guardian.getTarget()) > 9.0D);
      }

      public void start() {
         this.attackTime = -10;
         this.guardian.getNavigation().stop();
         this.guardian.getLookControl().setLookAt(this.guardian.getTarget(), 90.0F, 90.0F);
         this.guardian.hasImpulse = true;
      }

      public void stop() {
         this.guardian.setActiveAttackTarget(0);
         this.guardian.setTarget((LivingEntity)null);
         this.guardian.randomStrollGoal.trigger();
      }

      public void tick() {
         LivingEntity var1 = this.guardian.getTarget();
         this.guardian.getNavigation().stop();
         this.guardian.getLookControl().setLookAt(var1, 90.0F, 90.0F);
         if(!this.guardian.canSee(var1)) {
            this.guardian.setTarget((LivingEntity)null);
         } else {
            ++this.attackTime;
            if(this.attackTime == 0) {
               this.guardian.setActiveAttackTarget(this.guardian.getTarget().getId());
               this.guardian.level.broadcastEntityEvent(this.guardian, (byte)21);
            } else if(this.attackTime >= this.guardian.getAttackDuration()) {
               float var2 = 1.0F;
               if(this.guardian.level.getDifficulty() == Difficulty.HARD) {
                  var2 += 2.0F;
               }

               if(this.elder) {
                  var2 += 2.0F;
               }

               var1.hurt(DamageSource.indirectMagic(this.guardian, this.guardian), var2);
               var1.hurt(DamageSource.mobAttack(this.guardian), (float)this.guardian.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue());
               this.guardian.setTarget((LivingEntity)null);
            }

            super.tick();
         }
      }
   }

   static class GuardianAttackSelector implements Predicate {
      private final Guardian guardian;

      public GuardianAttackSelector(Guardian guardian) {
         this.guardian = guardian;
      }

      public boolean test(@Nullable LivingEntity livingEntity) {
         return (livingEntity instanceof Player || livingEntity instanceof Squid) && livingEntity.distanceToSqr(this.guardian) > 9.0D;
      }

      // $FF: synthetic method
      public boolean test(@Nullable Object var1) {
         return this.test((LivingEntity)var1);
      }
   }

   static class GuardianMoveControl extends MoveControl {
      private final Guardian guardian;

      public GuardianMoveControl(Guardian guardian) {
         super(guardian);
         this.guardian = guardian;
      }

      public void tick() {
         if(this.operation == MoveControl.Operation.MOVE_TO && !this.guardian.getNavigation().isDone()) {
            Vec3 var1 = new Vec3(this.wantedX - this.guardian.x, this.wantedY - this.guardian.y, this.wantedZ - this.guardian.z);
            double var2 = var1.length();
            double var4 = var1.x / var2;
            double var6 = var1.y / var2;
            double var8 = var1.z / var2;
            float var10 = (float)(Mth.atan2(var1.z, var1.x) * 57.2957763671875D) - 90.0F;
            this.guardian.yRot = this.rotlerp(this.guardian.yRot, var10, 90.0F);
            this.guardian.yBodyRot = this.guardian.yRot;
            float var11 = (float)(this.speedModifier * this.guardian.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            float var12 = Mth.lerp(0.125F, this.guardian.getSpeed(), var11);
            this.guardian.setSpeed(var12);
            double var13 = Math.sin((double)(this.guardian.tickCount + this.guardian.getId()) * 0.5D) * 0.05D;
            double var15 = Math.cos((double)(this.guardian.yRot * 0.017453292F));
            double var17 = Math.sin((double)(this.guardian.yRot * 0.017453292F));
            double var19 = Math.sin((double)(this.guardian.tickCount + this.guardian.getId()) * 0.75D) * 0.05D;
            this.guardian.setDeltaMovement(this.guardian.getDeltaMovement().add(var13 * var15, var19 * (var17 + var15) * 0.25D + (double)var12 * var6 * 0.1D, var13 * var17));
            LookControl var21 = this.guardian.getLookControl();
            double var22 = this.guardian.x + var4 * 2.0D;
            double var24 = (double)this.guardian.getEyeHeight() + this.guardian.y + var6 / var2;
            double var26 = this.guardian.z + var8 * 2.0D;
            double var28 = var21.getWantedX();
            double var30 = var21.getWantedY();
            double var32 = var21.getWantedZ();
            if(!var21.isHasWanted()) {
               var28 = var22;
               var30 = var24;
               var32 = var26;
            }

            this.guardian.getLookControl().setLookAt(Mth.lerp(0.125D, var28, var22), Mth.lerp(0.125D, var30, var24), Mth.lerp(0.125D, var32, var26), 10.0F, 40.0F);
            this.guardian.setMoving(true);
         } else {
            this.guardian.setSpeed(0.0F);
            this.guardian.setMoving(false);
         }
      }
   }
}
