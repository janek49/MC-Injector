package net.minecraft.world.entity.ambient;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Bat extends AmbientCreature {
   private static final EntityDataAccessor DATA_ID_FLAGS = SynchedEntityData.defineId(Bat.class, EntityDataSerializers.BYTE);
   private static final TargetingConditions BAT_RESTING_TARGETING = (new TargetingConditions()).range(4.0D).allowSameTeam();
   private BlockPos targetPosition;

   public Bat(EntityType entityType, Level level) {
      super(entityType, level);
      this.setResting(true);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_FLAGS, Byte.valueOf((byte)0));
   }

   protected float getSoundVolume() {
      return 0.1F;
   }

   protected float getVoicePitch() {
      return super.getVoicePitch() * 0.95F;
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return this.isResting() && this.random.nextInt(4) != 0?null:SoundEvents.BAT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.BAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.BAT_DEATH;
   }

   public boolean isPushable() {
      return false;
   }

   protected void doPush(Entity entity) {
   }

   protected void pushEntities() {
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
   }

   public boolean isResting() {
      return (((Byte)this.entityData.get(DATA_ID_FLAGS)).byteValue() & 1) != 0;
   }

   public void setResting(boolean resting) {
      byte var2 = ((Byte)this.entityData.get(DATA_ID_FLAGS)).byteValue();
      if(resting) {
         this.entityData.set(DATA_ID_FLAGS, Byte.valueOf((byte)(var2 | 1)));
      } else {
         this.entityData.set(DATA_ID_FLAGS, Byte.valueOf((byte)(var2 & -2)));
      }

   }

   public void tick() {
      super.tick();
      if(this.isResting()) {
         this.setDeltaMovement(Vec3.ZERO);
         this.y = (double)Mth.floor(this.y) + 1.0D - (double)this.getBbHeight();
      } else {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
      }

   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      BlockPos var1 = new BlockPos(this);
      BlockPos var2 = var1.above();
      if(this.isResting()) {
         if(this.level.getBlockState(var2).isRedstoneConductor(this.level, var1)) {
            if(this.random.nextInt(200) == 0) {
               this.yHeadRot = (float)this.random.nextInt(360);
            }

            if(this.level.getNearestPlayer(BAT_RESTING_TARGETING, this) != null) {
               this.setResting(false);
               this.level.levelEvent((Player)null, 1025, var1, 0);
            }
         } else {
            this.setResting(false);
            this.level.levelEvent((Player)null, 1025, var1, 0);
         }
      } else {
         if(this.targetPosition != null && (!this.level.isEmptyBlock(this.targetPosition) || this.targetPosition.getY() < 1)) {
            this.targetPosition = null;
         }

         if(this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerThan(this.position(), 2.0D)) {
            this.targetPosition = new BlockPos(this.x + (double)this.random.nextInt(7) - (double)this.random.nextInt(7), this.y + (double)this.random.nextInt(6) - 2.0D, this.z + (double)this.random.nextInt(7) - (double)this.random.nextInt(7));
         }

         double var3 = (double)this.targetPosition.getX() + 0.5D - this.x;
         double var5 = (double)this.targetPosition.getY() + 0.1D - this.y;
         double var7 = (double)this.targetPosition.getZ() + 0.5D - this.z;
         Vec3 var9 = this.getDeltaMovement();
         Vec3 var10 = var9.add((Math.signum(var3) * 0.5D - var9.x) * 0.10000000149011612D, (Math.signum(var5) * 0.699999988079071D - var9.y) * 0.10000000149011612D, (Math.signum(var7) * 0.5D - var9.z) * 0.10000000149011612D);
         this.setDeltaMovement(var10);
         float var11 = (float)(Mth.atan2(var10.z, var10.x) * 57.2957763671875D) - 90.0F;
         float var12 = Mth.wrapDegrees(var11 - this.yRot);
         this.zza = 0.5F;
         this.yRot += var12;
         if(this.random.nextInt(100) == 0 && this.level.getBlockState(var2).isRedstoneConductor(this.level, var2)) {
            this.setResting(true);
         }
      }

   }

   protected boolean makeStepSound() {
      return false;
   }

   public void causeFallDamage(float var1, float var2) {
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState blockState, BlockPos blockPos) {
   }

   public boolean isIgnoringBlockTriggers() {
      return true;
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(this.isInvulnerableTo(damageSource)) {
         return false;
      } else {
         if(!this.level.isClientSide && this.isResting()) {
            this.setResting(false);
         }

         return super.hurt(damageSource, var2);
      }
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.entityData.set(DATA_ID_FLAGS, Byte.valueOf(compoundTag.getByte("BatFlags")));
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putByte("BatFlags", ((Byte)this.entityData.get(DATA_ID_FLAGS)).byteValue());
   }

   public static boolean checkBatSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      if(blockPos.getY() >= levelAccessor.getSeaLevel()) {
         return false;
      } else {
         int var5 = levelAccessor.getMaxLocalRawBrightness(blockPos);
         int var6 = 4;
         if(isHalloween()) {
            var6 = 7;
         } else if(random.nextBoolean()) {
            return false;
         }

         return var5 > random.nextInt(var6)?false:checkMobSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, random);
      }
   }

   private static boolean isHalloween() {
      LocalDate var0 = LocalDate.now();
      int var1 = var0.get(ChronoField.DAY_OF_MONTH);
      int var2 = var0.get(ChronoField.MONTH_OF_YEAR);
      return var2 == 10 && var1 >= 20 || var2 == 11 && var1 <= 3;
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return entityDimensions.height / 2.0F;
   }
}
