package net.minecraft.world.entity.animal;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.TurtleNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class Turtle extends Animal {
   private static final EntityDataAccessor HOME_POS = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BLOCK_POS);
   private static final EntityDataAccessor HAS_EGG = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor LAYING_EGG = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor TRAVEL_POS = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BLOCK_POS);
   private static final EntityDataAccessor GOING_HOME = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor TRAVELLING = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
   private int layEggCounter;
   public static final Predicate BABY_ON_LAND_SELECTOR = (livingEntity) -> {
      return livingEntity.isBaby() && !livingEntity.isInWater();
   };

   public Turtle(EntityType entityType, Level level) {
      super(entityType, level);
      this.moveControl = new Turtle.TurtleMoveControl(this);
      this.maxUpStep = 1.0F;
   }

   public void setHomePos(BlockPos homePos) {
      this.entityData.set(HOME_POS, homePos);
   }

   private BlockPos getHomePos() {
      return (BlockPos)this.entityData.get(HOME_POS);
   }

   private void setTravelPos(BlockPos travelPos) {
      this.entityData.set(TRAVEL_POS, travelPos);
   }

   private BlockPos getTravelPos() {
      return (BlockPos)this.entityData.get(TRAVEL_POS);
   }

   public boolean hasEgg() {
      return ((Boolean)this.entityData.get(HAS_EGG)).booleanValue();
   }

   private void setHasEgg(boolean hasEgg) {
      this.entityData.set(HAS_EGG, Boolean.valueOf(hasEgg));
   }

   public boolean isLayingEgg() {
      return ((Boolean)this.entityData.get(LAYING_EGG)).booleanValue();
   }

   private void setLayingEgg(boolean layingEgg) {
      this.layEggCounter = layingEgg?1:0;
      this.entityData.set(LAYING_EGG, Boolean.valueOf(layingEgg));
   }

   private boolean isGoingHome() {
      return ((Boolean)this.entityData.get(GOING_HOME)).booleanValue();
   }

   private void setGoingHome(boolean goingHome) {
      this.entityData.set(GOING_HOME, Boolean.valueOf(goingHome));
   }

   private boolean isTravelling() {
      return ((Boolean)this.entityData.get(TRAVELLING)).booleanValue();
   }

   private void setTravelling(boolean travelling) {
      this.entityData.set(TRAVELLING, Boolean.valueOf(travelling));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(HOME_POS, BlockPos.ZERO);
      this.entityData.define(HAS_EGG, Boolean.valueOf(false));
      this.entityData.define(TRAVEL_POS, BlockPos.ZERO);
      this.entityData.define(GOING_HOME, Boolean.valueOf(false));
      this.entityData.define(TRAVELLING, Boolean.valueOf(false));
      this.entityData.define(LAYING_EGG, Boolean.valueOf(false));
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("HomePosX", this.getHomePos().getX());
      compoundTag.putInt("HomePosY", this.getHomePos().getY());
      compoundTag.putInt("HomePosZ", this.getHomePos().getZ());
      compoundTag.putBoolean("HasEgg", this.hasEgg());
      compoundTag.putInt("TravelPosX", this.getTravelPos().getX());
      compoundTag.putInt("TravelPosY", this.getTravelPos().getY());
      compoundTag.putInt("TravelPosZ", this.getTravelPos().getZ());
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      int var2 = compoundTag.getInt("HomePosX");
      int var3 = compoundTag.getInt("HomePosY");
      int var4 = compoundTag.getInt("HomePosZ");
      this.setHomePos(new BlockPos(var2, var3, var4));
      super.readAdditionalSaveData(compoundTag);
      this.setHasEgg(compoundTag.getBoolean("HasEgg"));
      int var5 = compoundTag.getInt("TravelPosX");
      int var6 = compoundTag.getInt("TravelPosY");
      int var7 = compoundTag.getInt("TravelPosZ");
      this.setTravelPos(new BlockPos(var5, var6, var7));
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      this.setHomePos(new BlockPos(this));
      this.setTravelPos(BlockPos.ZERO);
      return super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
   }

   public static boolean checkTurtleSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      return blockPos.getY() < levelAccessor.getSeaLevel() + 4 && levelAccessor.getBlockState(blockPos.below()).getBlock() == Blocks.SAND && levelAccessor.getRawBrightness(blockPos, 0) > 8;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new Turtle.TurtlePanicGoal(this, 1.2D));
      this.goalSelector.addGoal(1, new Turtle.TurtleBreedGoal(this, 1.0D));
      this.goalSelector.addGoal(1, new Turtle.TurtleLayEggGoal(this, 1.0D));
      this.goalSelector.addGoal(2, new Turtle.TurtleTemptGoal(this, 1.1D, Blocks.SEAGRASS.asItem()));
      this.goalSelector.addGoal(3, new Turtle.TurtleGoToWaterGoal(this, 1.0D));
      this.goalSelector.addGoal(4, new Turtle.TurtleGoHomeGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new Turtle.TurtleTravelGoal(this, 1.0D));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(9, new Turtle.TurtleRandomStrollGoal(this, 1.0D, 100));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   public boolean isPushedByWater() {
      return false;
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public MobType getMobType() {
      return MobType.WATER;
   }

   public int getAmbientSoundInterval() {
      return 200;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return !this.isInWater() && this.onGround && !this.isBaby()?SoundEvents.TURTLE_AMBIENT_LAND:super.getAmbientSound();
   }

   protected void playSwimSound(float f) {
      super.playSwimSound(f * 1.5F);
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.TURTLE_SWIM;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return this.isBaby()?SoundEvents.TURTLE_HURT_BABY:SoundEvents.TURTLE_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return this.isBaby()?SoundEvents.TURTLE_DEATH_BABY:SoundEvents.TURTLE_DEATH;
   }

   protected void playStepSound(BlockPos blockPos, BlockState blockState) {
      SoundEvent var3 = this.isBaby()?SoundEvents.TURTLE_SHAMBLE_BABY:SoundEvents.TURTLE_SHAMBLE;
      this.playSound(var3, 0.15F, 1.0F);
   }

   public boolean canFallInLove() {
      return super.canFallInLove() && !this.hasEgg();
   }

   protected float nextStep() {
      return this.moveDist + 0.15F;
   }

   public float getScale() {
      return this.isBaby()?0.3F:1.0F;
   }

   protected PathNavigation createNavigation(Level level) {
      return new Turtle.TurtlePathNavigation(this, level);
   }

   @Nullable
   public AgableMob getBreedOffspring(AgableMob agableMob) {
      return (AgableMob)EntityType.TURTLE.create(this.level);
   }

   public boolean isFood(ItemStack itemStack) {
      return itemStack.getItem() == Blocks.SEAGRASS.asItem();
   }

   public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
      return !this.isGoingHome() && levelReader.getFluidState(blockPos).is(FluidTags.WATER)?10.0F:(levelReader.getBlockState(blockPos.below()).getBlock() == Blocks.SAND?10.0F:levelReader.getBrightness(blockPos) - 0.5F);
   }

   public void aiStep() {
      super.aiStep();
      if(this.isAlive() && this.isLayingEgg() && this.layEggCounter >= 1 && this.layEggCounter % 5 == 0) {
         BlockPos var1 = new BlockPos(this);
         if(this.level.getBlockState(var1.below()).getBlock() == Blocks.SAND) {
            this.level.levelEvent(2001, var1, Block.getId(Blocks.SAND.defaultBlockState()));
         }
      }

   }

   protected void ageBoundaryReached() {
      super.ageBoundaryReached();
      if(!this.isBaby() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
         this.spawnAtLocation(Items.SCUTE, 1);
      }

   }

   public void travel(Vec3 vec3) {
      if(this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(0.1F, vec3);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
         if(this.getTarget() == null && (!this.isGoingHome() || !this.getHomePos().closerThan(this.position(), 20.0D))) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
         }
      } else {
         super.travel(vec3);
      }

   }

   public boolean canBeLeashed(Player player) {
      return false;
   }

   public void thunderHit(LightningBolt lightningBolt) {
      this.hurt(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
   }

   static class TurtleBreedGoal extends BreedGoal {
      private final Turtle turtle;

      TurtleBreedGoal(Turtle turtle, double var2) {
         super(turtle, var2);
         this.turtle = turtle;
      }

      public boolean canUse() {
         return super.canUse() && !this.turtle.hasEgg();
      }

      protected void breed() {
         ServerPlayer var1 = this.animal.getLoveCause();
         if(var1 == null && this.partner.getLoveCause() != null) {
            var1 = this.partner.getLoveCause();
         }

         if(var1 != null) {
            var1.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(var1, this.animal, this.partner, (AgableMob)null);
         }

         this.turtle.setHasEgg(true);
         this.animal.resetLove();
         this.partner.resetLove();
         Random var2 = this.animal.getRandom();
         if(this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.x, this.animal.y, this.animal.z, var2.nextInt(7) + 1));
         }

      }
   }

   static class TurtleGoHomeGoal extends Goal {
      private final Turtle turtle;
      private final double speedModifier;
      private boolean stuck;
      private int closeToHomeTryTicks;

      TurtleGoHomeGoal(Turtle turtle, double speedModifier) {
         this.turtle = turtle;
         this.speedModifier = speedModifier;
      }

      public boolean canUse() {
         return this.turtle.isBaby()?false:(this.turtle.hasEgg()?true:(this.turtle.getRandom().nextInt(700) != 0?false:!this.turtle.getHomePos().closerThan(this.turtle.position(), 64.0D)));
      }

      public void start() {
         this.turtle.setGoingHome(true);
         this.stuck = false;
         this.closeToHomeTryTicks = 0;
      }

      public void stop() {
         this.turtle.setGoingHome(false);
      }

      public boolean canContinueToUse() {
         return !this.turtle.getHomePos().closerThan(this.turtle.position(), 7.0D) && !this.stuck && this.closeToHomeTryTicks <= 600;
      }

      public void tick() {
         BlockPos var1 = this.turtle.getHomePos();
         boolean var2 = var1.closerThan(this.turtle.position(), 16.0D);
         if(var2) {
            ++this.closeToHomeTryTicks;
         }

         if(this.turtle.getNavigation().isDone()) {
            Vec3 var3 = RandomPos.getPosTowards(this.turtle, 16, 3, new Vec3((double)var1.getX(), (double)var1.getY(), (double)var1.getZ()), 0.3141592741012573D);
            if(var3 == null) {
               var3 = RandomPos.getPosTowards(this.turtle, 8, 7, new Vec3((double)var1.getX(), (double)var1.getY(), (double)var1.getZ()));
            }

            if(var3 != null && !var2 && this.turtle.level.getBlockState(new BlockPos(var3)).getBlock() != Blocks.WATER) {
               var3 = RandomPos.getPosTowards(this.turtle, 16, 5, new Vec3((double)var1.getX(), (double)var1.getY(), (double)var1.getZ()));
            }

            if(var3 == null) {
               this.stuck = true;
               return;
            }

            this.turtle.getNavigation().moveTo(var3.x, var3.y, var3.z, this.speedModifier);
         }

      }
   }

   static class TurtleGoToWaterGoal extends MoveToBlockGoal {
      private final Turtle turtle;

      private TurtleGoToWaterGoal(Turtle turtle, double var2) {
         super(turtle, turtle.isBaby()?2.0D:var2, 24);
         this.turtle = turtle;
         this.verticalSearchStart = -1;
      }

      public boolean canContinueToUse() {
         return !this.turtle.isInWater() && this.tryTicks <= 1200 && this.isValidTarget(this.turtle.level, this.blockPos);
      }

      public boolean canUse() {
         return this.turtle.isBaby() && !this.turtle.isInWater()?super.canUse():(!this.turtle.isGoingHome() && !this.turtle.isInWater() && !this.turtle.hasEgg()?super.canUse():false);
      }

      public boolean shouldRecalculatePath() {
         return this.tryTicks % 160 == 0;
      }

      protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
         Block var3 = levelReader.getBlockState(blockPos).getBlock();
         return var3 == Blocks.WATER;
      }
   }

   static class TurtleLayEggGoal extends MoveToBlockGoal {
      private final Turtle turtle;

      TurtleLayEggGoal(Turtle turtle, double var2) {
         super(turtle, var2, 16);
         this.turtle = turtle;
      }

      public boolean canUse() {
         return this.turtle.hasEgg() && this.turtle.getHomePos().closerThan(this.turtle.position(), 9.0D)?super.canUse():false;
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.turtle.hasEgg() && this.turtle.getHomePos().closerThan(this.turtle.position(), 9.0D);
      }

      public void tick() {
         super.tick();
         BlockPos var1 = new BlockPos(this.turtle);
         if(!this.turtle.isInWater() && this.isReachedTarget()) {
            if(this.turtle.layEggCounter < 1) {
               this.turtle.setLayingEgg(true);
            } else if(this.turtle.layEggCounter > 200) {
               Level var2 = this.turtle.level;
               var2.playSound((Player)null, (BlockPos)var1, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + var2.random.nextFloat() * 0.2F);
               var2.setBlock(this.blockPos.above(), (BlockState)Blocks.TURTLE_EGG.defaultBlockState().setValue(TurtleEggBlock.EGGS, Integer.valueOf(this.turtle.random.nextInt(4) + 1)), 3);
               this.turtle.setHasEgg(false);
               this.turtle.setLayingEgg(false);
               this.turtle.setInLoveTime(600);
            }

            if(this.turtle.isLayingEgg()) {
               this.turtle.layEggCounter++;
            }
         }

      }

      protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
         if(!levelReader.isEmptyBlock(blockPos.above())) {
            return false;
         } else {
            Block var3 = levelReader.getBlockState(blockPos).getBlock();
            return var3 == Blocks.SAND;
         }
      }
   }

   static class TurtleMoveControl extends MoveControl {
      private final Turtle turtle;

      TurtleMoveControl(Turtle turtle) {
         super(turtle);
         this.turtle = turtle;
      }

      private void updateSpeed() {
         if(this.turtle.isInWater()) {
            this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
            if(!this.turtle.getHomePos().closerThan(this.turtle.position(), 16.0D)) {
               this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 2.0F, 0.08F));
            }

            if(this.turtle.isBaby()) {
               this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 3.0F, 0.06F));
            }
         } else if(this.turtle.onGround) {
            this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 2.0F, 0.06F));
         }

      }

      public void tick() {
         this.updateSpeed();
         if(this.operation == MoveControl.Operation.MOVE_TO && !this.turtle.getNavigation().isDone()) {
            double var1 = this.wantedX - this.turtle.x;
            double var3 = this.wantedY - this.turtle.y;
            double var5 = this.wantedZ - this.turtle.z;
            double var7 = (double)Mth.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
            var3 = var3 / var7;
            float var9 = (float)(Mth.atan2(var5, var1) * 57.2957763671875D) - 90.0F;
            this.turtle.yRot = this.rotlerp(this.turtle.yRot, var9, 90.0F);
            this.turtle.yBodyRot = this.turtle.yRot;
            float var10 = (float)(this.speedModifier * this.turtle.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            this.turtle.setSpeed(Mth.lerp(0.125F, this.turtle.getSpeed(), var10));
            this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0D, (double)this.turtle.getSpeed() * var3 * 0.1D, 0.0D));
         } else {
            this.turtle.setSpeed(0.0F);
         }
      }
   }

   static class TurtlePanicGoal extends PanicGoal {
      TurtlePanicGoal(Turtle turtle, double var2) {
         super(turtle, var2);
      }

      public boolean canUse() {
         if(this.mob.getLastHurtByMob() == null && !this.mob.isOnFire()) {
            return false;
         } else {
            BlockPos var1 = this.lookForWater(this.mob.level, this.mob, 7, 4);
            if(var1 != null) {
               this.posX = (double)var1.getX();
               this.posY = (double)var1.getY();
               this.posZ = (double)var1.getZ();
               return true;
            } else {
               return this.findRandomPosition();
            }
         }
      }
   }

   static class TurtlePathNavigation extends WaterBoundPathNavigation {
      TurtlePathNavigation(Turtle turtle, Level level) {
         super(turtle, level);
      }

      protected boolean canUpdatePath() {
         return true;
      }

      protected PathFinder createPathFinder(int i) {
         return new PathFinder(new TurtleNodeEvaluator(), i);
      }

      public boolean isStableDestination(BlockPos blockPos) {
         if(this.mob instanceof Turtle) {
            Turtle var2 = (Turtle)this.mob;
            if(var2.isTravelling()) {
               return this.level.getBlockState(blockPos).getBlock() == Blocks.WATER;
            }
         }

         return !this.level.getBlockState(blockPos.below()).isAir();
      }
   }

   static class TurtleRandomStrollGoal extends RandomStrollGoal {
      private final Turtle turtle;

      private TurtleRandomStrollGoal(Turtle turtle, double var2, int var4) {
         super(turtle, var2, var4);
         this.turtle = turtle;
      }

      public boolean canUse() {
         return !this.mob.isInWater() && !this.turtle.isGoingHome() && !this.turtle.hasEgg()?super.canUse():false;
      }
   }

   static class TurtleTemptGoal extends Goal {
      private static final TargetingConditions TEMPT_TARGETING = (new TargetingConditions()).range(10.0D).allowSameTeam().allowInvulnerable();
      private final Turtle turtle;
      private final double speedModifier;
      private Player player;
      private int calmDown;
      private final Set items;

      TurtleTemptGoal(Turtle turtle, double speedModifier, Item item) {
         this.turtle = turtle;
         this.speedModifier = speedModifier;
         this.items = Sets.newHashSet(new Item[]{item});
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         if(this.calmDown > 0) {
            --this.calmDown;
            return false;
         } else {
            this.player = this.turtle.level.getNearestPlayer(TEMPT_TARGETING, this.turtle);
            return this.player == null?false:this.shouldFollowItem(this.player.getMainHandItem()) || this.shouldFollowItem(this.player.getOffhandItem());
         }
      }

      private boolean shouldFollowItem(ItemStack itemStack) {
         return this.items.contains(itemStack.getItem());
      }

      public boolean canContinueToUse() {
         return this.canUse();
      }

      public void stop() {
         this.player = null;
         this.turtle.getNavigation().stop();
         this.calmDown = 100;
      }

      public void tick() {
         this.turtle.getLookControl().setLookAt(this.player, (float)(this.turtle.getMaxHeadYRot() + 20), (float)this.turtle.getMaxHeadXRot());
         if(this.turtle.distanceToSqr(this.player) < 6.25D) {
            this.turtle.getNavigation().stop();
         } else {
            this.turtle.getNavigation().moveTo((Entity)this.player, this.speedModifier);
         }

      }
   }

   static class TurtleTravelGoal extends Goal {
      private final Turtle turtle;
      private final double speedModifier;
      private boolean stuck;

      TurtleTravelGoal(Turtle turtle, double speedModifier) {
         this.turtle = turtle;
         this.speedModifier = speedModifier;
      }

      public boolean canUse() {
         return !this.turtle.isGoingHome() && !this.turtle.hasEgg() && this.turtle.isInWater();
      }

      public void start() {
         int var1 = 512;
         int var2 = 4;
         Random var3 = this.turtle.random;
         int var4 = var3.nextInt(1025) - 512;
         int var5 = var3.nextInt(9) - 4;
         int var6 = var3.nextInt(1025) - 512;
         if((double)var5 + this.turtle.y > (double)(this.turtle.level.getSeaLevel() - 1)) {
            var5 = 0;
         }

         BlockPos var7 = new BlockPos((double)var4 + this.turtle.x, (double)var5 + this.turtle.y, (double)var6 + this.turtle.z);
         this.turtle.setTravelPos(var7);
         this.turtle.setTravelling(true);
         this.stuck = false;
      }

      public void tick() {
         if(this.turtle.getNavigation().isDone()) {
            BlockPos var1 = this.turtle.getTravelPos();
            Vec3 var2 = RandomPos.getPosTowards(this.turtle, 16, 3, new Vec3((double)var1.getX(), (double)var1.getY(), (double)var1.getZ()), 0.3141592741012573D);
            if(var2 == null) {
               var2 = RandomPos.getPosTowards(this.turtle, 8, 7, new Vec3((double)var1.getX(), (double)var1.getY(), (double)var1.getZ()));
            }

            if(var2 != null) {
               int var3 = Mth.floor(var2.x);
               int var4 = Mth.floor(var2.z);
               int var5 = 34;
               if(!this.turtle.level.hasChunksAt(var3 - 34, 0, var4 - 34, var3 + 34, 0, var4 + 34)) {
                  var2 = null;
               }
            }

            if(var2 == null) {
               this.stuck = true;
               return;
            }

            this.turtle.getNavigation().moveTo(var2.x, var2.y, var2.z, this.speedModifier);
         }

      }

      public boolean canContinueToUse() {
         return !this.turtle.getNavigation().isDone() && !this.stuck && !this.turtle.isGoingHome() && !this.turtle.isInLove() && !this.turtle.hasEgg();
      }

      public void stop() {
         this.turtle.setTravelling(false);
         super.stop();
      }
   }
}
