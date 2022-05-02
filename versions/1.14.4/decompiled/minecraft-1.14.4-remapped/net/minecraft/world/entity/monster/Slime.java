package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;

public class Slime extends Mob implements Enemy {
   private static final EntityDataAccessor ID_SIZE = SynchedEntityData.defineId(Slime.class, EntityDataSerializers.INT);
   public float targetSquish;
   public float squish;
   public float oSquish;
   private boolean wasOnGround;

   public Slime(EntityType entityType, Level level) {
      super(entityType, level);
      this.moveControl = new Slime.SlimeMoveControl(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new Slime.SlimeFloatGoal(this));
      this.goalSelector.addGoal(2, new Slime.SlimeAttackGoal(this));
      this.goalSelector.addGoal(3, new Slime.SlimeRandomDirectionGoal(this));
      this.goalSelector.addGoal(5, new Slime.SlimeKeepOnJumpingGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, (livingEntity) -> {
         return Math.abs(livingEntity.y - this.y) <= 4.0D;
      }));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ID_SIZE, Integer.valueOf(1));
   }

   protected void setSize(int xpReward, boolean var2) {
      this.entityData.set(ID_SIZE, Integer.valueOf(xpReward));
      this.setPos(this.x, this.y, this.z);
      this.refreshDimensions();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)(xpReward * xpReward));
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)xpReward));
      if(var2) {
         this.setHealth(this.getMaxHealth());
      }

      this.xpReward = xpReward;
   }

   public int getSize() {
      return ((Integer)this.entityData.get(ID_SIZE)).intValue();
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("Size", this.getSize() - 1);
      compoundTag.putBoolean("wasOnGround", this.wasOnGround);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      int var2 = compoundTag.getInt("Size");
      if(var2 < 0) {
         var2 = 0;
      }

      this.setSize(var2 + 1, false);
      this.wasOnGround = compoundTag.getBoolean("wasOnGround");
   }

   public boolean isTiny() {
      return this.getSize() <= 1;
   }

   protected ParticleOptions getParticleType() {
      return ParticleTypes.ITEM_SLIME;
   }

   public void tick() {
      if(!this.level.isClientSide && this.level.getDifficulty() == Difficulty.PEACEFUL && this.getSize() > 0) {
         this.removed = true;
      }

      this.squish += (this.targetSquish - this.squish) * 0.5F;
      this.oSquish = this.squish;
      super.tick();
      if(this.onGround && !this.wasOnGround) {
         int var1 = this.getSize();

         for(int var2 = 0; var2 < var1 * 8; ++var2) {
            float var3 = this.random.nextFloat() * 6.2831855F;
            float var4 = this.random.nextFloat() * 0.5F + 0.5F;
            float var5 = Mth.sin(var3) * (float)var1 * 0.5F * var4;
            float var6 = Mth.cos(var3) * (float)var1 * 0.5F * var4;
            Level var10000 = this.level;
            ParticleOptions var10001 = this.getParticleType();
            double var10002 = this.x + (double)var5;
            double var10004 = this.z + (double)var6;
            var10000.addParticle(var10001, var10002, this.getBoundingBox().minY, var10004, 0.0D, 0.0D, 0.0D);
         }

         this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         this.targetSquish = -0.5F;
      } else if(!this.onGround && this.wasOnGround) {
         this.targetSquish = 1.0F;
      }

      this.wasOnGround = this.onGround;
      this.decreaseSquish();
   }

   protected void decreaseSquish() {
      this.targetSquish *= 0.6F;
   }

   protected int getJumpDelay() {
      return this.random.nextInt(20) + 10;
   }

   public void onSyncedDataUpdated(EntityDataAccessor entityDataAccessor) {
      if(ID_SIZE.equals(entityDataAccessor)) {
         this.refreshDimensions();
         this.yRot = this.yHeadRot;
         this.yBodyRot = this.yHeadRot;
         if(this.isInWater() && this.random.nextInt(20) == 0) {
            this.doWaterSplashEffect();
         }
      }

      super.onSyncedDataUpdated(entityDataAccessor);
   }

   public EntityType getType() {
      return super.getType();
   }

   public void remove() {
      int var1 = this.getSize();
      if(!this.level.isClientSide && var1 > 1 && this.getHealth() <= 0.0F) {
         int var2 = 2 + this.random.nextInt(3);

         for(int var3 = 0; var3 < var2; ++var3) {
            float var4 = ((float)(var3 % 2) - 0.5F) * (float)var1 / 4.0F;
            float var5 = ((float)(var3 / 2) - 0.5F) * (float)var1 / 4.0F;
            Slime var6 = (Slime)this.getType().create(this.level);
            if(this.hasCustomName()) {
               var6.setCustomName(this.getCustomName());
            }

            if(this.isPersistenceRequired()) {
               var6.setPersistenceRequired();
            }

            var6.setSize(var1 / 2, true);
            var6.moveTo(this.x + (double)var4, this.y + 0.5D, this.z + (double)var5, this.random.nextFloat() * 360.0F, 0.0F);
            this.level.addFreshEntity(var6);
         }
      }

      super.remove();
   }

   public void push(Entity entity) {
      super.push(entity);
      if(entity instanceof IronGolem && this.isDealsDamage()) {
         this.dealDamage((LivingEntity)entity);
      }

   }

   public void playerTouch(Player player) {
      if(this.isDealsDamage()) {
         this.dealDamage(player);
      }

   }

   protected void dealDamage(LivingEntity livingEntity) {
      if(this.isAlive()) {
         int var2 = this.getSize();
         if(this.distanceToSqr(livingEntity) < 0.6D * (double)var2 * 0.6D * (double)var2 && this.canSee(livingEntity) && livingEntity.hurt(DamageSource.mobAttack(this), (float)this.getAttackDamage())) {
            this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.doEnchantDamageEffects(this, livingEntity);
         }
      }

   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return 0.625F * entityDimensions.height;
   }

   protected boolean isDealsDamage() {
      return !this.isTiny() && this.isEffectiveAi();
   }

   protected int getAttackDamage() {
      return this.getSize();
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return this.isTiny()?SoundEvents.SLIME_HURT_SMALL:SoundEvents.SLIME_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isTiny()?SoundEvents.SLIME_DEATH_SMALL:SoundEvents.SLIME_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return this.isTiny()?SoundEvents.SLIME_SQUISH_SMALL:SoundEvents.SLIME_SQUISH;
   }

   protected ResourceLocation getDefaultLootTable() {
      return this.getSize() == 1?this.getType().getDefaultLootTable():BuiltInLootTables.EMPTY;
   }

   public static boolean checkSlimeSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      if(levelAccessor.getLevelData().getGeneratorType() == LevelType.FLAT && random.nextInt(4) != 1) {
         return false;
      } else {
         if(levelAccessor.getDifficulty() != Difficulty.PEACEFUL) {
            Biome var5 = levelAccessor.getBiome(blockPos);
            if(var5 == Biomes.SWAMP && blockPos.getY() > 50 && blockPos.getY() < 70 && random.nextFloat() < 0.5F && random.nextFloat() < levelAccessor.getMoonBrightness() && levelAccessor.getMaxLocalRawBrightness(blockPos) <= random.nextInt(8)) {
               return checkMobSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, random);
            }

            ChunkPos var6 = new ChunkPos(blockPos);
            boolean var7 = WorldgenRandom.seedSlimeChunk(var6.x, var6.z, levelAccessor.getSeed(), 987234911L).nextInt(10) == 0;
            if(random.nextInt(10) == 0 && var7 && blockPos.getY() < 40) {
               return checkMobSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, random);
            }
         }

         return false;
      }
   }

   protected float getSoundVolume() {
      return 0.4F * (float)this.getSize();
   }

   public int getMaxHeadXRot() {
      return 0;
   }

   protected boolean doPlayJumpSound() {
      return this.getSize() > 0;
   }

   protected void jumpFromGround() {
      Vec3 var1 = this.getDeltaMovement();
      this.setDeltaMovement(var1.x, 0.41999998688697815D, var1.z);
      this.hasImpulse = true;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      int var6 = this.random.nextInt(3);
      if(var6 < 2 && this.random.nextFloat() < 0.5F * difficultyInstance.getSpecialMultiplier()) {
         ++var6;
      }

      int var7 = 1 << var6;
      this.setSize(var7, true);
      return super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
   }

   protected SoundEvent getJumpSound() {
      return this.isTiny()?SoundEvents.SLIME_JUMP_SMALL:SoundEvents.SLIME_JUMP;
   }

   public EntityDimensions getDimensions(Pose pose) {
      return super.getDimensions(pose).scale(0.255F * (float)this.getSize());
   }

   static class SlimeAttackGoal extends Goal {
      private final Slime slime;
      private int growTiredTimer;

      public SlimeAttackGoal(Slime slime) {
         this.slime = slime;
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity var1 = this.slime.getTarget();
         return var1 == null?false:(!var1.isAlive()?false:(var1 instanceof Player && ((Player)var1).abilities.invulnerable?false:this.slime.getMoveControl() instanceof Slime.SlimeMoveControl));
      }

      public void start() {
         this.growTiredTimer = 300;
         super.start();
      }

      public boolean canContinueToUse() {
         LivingEntity var1 = this.slime.getTarget();
         return var1 == null?false:(!var1.isAlive()?false:(var1 instanceof Player && ((Player)var1).abilities.invulnerable?false:--this.growTiredTimer > 0));
      }

      public void tick() {
         this.slime.lookAt(this.slime.getTarget(), 10.0F, 10.0F);
         ((Slime.SlimeMoveControl)this.slime.getMoveControl()).setDirection(this.slime.yRot, this.slime.isDealsDamage());
      }
   }

   static class SlimeFloatGoal extends Goal {
      private final Slime slime;

      public SlimeFloatGoal(Slime slime) {
         this.slime = slime;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
         slime.getNavigation().setCanFloat(true);
      }

      public boolean canUse() {
         return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof Slime.SlimeMoveControl;
      }

      public void tick() {
         if(this.slime.getRandom().nextFloat() < 0.8F) {
            this.slime.getJumpControl().jump();
         }

         ((Slime.SlimeMoveControl)this.slime.getMoveControl()).setWantedMovement(1.2D);
      }
   }

   static class SlimeKeepOnJumpingGoal extends Goal {
      private final Slime slime;

      public SlimeKeepOnJumpingGoal(Slime slime) {
         this.slime = slime;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      public boolean canUse() {
         return !this.slime.isPassenger();
      }

      public void tick() {
         ((Slime.SlimeMoveControl)this.slime.getMoveControl()).setWantedMovement(1.0D);
      }
   }

   static class SlimeMoveControl extends MoveControl {
      private float yRot;
      private int jumpDelay;
      private final Slime slime;
      private boolean isAggressive;

      public SlimeMoveControl(Slime slime) {
         super(slime);
         this.slime = slime;
         this.yRot = 180.0F * slime.yRot / 3.1415927F;
      }

      public void setDirection(float yRot, boolean isAggressive) {
         this.yRot = yRot;
         this.isAggressive = isAggressive;
      }

      public void setWantedMovement(double wantedMovement) {
         this.speedModifier = wantedMovement;
         this.operation = MoveControl.Operation.MOVE_TO;
      }

      public void tick() {
         this.mob.yRot = this.rotlerp(this.mob.yRot, this.yRot, 90.0F);
         this.mob.yHeadRot = this.mob.yRot;
         this.mob.yBodyRot = this.mob.yRot;
         if(this.operation != MoveControl.Operation.MOVE_TO) {
            this.mob.setZza(0.0F);
         } else {
            this.operation = MoveControl.Operation.WAIT;
            if(this.mob.onGround) {
               this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
               if(this.jumpDelay-- <= 0) {
                  this.jumpDelay = this.slime.getJumpDelay();
                  if(this.isAggressive) {
                     this.jumpDelay /= 3;
                  }

                  this.slime.getJumpControl().jump();
                  if(this.slime.doPlayJumpSound()) {
                     this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), ((this.slime.getRandom().nextFloat() - this.slime.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F);
                  }
               } else {
                  this.slime.xxa = 0.0F;
                  this.slime.zza = 0.0F;
                  this.mob.setSpeed(0.0F);
               }
            } else {
               this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
            }

         }
      }
   }

   static class SlimeRandomDirectionGoal extends Goal {
      private final Slime slime;
      private float chosenDegrees;
      private int nextRandomizeTime;

      public SlimeRandomDirectionGoal(Slime slime) {
         this.slime = slime;
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return this.slime.getTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION)) && this.slime.getMoveControl() instanceof Slime.SlimeMoveControl;
      }

      public void tick() {
         if(--this.nextRandomizeTime <= 0) {
            this.nextRandomizeTime = 40 + this.slime.getRandom().nextInt(60);
            this.chosenDegrees = (float)this.slime.getRandom().nextInt(360);
         }

         ((Slime.SlimeMoveControl)this.slime.getMoveControl()).setDirection(this.chosenDegrees, false);
      }
   }
}
