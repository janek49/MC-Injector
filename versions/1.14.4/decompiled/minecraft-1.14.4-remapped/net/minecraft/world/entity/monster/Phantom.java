package net.minecraft.world.entity.monster;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class Phantom extends FlyingMob implements Enemy {
   private static final EntityDataAccessor ID_SIZE = SynchedEntityData.defineId(Phantom.class, EntityDataSerializers.INT);
   private Vec3 moveTargetPoint = Vec3.ZERO;
   private BlockPos anchorPoint = BlockPos.ZERO;
   private Phantom.AttackPhase attackPhase = Phantom.AttackPhase.CIRCLE;

   public Phantom(EntityType entityType, Level level) {
      super(entityType, level);
      this.xpReward = 5;
      this.moveControl = new Phantom.PhantomMoveControl(this);
      this.lookControl = new Phantom.PhantomLookControl(this);
   }

   protected BodyRotationControl createBodyControl() {
      return new Phantom.PhantomBodyRotationControl(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new Phantom.PhantomAttackStrategyGoal());
      this.goalSelector.addGoal(2, new Phantom.PhantomSweepAttackGoal());
      this.goalSelector.addGoal(3, new Phantom.PhantomCircleAroundAnchorGoal());
      this.targetSelector.addGoal(1, new Phantom.PhantomAttackPlayerTargetGoal());
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ID_SIZE, Integer.valueOf(0));
   }

   public void setPhantomSize(int phantomSize) {
      this.entityData.set(ID_SIZE, Integer.valueOf(Mth.clamp(phantomSize, 0, 64)));
   }

   private void updatePhantomSizeInfo() {
      this.refreshDimensions();
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue((double)(6 + this.getPhantomSize()));
   }

   public int getPhantomSize() {
      return ((Integer)this.entityData.get(ID_SIZE)).intValue();
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return entityDimensions.height * 0.35F;
   }

   public void onSyncedDataUpdated(EntityDataAccessor entityDataAccessor) {
      if(ID_SIZE.equals(entityDataAccessor)) {
         this.updatePhantomSizeInfo();
      }

      super.onSyncedDataUpdated(entityDataAccessor);
   }

   public void tick() {
      super.tick();
      if(this.level.isClientSide) {
         float var1 = Mth.cos((float)(this.getId() * 3 + this.tickCount) * 0.13F + 3.1415927F);
         float var2 = Mth.cos((float)(this.getId() * 3 + this.tickCount + 1) * 0.13F + 3.1415927F);
         if(var1 > 0.0F && var2 <= 0.0F) {
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.PHANTOM_FLAP, this.getSoundSource(), 0.95F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
         }

         int var3 = this.getPhantomSize();
         float var4 = Mth.cos(this.yRot * 0.017453292F) * (1.3F + 0.21F * (float)var3);
         float var5 = Mth.sin(this.yRot * 0.017453292F) * (1.3F + 0.21F * (float)var3);
         float var6 = (0.3F + var1 * 0.45F) * ((float)var3 * 0.2F + 1.0F);
         this.level.addParticle(ParticleTypes.MYCELIUM, this.x + (double)var4, this.y + (double)var6, this.z + (double)var5, 0.0D, 0.0D, 0.0D);
         this.level.addParticle(ParticleTypes.MYCELIUM, this.x - (double)var4, this.y + (double)var6, this.z - (double)var5, 0.0D, 0.0D, 0.0D);
      }

      if(!this.level.isClientSide && this.level.getDifficulty() == Difficulty.PEACEFUL) {
         this.remove();
      }

   }

   public void aiStep() {
      if(this.isAlive() && this.isSunBurnTick()) {
         this.setSecondsOnFire(8);
      }

      super.aiStep();
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
   }

   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      this.anchorPoint = (new BlockPos(this)).above(5);
      this.setPhantomSize(0);
      return super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if(compoundTag.contains("AX")) {
         this.anchorPoint = new BlockPos(compoundTag.getInt("AX"), compoundTag.getInt("AY"), compoundTag.getInt("AZ"));
      }

      this.setPhantomSize(compoundTag.getInt("Size"));
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("AX", this.anchorPoint.getX());
      compoundTag.putInt("AY", this.anchorPoint.getY());
      compoundTag.putInt("AZ", this.anchorPoint.getZ());
      compoundTag.putInt("Size", this.getPhantomSize());
   }

   public boolean shouldRenderAtSqrDistance(double d) {
      return true;
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PHANTOM_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.PHANTOM_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PHANTOM_DEATH;
   }

   public MobType getMobType() {
      return MobType.UNDEAD;
   }

   protected float getSoundVolume() {
      return 1.0F;
   }

   public boolean canAttackType(EntityType entityType) {
      return true;
   }

   public EntityDimensions getDimensions(Pose pose) {
      int var2 = this.getPhantomSize();
      EntityDimensions var3 = super.getDimensions(pose);
      float var4 = (var3.width + 0.2F * (float)var2) / var3.width;
      return var3.scale(var4);
   }

   static enum AttackPhase {
      CIRCLE,
      SWOOP;
   }

   class PhantomAttackPlayerTargetGoal extends Goal {
      private final TargetingConditions attackTargeting;
      private int nextScanTick;

      private PhantomAttackPlayerTargetGoal() {
         this.attackTargeting = (new TargetingConditions()).range(64.0D);
         this.nextScanTick = 20;
      }

      public boolean canUse() {
         if(this.nextScanTick > 0) {
            --this.nextScanTick;
            return false;
         } else {
            this.nextScanTick = 60;
            List<Player> var1 = Phantom.this.level.getNearbyPlayers(this.attackTargeting, Phantom.this, Phantom.this.getBoundingBox().inflate(16.0D, 64.0D, 16.0D));
            if(!var1.isEmpty()) {
               var1.sort((var0, var1) -> {
                  return var0.y > var1.y?-1:1;
               });

               for(Player var3 : var1) {
                  if(Phantom.this.canAttack(var3, TargetingConditions.DEFAULT)) {
                     Phantom.this.setTarget(var3);
                     return true;
                  }
               }
            }

            return false;
         }
      }

      public boolean canContinueToUse() {
         LivingEntity var1 = Phantom.this.getTarget();
         return var1 != null?Phantom.this.canAttack(var1, TargetingConditions.DEFAULT):false;
      }
   }

   class PhantomAttackStrategyGoal extends Goal {
      private int nextSweepTick;

      private PhantomAttackStrategyGoal() {
      }

      public boolean canUse() {
         LivingEntity var1 = Phantom.this.getTarget();
         return var1 != null?Phantom.this.canAttack(Phantom.this.getTarget(), TargetingConditions.DEFAULT):false;
      }

      public void start() {
         this.nextSweepTick = 10;
         Phantom.this.attackPhase = Phantom.AttackPhase.CIRCLE;
         this.setAnchorAboveTarget();
      }

      public void stop() {
         Phantom.this.anchorPoint = Phantom.this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, Phantom.this.anchorPoint).above(10 + Phantom.this.random.nextInt(20));
      }

      public void tick() {
         if(Phantom.this.attackPhase == Phantom.AttackPhase.CIRCLE) {
            --this.nextSweepTick;
            if(this.nextSweepTick <= 0) {
               Phantom.this.attackPhase = Phantom.AttackPhase.SWOOP;
               this.setAnchorAboveTarget();
               this.nextSweepTick = (8 + Phantom.this.random.nextInt(4)) * 20;
               Phantom.this.playSound(SoundEvents.PHANTOM_SWOOP, 10.0F, 0.95F + Phantom.this.random.nextFloat() * 0.1F);
            }
         }

      }

      private void setAnchorAboveTarget() {
         Phantom.this.anchorPoint = (new BlockPos(Phantom.this.getTarget())).above(20 + Phantom.this.random.nextInt(20));
         if(Phantom.this.anchorPoint.getY() < Phantom.this.level.getSeaLevel()) {
            Phantom.this.anchorPoint = new BlockPos(Phantom.this.anchorPoint.getX(), Phantom.this.level.getSeaLevel() + 1, Phantom.this.anchorPoint.getZ());
         }

      }
   }

   class PhantomBodyRotationControl extends BodyRotationControl {
      public PhantomBodyRotationControl(Mob mob) {
         super(mob);
      }

      public void clientTick() {
         Phantom.this.yHeadRot = Phantom.this.yBodyRot;
         Phantom.this.yBodyRot = Phantom.this.yRot;
      }
   }

   class PhantomCircleAroundAnchorGoal extends Phantom.PhantomMoveTargetGoal {
      private float angle;
      private float distance;
      private float height;
      private float clockwise;

      private PhantomCircleAroundAnchorGoal() {
         super();
      }

      public boolean canUse() {
         return Phantom.this.getTarget() == null || Phantom.this.attackPhase == Phantom.AttackPhase.CIRCLE;
      }

      public void start() {
         this.distance = 5.0F + Phantom.this.random.nextFloat() * 10.0F;
         this.height = -4.0F + Phantom.this.random.nextFloat() * 9.0F;
         this.clockwise = Phantom.this.random.nextBoolean()?1.0F:-1.0F;
         this.selectNext();
      }

      public void tick() {
         if(Phantom.this.random.nextInt(350) == 0) {
            this.height = -4.0F + Phantom.this.random.nextFloat() * 9.0F;
         }

         if(Phantom.this.random.nextInt(250) == 0) {
            ++this.distance;
            if(this.distance > 15.0F) {
               this.distance = 5.0F;
               this.clockwise = -this.clockwise;
            }
         }

         if(Phantom.this.random.nextInt(450) == 0) {
            this.angle = Phantom.this.random.nextFloat() * 2.0F * 3.1415927F;
            this.selectNext();
         }

         if(this.touchingTarget()) {
            this.selectNext();
         }

         if(Phantom.this.moveTargetPoint.y < Phantom.this.y && !Phantom.this.level.isEmptyBlock((new BlockPos(Phantom.this)).below(1))) {
            this.height = Math.max(1.0F, this.height);
            this.selectNext();
         }

         if(Phantom.this.moveTargetPoint.y > Phantom.this.y && !Phantom.this.level.isEmptyBlock((new BlockPos(Phantom.this)).above(1))) {
            this.height = Math.min(-1.0F, this.height);
            this.selectNext();
         }

      }

      private void selectNext() {
         if(BlockPos.ZERO.equals(Phantom.this.anchorPoint)) {
            Phantom.this.anchorPoint = new BlockPos(Phantom.this);
         }

         this.angle += this.clockwise * 15.0F * 0.017453292F;
         Phantom.this.moveTargetPoint = (new Vec3(Phantom.this.anchorPoint)).add((double)(this.distance * Mth.cos(this.angle)), (double)(-4.0F + this.height), (double)(this.distance * Mth.sin(this.angle)));
      }
   }

   class PhantomLookControl extends LookControl {
      public PhantomLookControl(Mob mob) {
         super(mob);
      }

      public void tick() {
      }
   }

   class PhantomMoveControl extends MoveControl {
      private float speed = 0.1F;

      public PhantomMoveControl(Mob mob) {
         super(mob);
      }

      public void tick() {
         if(Phantom.this.horizontalCollision) {
            Phantom.this.yRot += 180.0F;
            this.speed = 0.1F;
         }

         float var1 = (float)(Phantom.this.moveTargetPoint.x - Phantom.this.x);
         float var2 = (float)(Phantom.this.moveTargetPoint.y - Phantom.this.y);
         float var3 = (float)(Phantom.this.moveTargetPoint.z - Phantom.this.z);
         double var4 = (double)Mth.sqrt(var1 * var1 + var3 * var3);
         double var6 = 1.0D - (double)Mth.abs(var2 * 0.7F) / var4;
         var1 = (float)((double)var1 * var6);
         var3 = (float)((double)var3 * var6);
         var4 = (double)Mth.sqrt(var1 * var1 + var3 * var3);
         double var8 = (double)Mth.sqrt(var1 * var1 + var3 * var3 + var2 * var2);
         float var10 = Phantom.this.yRot;
         float var11 = (float)Mth.atan2((double)var3, (double)var1);
         float var12 = Mth.wrapDegrees(Phantom.this.yRot + 90.0F);
         float var13 = Mth.wrapDegrees(var11 * 57.295776F);
         Phantom.this.yRot = Mth.approachDegrees(var12, var13, 4.0F) - 90.0F;
         Phantom.this.yBodyRot = Phantom.this.yRot;
         if(Mth.degreesDifferenceAbs(var10, Phantom.this.yRot) < 3.0F) {
            this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
         } else {
            this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
         }

         float var14 = (float)(-(Mth.atan2((double)(-var2), var4) * 57.2957763671875D));
         Phantom.this.xRot = var14;
         float var15 = Phantom.this.yRot + 90.0F;
         double var16 = (double)(this.speed * Mth.cos(var15 * 0.017453292F)) * Math.abs((double)var1 / var8);
         double var18 = (double)(this.speed * Mth.sin(var15 * 0.017453292F)) * Math.abs((double)var3 / var8);
         double var20 = (double)(this.speed * Mth.sin(var14 * 0.017453292F)) * Math.abs((double)var2 / var8);
         Vec3 var22 = Phantom.this.getDeltaMovement();
         Phantom.this.setDeltaMovement(var22.add((new Vec3(var16, var20, var18)).subtract(var22).scale(0.2D)));
      }
   }

   abstract class PhantomMoveTargetGoal extends Goal {
      public PhantomMoveTargetGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      protected boolean touchingTarget() {
         return Phantom.this.moveTargetPoint.distanceToSqr(Phantom.this.x, Phantom.this.y, Phantom.this.z) < 4.0D;
      }
   }

   class PhantomSweepAttackGoal extends Phantom.PhantomMoveTargetGoal {
      private PhantomSweepAttackGoal() {
         super();
      }

      public boolean canUse() {
         return Phantom.this.getTarget() != null && Phantom.this.attackPhase == Phantom.AttackPhase.SWOOP;
      }

      public boolean canContinueToUse() {
         LivingEntity var1 = Phantom.this.getTarget();
         if(var1 == null) {
            return false;
         } else if(!var1.isAlive()) {
            return false;
         } else if(!(var1 instanceof Player) || !((Player)var1).isSpectator() && !((Player)var1).isCreative()) {
            if(!this.canUse()) {
               return false;
            } else {
               if(Phantom.this.tickCount % 20 == 0) {
                  List<Cat> var2 = Phantom.this.level.getEntitiesOfClass(Cat.class, Phantom.this.getBoundingBox().inflate(16.0D), EntitySelector.ENTITY_STILL_ALIVE);
                  if(!var2.isEmpty()) {
                     for(Cat var4 : var2) {
                        var4.hiss();
                     }

                     return false;
                  }
               }

               return true;
            }
         } else {
            return false;
         }
      }

      public void start() {
      }

      public void stop() {
         Phantom.this.setTarget((LivingEntity)null);
         Phantom.this.attackPhase = Phantom.AttackPhase.CIRCLE;
      }

      public void tick() {
         LivingEntity var1 = Phantom.this.getTarget();
         Phantom.this.moveTargetPoint = new Vec3(var1.x, var1.y + (double)var1.getBbHeight() * 0.5D, var1.z);
         if(Phantom.this.getBoundingBox().inflate(0.20000000298023224D).intersects(var1.getBoundingBox())) {
            Phantom.this.doHurtTarget(var1);
            Phantom.this.attackPhase = Phantom.AttackPhase.CIRCLE;
            Phantom.this.level.levelEvent(1039, new BlockPos(Phantom.this), 0);
         } else if(Phantom.this.horizontalCollision || Phantom.this.hurtTime > 0) {
            Phantom.this.attackPhase = Phantom.AttackPhase.CIRCLE;
         }

      }
   }
}
