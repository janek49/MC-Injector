package net.minecraft.world.entity.monster;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Ravager extends Raider {
   private static final Predicate NO_RAVAGER_AND_ALIVE = (entity) -> {
      return entity.isAlive() && !(entity instanceof Ravager);
   };
   private int attackTick;
   private int stunnedTick;
   private int roarTick;

   public Ravager(EntityType entityType, Level level) {
      super(entityType, level);
      this.maxUpStep = 1.0F;
      this.xpReward = 20;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(4, new Ravager.RavagerMeleeAttackGoal());
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.4D));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(2, (new HurtByTargetGoal(this, new Class[]{Raider.class})).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, AbstractVillager.class, true));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, IronGolem.class, true));
   }

   protected void updateControlFlags() {
      boolean var1 = !(this.getControllingPassenger() instanceof Mob) || this.getControllingPassenger().getType().is(EntityTypeTags.RAIDERS);
      boolean var2 = !(this.getVehicle() instanceof Boat);
      this.goalSelector.setControlFlag(Goal.Flag.MOVE, var1);
      this.goalSelector.setControlFlag(Goal.Flag.JUMP, var1 && var2);
      this.goalSelector.setControlFlag(Goal.Flag.LOOK, var1);
      this.goalSelector.setControlFlag(Goal.Flag.TARGET, var1);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
      this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(12.0D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK).setBaseValue(1.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("AttackTick", this.attackTick);
      compoundTag.putInt("StunTick", this.stunnedTick);
      compoundTag.putInt("RoarTick", this.roarTick);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.attackTick = compoundTag.getInt("AttackTick");
      this.stunnedTick = compoundTag.getInt("StunTick");
      this.roarTick = compoundTag.getInt("RoarTick");
   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.RAVAGER_CELEBRATE;
   }

   protected PathNavigation createNavigation(Level level) {
      return new Ravager.RavagerNavigation(this, level);
   }

   public int getMaxHeadYRot() {
      return 45;
   }

   public double getRideHeight() {
      return 2.1D;
   }

   public boolean canBeControlledByRider() {
      return !this.isNoAi() && this.getControllingPassenger() instanceof LivingEntity;
   }

   @Nullable
   public Entity getControllingPassenger() {
      return this.getPassengers().isEmpty()?null:(Entity)this.getPassengers().get(0);
   }

   public void aiStep() {
      super.aiStep();
      if(this.isAlive()) {
         if(this.isImmobile()) {
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
         } else {
            double var1 = this.getTarget() != null?0.35D:0.3D;
            double var3 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue();
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Mth.lerp(0.1D, var3, var1));
         }

         if(this.horizontalCollision && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            boolean var1 = false;
            AABB var2 = this.getBoundingBox().inflate(0.2D);

            for(BlockPos var4 : BlockPos.betweenClosed(Mth.floor(var2.minX), Mth.floor(var2.minY), Mth.floor(var2.minZ), Mth.floor(var2.maxX), Mth.floor(var2.maxY), Mth.floor(var2.maxZ))) {
               BlockState var5 = this.level.getBlockState(var4);
               Block var6 = var5.getBlock();
               if(var6 instanceof LeavesBlock) {
                  var1 = this.level.destroyBlock(var4, true) || var1;
               }
            }

            if(!var1 && this.onGround) {
               this.jumpFromGround();
            }
         }

         if(this.roarTick > 0) {
            --this.roarTick;
            if(this.roarTick == 10) {
               this.roar();
            }
         }

         if(this.attackTick > 0) {
            --this.attackTick;
         }

         if(this.stunnedTick > 0) {
            --this.stunnedTick;
            this.stunEffect();
            if(this.stunnedTick == 0) {
               this.playSound(SoundEvents.RAVAGER_ROAR, 1.0F, 1.0F);
               this.roarTick = 20;
            }
         }

      }
   }

   private void stunEffect() {
      if(this.random.nextInt(6) == 0) {
         double var1 = this.x - (double)this.getBbWidth() * Math.sin((double)(this.yBodyRot * 0.017453292F)) + (this.random.nextDouble() * 0.6D - 0.3D);
         double var3 = this.y + (double)this.getBbHeight() - 0.3D;
         double var5 = this.z + (double)this.getBbWidth() * Math.cos((double)(this.yBodyRot * 0.017453292F)) + (this.random.nextDouble() * 0.6D - 0.3D);
         this.level.addParticle(ParticleTypes.ENTITY_EFFECT, var1, var3, var5, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
      }

   }

   protected boolean isImmobile() {
      return super.isImmobile() || this.attackTick > 0 || this.stunnedTick > 0 || this.roarTick > 0;
   }

   public boolean canSee(Entity entity) {
      return this.stunnedTick <= 0 && this.roarTick <= 0?super.canSee(entity):false;
   }

   protected void blockedByShield(LivingEntity livingEntity) {
      if(this.roarTick == 0) {
         if(this.random.nextDouble() < 0.5D) {
            this.stunnedTick = 40;
            this.playSound(SoundEvents.RAVAGER_STUNNED, 1.0F, 1.0F);
            this.level.broadcastEntityEvent(this, (byte)39);
            livingEntity.push(this);
         } else {
            this.strongKnockback(livingEntity);
         }

         livingEntity.hurtMarked = true;
      }

   }

   private void roar() {
      if(this.isAlive()) {
         for(Entity var3 : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D), NO_RAVAGER_AND_ALIVE)) {
            if(!(var3 instanceof AbstractIllager)) {
               var3.hurt(DamageSource.mobAttack(this), 6.0F);
            }

            this.strongKnockback(var3);
         }

         Vec3 var2 = this.getBoundingBox().getCenter();

         for(int var3 = 0; var3 < 40; ++var3) {
            double var4 = this.random.nextGaussian() * 0.2D;
            double var6 = this.random.nextGaussian() * 0.2D;
            double var8 = this.random.nextGaussian() * 0.2D;
            this.level.addParticle(ParticleTypes.POOF, var2.x, var2.y, var2.z, var4, var6, var8);
         }
      }

   }

   private void strongKnockback(Entity entity) {
      double var2 = entity.x - this.x;
      double var4 = entity.z - this.z;
      double var6 = Math.max(var2 * var2 + var4 * var4, 0.001D);
      entity.push(var2 / var6 * 4.0D, 0.2D, var4 / var6 * 4.0D);
   }

   public void handleEntityEvent(byte b) {
      if(b == 4) {
         this.attackTick = 10;
         this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
      } else if(b == 39) {
         this.stunnedTick = 40;
      }

      super.handleEntityEvent(b);
   }

   public int getAttackTick() {
      return this.attackTick;
   }

   public int getStunnedTick() {
      return this.stunnedTick;
   }

   public int getRoarTick() {
      return this.roarTick;
   }

   public boolean doHurtTarget(Entity entity) {
      this.attackTick = 10;
      this.level.broadcastEntityEvent(this, (byte)4);
      this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
      return super.doHurtTarget(entity);
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return SoundEvents.RAVAGER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.RAVAGER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.RAVAGER_DEATH;
   }

   protected void playStepSound(BlockPos blockPos, BlockState blockState) {
      this.playSound(SoundEvents.RAVAGER_STEP, 0.15F, 1.0F);
   }

   public boolean checkSpawnObstruction(LevelReader levelReader) {
      return !levelReader.containsAnyLiquid(this.getBoundingBox());
   }

   public void applyRaidBuffs(int var1, boolean var2) {
   }

   public boolean canBeLeader() {
      return false;
   }

   class RavagerMeleeAttackGoal extends MeleeAttackGoal {
      public RavagerMeleeAttackGoal() {
         super(Ravager.this, 1.0D, true);
      }

      protected double getAttackReachSqr(LivingEntity livingEntity) {
         float var2 = Ravager.this.getBbWidth() - 0.1F;
         return (double)(var2 * 2.0F * var2 * 2.0F + livingEntity.getBbWidth());
      }
   }

   static class RavagerNavigation extends GroundPathNavigation {
      public RavagerNavigation(Mob mob, Level level) {
         super(mob, level);
      }

      protected PathFinder createPathFinder(int i) {
         this.nodeEvaluator = new Ravager.RavagerNodeEvaluator();
         return new PathFinder(this.nodeEvaluator, i);
      }
   }

   static class RavagerNodeEvaluator extends WalkNodeEvaluator {
      private RavagerNodeEvaluator() {
      }

      protected BlockPathTypes evaluateBlockPathType(BlockGetter blockGetter, boolean var2, boolean var3, BlockPos blockPos, BlockPathTypes var5) {
         return var5 == BlockPathTypes.LEAVES?BlockPathTypes.OPEN:super.evaluateBlockPathType(blockGetter, var2, var3, blockPos, var5);
      }
   }
}
