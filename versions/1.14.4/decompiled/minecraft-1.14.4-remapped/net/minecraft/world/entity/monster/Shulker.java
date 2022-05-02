package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Shulker extends AbstractGolem implements Enemy {
   private static final UUID COVERED_ARMOR_MODIFIER_UUID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
   private static final AttributeModifier COVERED_ARMOR_MODIFIER = (new AttributeModifier(COVERED_ARMOR_MODIFIER_UUID, "Covered armor bonus", 20.0D, AttributeModifier.Operation.ADDITION)).setSerialize(false);
   protected static final EntityDataAccessor DATA_ATTACH_FACE_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.DIRECTION);
   protected static final EntityDataAccessor DATA_ATTACH_POS_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
   protected static final EntityDataAccessor DATA_PEEK_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.BYTE);
   protected static final EntityDataAccessor DATA_COLOR_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.BYTE);
   private float currentPeekAmountO;
   private float currentPeekAmount;
   private BlockPos oldAttachPosition;
   private int clientSideTeleportInterpolation;

   public Shulker(EntityType entityType, Level level) {
      super(entityType, level);
      this.yBodyRotO = 180.0F;
      this.yBodyRot = 180.0F;
      this.oldAttachPosition = null;
      this.xpReward = 5;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      this.yBodyRot = 180.0F;
      this.yBodyRotO = 180.0F;
      this.yRot = 180.0F;
      this.yRotO = 180.0F;
      this.yHeadRot = 180.0F;
      this.yHeadRotO = 180.0F;
      return super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(4, new Shulker.ShulkerAttackGoal());
      this.goalSelector.addGoal(7, new Shulker.ShulkerPeekGoal());
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new Shulker.ShulkerNearestAttackGoal(this));
      this.targetSelector.addGoal(3, new Shulker.ShulkerDefenseAttackGoal(this));
   }

   protected boolean makeStepSound() {
      return false;
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SHULKER_AMBIENT;
   }

   public void playAmbientSound() {
      if(!this.isClosed()) {
         super.playAmbientSound();
      }

   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SHULKER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return this.isClosed()?SoundEvents.SHULKER_HURT_CLOSED:SoundEvents.SHULKER_HURT;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ATTACH_FACE_ID, Direction.DOWN);
      this.entityData.define(DATA_ATTACH_POS_ID, Optional.empty());
      this.entityData.define(DATA_PEEK_ID, Byte.valueOf((byte)0));
      this.entityData.define(DATA_COLOR_ID, Byte.valueOf((byte)16));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
   }

   protected BodyRotationControl createBodyControl() {
      return new Shulker.ShulkerBodyRotationControl(this);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.entityData.set(DATA_ATTACH_FACE_ID, Direction.from3DDataValue(compoundTag.getByte("AttachFace")));
      this.entityData.set(DATA_PEEK_ID, Byte.valueOf(compoundTag.getByte("Peek")));
      this.entityData.set(DATA_COLOR_ID, Byte.valueOf(compoundTag.getByte("Color")));
      if(compoundTag.contains("APX")) {
         int var2 = compoundTag.getInt("APX");
         int var3 = compoundTag.getInt("APY");
         int var4 = compoundTag.getInt("APZ");
         this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(new BlockPos(var2, var3, var4)));
      } else {
         this.entityData.set(DATA_ATTACH_POS_ID, Optional.empty());
      }

   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putByte("AttachFace", (byte)((Direction)this.entityData.get(DATA_ATTACH_FACE_ID)).get3DDataValue());
      compoundTag.putByte("Peek", ((Byte)this.entityData.get(DATA_PEEK_ID)).byteValue());
      compoundTag.putByte("Color", ((Byte)this.entityData.get(DATA_COLOR_ID)).byteValue());
      BlockPos var2 = this.getAttachPosition();
      if(var2 != null) {
         compoundTag.putInt("APX", var2.getX());
         compoundTag.putInt("APY", var2.getY());
         compoundTag.putInt("APZ", var2.getZ());
      }

   }

   public void tick() {
      super.tick();
      BlockPos var1 = (BlockPos)((Optional)this.entityData.get(DATA_ATTACH_POS_ID)).orElse((Object)null);
      if(var1 == null && !this.level.isClientSide) {
         var1 = new BlockPos(this);
         this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(var1));
      }

      if(this.isPassenger()) {
         var1 = null;
         float var2 = this.getVehicle().yRot;
         this.yRot = var2;
         this.yBodyRot = var2;
         this.yBodyRotO = var2;
         this.clientSideTeleportInterpolation = 0;
      } else if(!this.level.isClientSide) {
         BlockState var2 = this.level.getBlockState(var1);
         if(!var2.isAir()) {
            if(var2.getBlock() == Blocks.MOVING_PISTON) {
               Direction var3 = (Direction)var2.getValue(PistonBaseBlock.FACING);
               if(this.level.isEmptyBlock(var1.relative(var3))) {
                  var1 = var1.relative(var3);
                  this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(var1));
               } else {
                  this.teleportSomewhere();
               }
            } else if(var2.getBlock() == Blocks.PISTON_HEAD) {
               Direction var3 = (Direction)var2.getValue(PistonHeadBlock.FACING);
               if(this.level.isEmptyBlock(var1.relative(var3))) {
                  var1 = var1.relative(var3);
                  this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(var1));
               } else {
                  this.teleportSomewhere();
               }
            } else {
               this.teleportSomewhere();
            }
         }

         BlockPos var3 = var1.relative(this.getAttachFace());
         if(!this.level.loadedAndEntityCanStandOn(var3, this)) {
            boolean var4 = false;

            for(Direction var8 : Direction.values()) {
               var3 = var1.relative(var8);
               if(this.level.loadedAndEntityCanStandOn(var3, this)) {
                  this.entityData.set(DATA_ATTACH_FACE_ID, var8);
                  var4 = true;
                  break;
               }
            }

            if(!var4) {
               this.teleportSomewhere();
            }
         }

         BlockPos var4 = var1.relative(this.getAttachFace().getOpposite());
         if(this.level.loadedAndEntityCanStandOn(var4, this)) {
            this.teleportSomewhere();
         }
      }

      float var2 = (float)this.getRawPeekAmount() * 0.01F;
      this.currentPeekAmountO = this.currentPeekAmount;
      if(this.currentPeekAmount > var2) {
         this.currentPeekAmount = Mth.clamp(this.currentPeekAmount - 0.05F, var2, 1.0F);
      } else if(this.currentPeekAmount < var2) {
         this.currentPeekAmount = Mth.clamp(this.currentPeekAmount + 0.05F, 0.0F, var2);
      }

      if(var1 != null) {
         if(this.level.isClientSide) {
            if(this.clientSideTeleportInterpolation > 0 && this.oldAttachPosition != null) {
               --this.clientSideTeleportInterpolation;
            } else {
               this.oldAttachPosition = var1;
            }
         }

         this.x = (double)var1.getX() + 0.5D;
         this.y = (double)var1.getY();
         this.z = (double)var1.getZ() + 0.5D;
         this.xo = this.x;
         this.yo = this.y;
         this.zo = this.z;
         this.xOld = this.x;
         this.yOld = this.y;
         this.zOld = this.z;
         double var3 = 0.5D - (double)Mth.sin((0.5F + this.currentPeekAmount) * 3.1415927F) * 0.5D;
         double var5 = 0.5D - (double)Mth.sin((0.5F + this.currentPeekAmountO) * 3.1415927F) * 0.5D;
         Direction var7 = this.getAttachFace().getOpposite();
         this.setBoundingBox((new AABB(this.x - 0.5D, this.y, this.z - 0.5D, this.x + 0.5D, this.y + 1.0D, this.z + 0.5D)).expandTowards((double)var7.getStepX() * var3, (double)var7.getStepY() * var3, (double)var7.getStepZ() * var3));
         double var8 = var3 - var5;
         if(var8 > 0.0D) {
            List<Entity> var10 = this.level.getEntities(this, this.getBoundingBox());
            if(!var10.isEmpty()) {
               for(Entity var12 : var10) {
                  if(!(var12 instanceof Shulker) && !var12.noPhysics) {
                     var12.move(MoverType.SHULKER, new Vec3(var8 * (double)var7.getStepX(), var8 * (double)var7.getStepY(), var8 * (double)var7.getStepZ()));
                  }
               }
            }
         }
      }

   }

   public void move(MoverType moverType, Vec3 vec3) {
      if(moverType == MoverType.SHULKER_BOX) {
         this.teleportSomewhere();
      } else {
         super.move(moverType, vec3);
      }

   }

   public void setPos(double var1, double var3, double var5) {
      super.setPos(var1, var3, var5);
      if(this.entityData != null && this.tickCount != 0) {
         Optional<BlockPos> var7 = (Optional)this.entityData.get(DATA_ATTACH_POS_ID);
         Optional<BlockPos> var8 = Optional.of(new BlockPos(var1, var3, var5));
         if(!var8.equals(var7)) {
            this.entityData.set(DATA_ATTACH_POS_ID, var8);
            this.entityData.set(DATA_PEEK_ID, Byte.valueOf((byte)0));
            this.hasImpulse = true;
         }

      }
   }

   protected boolean teleportSomewhere() {
      if(!this.isNoAi() && this.isAlive()) {
         BlockPos var1 = new BlockPos(this);

         for(int var2 = 0; var2 < 5; ++var2) {
            BlockPos var3 = var1.offset(8 - this.random.nextInt(17), 8 - this.random.nextInt(17), 8 - this.random.nextInt(17));
            if(var3.getY() > 0 && this.level.isEmptyBlock(var3) && this.level.getWorldBorder().isWithinBounds(var3) && this.level.noCollision(this, new AABB(var3))) {
               boolean var4 = false;

               for(Direction var8 : Direction.values()) {
                  if(this.level.loadedAndEntityCanStandOn(var3.relative(var8), this)) {
                     this.entityData.set(DATA_ATTACH_FACE_ID, var8);
                     var4 = true;
                     break;
                  }
               }

               if(var4) {
                  this.playSound(SoundEvents.SHULKER_TELEPORT, 1.0F, 1.0F);
                  this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(var3));
                  this.entityData.set(DATA_PEEK_ID, Byte.valueOf((byte)0));
                  this.setTarget((LivingEntity)null);
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public void aiStep() {
      super.aiStep();
      this.setDeltaMovement(Vec3.ZERO);
      this.yBodyRotO = 180.0F;
      this.yBodyRot = 180.0F;
      this.yRot = 180.0F;
   }

   public void onSyncedDataUpdated(EntityDataAccessor entityDataAccessor) {
      if(DATA_ATTACH_POS_ID.equals(entityDataAccessor) && this.level.isClientSide && !this.isPassenger()) {
         BlockPos var2 = this.getAttachPosition();
         if(var2 != null) {
            if(this.oldAttachPosition == null) {
               this.oldAttachPosition = var2;
            } else {
               this.clientSideTeleportInterpolation = 6;
            }

            this.x = (double)var2.getX() + 0.5D;
            this.y = (double)var2.getY();
            this.z = (double)var2.getZ() + 0.5D;
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            this.xOld = this.x;
            this.yOld = this.y;
            this.zOld = this.z;
         }
      }

      super.onSyncedDataUpdated(entityDataAccessor);
   }

   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.lerpSteps = 0;
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(this.isClosed()) {
         Entity var3 = damageSource.getDirectEntity();
         if(var3 instanceof AbstractArrow) {
            return false;
         }
      }

      if(super.hurt(damageSource, var2)) {
         if((double)this.getHealth() < (double)this.getMaxHealth() * 0.5D && this.random.nextInt(4) == 0) {
            this.teleportSomewhere();
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean isClosed() {
      return this.getRawPeekAmount() == 0;
   }

   @Nullable
   public AABB getCollideBox() {
      return this.isAlive()?this.getBoundingBox():null;
   }

   public Direction getAttachFace() {
      return (Direction)this.entityData.get(DATA_ATTACH_FACE_ID);
   }

   @Nullable
   public BlockPos getAttachPosition() {
      return (BlockPos)((Optional)this.entityData.get(DATA_ATTACH_POS_ID)).orElse((Object)null);
   }

   public void setAttachPosition(@Nullable BlockPos attachPosition) {
      this.entityData.set(DATA_ATTACH_POS_ID, Optional.ofNullable(attachPosition));
   }

   public int getRawPeekAmount() {
      return ((Byte)this.entityData.get(DATA_PEEK_ID)).byteValue();
   }

   public void setRawPeekAmount(int rawPeekAmount) {
      if(!this.level.isClientSide) {
         this.getAttribute(SharedMonsterAttributes.ARMOR).removeModifier(COVERED_ARMOR_MODIFIER);
         if(rawPeekAmount == 0) {
            this.getAttribute(SharedMonsterAttributes.ARMOR).addModifier(COVERED_ARMOR_MODIFIER);
            this.playSound(SoundEvents.SHULKER_CLOSE, 1.0F, 1.0F);
         } else {
            this.playSound(SoundEvents.SHULKER_OPEN, 1.0F, 1.0F);
         }
      }

      this.entityData.set(DATA_PEEK_ID, Byte.valueOf((byte)rawPeekAmount));
   }

   public float getClientPeekAmount(float f) {
      return Mth.lerp(f, this.currentPeekAmountO, this.currentPeekAmount);
   }

   public int getClientSideTeleportInterpolation() {
      return this.clientSideTeleportInterpolation;
   }

   public BlockPos getOldAttachPosition() {
      return this.oldAttachPosition;
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return 0.5F;
   }

   public int getMaxHeadXRot() {
      return 180;
   }

   public int getMaxHeadYRot() {
      return 180;
   }

   public void push(Entity entity) {
   }

   public float getPickRadius() {
      return 0.0F;
   }

   public boolean hasValidInterpolationPositions() {
      return this.oldAttachPosition != null && this.getAttachPosition() != null;
   }

   @Nullable
   public DyeColor getColor() {
      Byte var1 = (Byte)this.entityData.get(DATA_COLOR_ID);
      return var1.byteValue() != 16 && var1.byteValue() <= 15?DyeColor.byId(var1.byteValue()):null;
   }

   class ShulkerAttackGoal extends Goal {
      private int attackTime;

      public ShulkerAttackGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity var1 = Shulker.this.getTarget();
         return var1 != null && var1.isAlive()?Shulker.this.level.getDifficulty() != Difficulty.PEACEFUL:false;
      }

      public void start() {
         this.attackTime = 20;
         Shulker.this.setRawPeekAmount(100);
      }

      public void stop() {
         Shulker.this.setRawPeekAmount(0);
      }

      public void tick() {
         if(Shulker.this.level.getDifficulty() != Difficulty.PEACEFUL) {
            --this.attackTime;
            LivingEntity var1 = Shulker.this.getTarget();
            Shulker.this.getLookControl().setLookAt(var1, 180.0F, 180.0F);
            double var2 = Shulker.this.distanceToSqr(var1);
            if(var2 < 400.0D) {
               if(this.attackTime <= 0) {
                  this.attackTime = 20 + Shulker.this.random.nextInt(10) * 20 / 2;
                  Shulker.this.level.addFreshEntity(new ShulkerBullet(Shulker.this.level, Shulker.this, var1, Shulker.this.getAttachFace().getAxis()));
                  Shulker.this.playSound(SoundEvents.SHULKER_SHOOT, 2.0F, (Shulker.this.random.nextFloat() - Shulker.this.random.nextFloat()) * 0.2F + 1.0F);
               }
            } else {
               Shulker.this.setTarget((LivingEntity)null);
            }

            super.tick();
         }
      }
   }

   class ShulkerBodyRotationControl extends BodyRotationControl {
      public ShulkerBodyRotationControl(Mob mob) {
         super(mob);
      }

      public void clientTick() {
      }
   }

   static class ShulkerDefenseAttackGoal extends NearestAttackableTargetGoal {
      public ShulkerDefenseAttackGoal(Shulker shulker) {
         super(shulker, LivingEntity.class, 10, true, false, (livingEntity) -> {
            return livingEntity instanceof Enemy;
         });
      }

      public boolean canUse() {
         return this.mob.getTeam() == null?false:super.canUse();
      }

      protected AABB getTargetSearchArea(double d) {
         Direction var3 = ((Shulker)this.mob).getAttachFace();
         return var3.getAxis() == Direction.Axis.X?this.mob.getBoundingBox().inflate(4.0D, d, d):(var3.getAxis() == Direction.Axis.Z?this.mob.getBoundingBox().inflate(d, d, 4.0D):this.mob.getBoundingBox().inflate(d, 4.0D, d));
      }
   }

   class ShulkerNearestAttackGoal extends NearestAttackableTargetGoal {
      public ShulkerNearestAttackGoal(Shulker var2) {
         super(var2, Player.class, true);
      }

      public boolean canUse() {
         return Shulker.this.level.getDifficulty() == Difficulty.PEACEFUL?false:super.canUse();
      }

      protected AABB getTargetSearchArea(double d) {
         Direction var3 = ((Shulker)this.mob).getAttachFace();
         return var3.getAxis() == Direction.Axis.X?this.mob.getBoundingBox().inflate(4.0D, d, d):(var3.getAxis() == Direction.Axis.Z?this.mob.getBoundingBox().inflate(d, d, 4.0D):this.mob.getBoundingBox().inflate(d, 4.0D, d));
      }
   }

   class ShulkerPeekGoal extends Goal {
      private int peekTime;

      private ShulkerPeekGoal() {
      }

      public boolean canUse() {
         return Shulker.this.getTarget() == null && Shulker.this.random.nextInt(40) == 0;
      }

      public boolean canContinueToUse() {
         return Shulker.this.getTarget() == null && this.peekTime > 0;
      }

      public void start() {
         this.peekTime = 20 * (1 + Shulker.this.random.nextInt(3));
         Shulker.this.setRawPeekAmount(30);
      }

      public void stop() {
         if(Shulker.this.getTarget() == null) {
            Shulker.this.setRawPeekAmount(0);
         }

      }

      public void tick() {
         --this.peekTime;
      }
   }
}
