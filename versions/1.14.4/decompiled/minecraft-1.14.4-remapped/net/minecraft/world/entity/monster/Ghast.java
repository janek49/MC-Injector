package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Ghast extends FlyingMob implements Enemy {
   private static final EntityDataAccessor DATA_IS_CHARGING = SynchedEntityData.defineId(Ghast.class, EntityDataSerializers.BOOLEAN);
   private int explosionPower = 1;

   public Ghast(EntityType entityType, Level level) {
      super(entityType, level);
      this.xpReward = 5;
      this.moveControl = new Ghast.GhastMoveControl(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(5, new Ghast.RandomFloatAroundGoal(this));
      this.goalSelector.addGoal(7, new Ghast.GhastLookGoal(this));
      this.goalSelector.addGoal(7, new Ghast.GhastShootFireballGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, (livingEntity) -> {
         return Math.abs(livingEntity.y - this.y) <= 4.0D;
      }));
   }

   public boolean isCharging() {
      return ((Boolean)this.entityData.get(DATA_IS_CHARGING)).booleanValue();
   }

   public void setCharging(boolean charging) {
      this.entityData.set(DATA_IS_CHARGING, Boolean.valueOf(charging));
   }

   public int getExplosionPower() {
      return this.explosionPower;
   }

   public void tick() {
      super.tick();
      if(!this.level.isClientSide && this.level.getDifficulty() == Difficulty.PEACEFUL) {
         this.remove();
      }

   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(this.isInvulnerableTo(damageSource)) {
         return false;
      } else if(damageSource.getDirectEntity() instanceof LargeFireball && damageSource.getEntity() instanceof Player) {
         super.hurt(damageSource, 1000.0F);
         return true;
      } else {
         return super.hurt(damageSource, var2);
      }
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_IS_CHARGING, Boolean.valueOf(false));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(100.0D);
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.GHAST_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.GHAST_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.GHAST_DEATH;
   }

   protected float getSoundVolume() {
      return 10.0F;
   }

   public static boolean checkGhastSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      return levelAccessor.getDifficulty() != Difficulty.PEACEFUL && random.nextInt(20) == 0 && checkMobSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, random);
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("ExplosionPower", this.explosionPower);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if(compoundTag.contains("ExplosionPower", 99)) {
         this.explosionPower = compoundTag.getInt("ExplosionPower");
      }

   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return 2.6F;
   }

   static class GhastLookGoal extends Goal {
      private final Ghast ghast;

      public GhastLookGoal(Ghast ghast) {
         this.ghast = ghast;
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return true;
      }

      public void tick() {
         if(this.ghast.getTarget() == null) {
            Vec3 var1 = this.ghast.getDeltaMovement();
            this.ghast.yRot = -((float)Mth.atan2(var1.x, var1.z)) * 57.295776F;
            this.ghast.yBodyRot = this.ghast.yRot;
         } else {
            LivingEntity var1 = this.ghast.getTarget();
            double var2 = 64.0D;
            if(var1.distanceToSqr(this.ghast) < 4096.0D) {
               double var4 = var1.x - this.ghast.x;
               double var6 = var1.z - this.ghast.z;
               this.ghast.yRot = -((float)Mth.atan2(var4, var6)) * 57.295776F;
               this.ghast.yBodyRot = this.ghast.yRot;
            }
         }

      }
   }

   static class GhastMoveControl extends MoveControl {
      private final Ghast ghast;
      private int floatDuration;

      public GhastMoveControl(Ghast ghast) {
         super(ghast);
         this.ghast = ghast;
      }

      public void tick() {
         if(this.operation == MoveControl.Operation.MOVE_TO) {
            if(this.floatDuration-- <= 0) {
               this.floatDuration += this.ghast.getRandom().nextInt(5) + 2;
               Vec3 var1 = new Vec3(this.wantedX - this.ghast.x, this.wantedY - this.ghast.y, this.wantedZ - this.ghast.z);
               double var2 = var1.length();
               var1 = var1.normalize();
               if(this.canReach(var1, Mth.ceil(var2))) {
                  this.ghast.setDeltaMovement(this.ghast.getDeltaMovement().add(var1.scale(0.1D)));
               } else {
                  this.operation = MoveControl.Operation.WAIT;
               }
            }

         }
      }

      private boolean canReach(Vec3 vec3, int var2) {
         AABB var3 = this.ghast.getBoundingBox();

         for(int var4 = 1; var4 < var2; ++var4) {
            var3 = var3.move(vec3);
            if(!this.ghast.level.noCollision(this.ghast, var3)) {
               return false;
            }
         }

         return true;
      }
   }

   static class GhastShootFireballGoal extends Goal {
      private final Ghast ghast;
      public int chargeTime;

      public GhastShootFireballGoal(Ghast ghast) {
         this.ghast = ghast;
      }

      public boolean canUse() {
         return this.ghast.getTarget() != null;
      }

      public void start() {
         this.chargeTime = 0;
      }

      public void stop() {
         this.ghast.setCharging(false);
      }

      public void tick() {
         LivingEntity var1 = this.ghast.getTarget();
         double var2 = 64.0D;
         if(var1.distanceToSqr(this.ghast) < 4096.0D && this.ghast.canSee(var1)) {
            Level var4 = this.ghast.level;
            ++this.chargeTime;
            if(this.chargeTime == 10) {
               var4.levelEvent((Player)null, 1015, new BlockPos(this.ghast), 0);
            }

            if(this.chargeTime == 20) {
               double var5 = 4.0D;
               Vec3 var7 = this.ghast.getViewVector(1.0F);
               double var8 = var1.x - (this.ghast.x + var7.x * 4.0D);
               double var10 = var1.getBoundingBox().minY + (double)(var1.getBbHeight() / 2.0F) - (0.5D + this.ghast.y + (double)(this.ghast.getBbHeight() / 2.0F));
               double var12 = var1.z - (this.ghast.z + var7.z * 4.0D);
               var4.levelEvent((Player)null, 1016, new BlockPos(this.ghast), 0);
               LargeFireball var14 = new LargeFireball(var4, this.ghast, var8, var10, var12);
               var14.explosionPower = this.ghast.getExplosionPower();
               var14.x = this.ghast.x + var7.x * 4.0D;
               var14.y = this.ghast.y + (double)(this.ghast.getBbHeight() / 2.0F) + 0.5D;
               var14.z = this.ghast.z + var7.z * 4.0D;
               var4.addFreshEntity(var14);
               this.chargeTime = -40;
            }
         } else if(this.chargeTime > 0) {
            --this.chargeTime;
         }

         this.ghast.setCharging(this.chargeTime > 10);
      }
   }

   static class RandomFloatAroundGoal extends Goal {
      private final Ghast ghast;

      public RandomFloatAroundGoal(Ghast ghast) {
         this.ghast = ghast;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         MoveControl var1 = this.ghast.getMoveControl();
         if(!var1.hasWanted()) {
            return true;
         } else {
            double var2 = var1.getWantedX() - this.ghast.x;
            double var4 = var1.getWantedY() - this.ghast.y;
            double var6 = var1.getWantedZ() - this.ghast.z;
            double var8 = var2 * var2 + var4 * var4 + var6 * var6;
            return var8 < 1.0D || var8 > 3600.0D;
         }
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void start() {
         Random var1 = this.ghast.getRandom();
         double var2 = this.ghast.x + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double var4 = this.ghast.y + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double var6 = this.ghast.z + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
         this.ghast.getMoveControl().setWantedPosition(var2, var4, var6, 1.0D);
      }
   }
}
