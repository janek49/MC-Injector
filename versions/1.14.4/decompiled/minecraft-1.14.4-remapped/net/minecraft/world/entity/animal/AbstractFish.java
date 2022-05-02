package net.minecraft.world.entity.animal;

import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractFish extends WaterAnimal {
   private static final EntityDataAccessor FROM_BUCKET = SynchedEntityData.defineId(AbstractFish.class, EntityDataSerializers.BOOLEAN);

   public AbstractFish(EntityType entityType, Level level) {
      super(entityType, level);
      this.moveControl = new AbstractFish.FishMoveControl(this);
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return entityDimensions.height * 0.65F;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
   }

   public boolean requiresCustomPersistence() {
      return this.fromBucket();
   }

   public static boolean checkFishSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      return levelAccessor.getBlockState(blockPos).getBlock() == Blocks.WATER && levelAccessor.getBlockState(blockPos.above()).getBlock() == Blocks.WATER;
   }

   public boolean removeWhenFarAway(double d) {
      return !this.fromBucket() && !this.hasCustomName();
   }

   public int getMaxSpawnClusterSize() {
      return 8;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(FROM_BUCKET, Boolean.valueOf(false));
   }

   private boolean fromBucket() {
      return ((Boolean)this.entityData.get(FROM_BUCKET)).booleanValue();
   }

   public void setFromBucket(boolean fromBucket) {
      this.entityData.set(FROM_BUCKET, Boolean.valueOf(fromBucket));
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putBoolean("FromBucket", this.fromBucket());
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.setFromBucket(compoundTag.getBoolean("FromBucket"));
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
      GoalSelector var10000 = this.goalSelector;
      Predicate var10009 = EntitySelector.NO_SPECTATORS;
      EntitySelector.NO_SPECTATORS.getClass();
      var10000.addGoal(2, new AvoidEntityGoal(this, Player.class, 8.0F, 1.6D, 1.4D, var10009::test));
      this.goalSelector.addGoal(4, new AbstractFish.FishSwimGoal(this));
   }

   protected PathNavigation createNavigation(Level level) {
      return new WaterBoundPathNavigation(this, level);
   }

   public void travel(Vec3 vec3) {
      if(this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(0.01F, vec3);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
         if(this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
         }
      } else {
         super.travel(vec3);
      }

   }

   public void aiStep() {
      if(!this.isInWater() && this.onGround && this.verticalCollision) {
         this.setDeltaMovement(this.getDeltaMovement().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F), 0.4000000059604645D, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F)));
         this.onGround = false;
         this.hasImpulse = true;
         this.playSound(this.getFlopSound(), this.getSoundVolume(), this.getVoicePitch());
      }

      super.aiStep();
   }

   protected boolean mobInteract(Player player, InteractionHand interactionHand) {
      ItemStack var3 = player.getItemInHand(interactionHand);
      if(var3.getItem() == Items.WATER_BUCKET && this.isAlive()) {
         this.playSound(SoundEvents.BUCKET_FILL_FISH, 1.0F, 1.0F);
         var3.shrink(1);
         ItemStack var4 = this.getBucketItemStack();
         this.saveToBucketTag(var4);
         if(!this.level.isClientSide) {
            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, var4);
         }

         if(var3.isEmpty()) {
            player.setItemInHand(interactionHand, var4);
         } else if(!player.inventory.add(var4)) {
            player.drop(var4, false);
         }

         this.remove();
         return true;
      } else {
         return super.mobInteract(player, interactionHand);
      }
   }

   protected void saveToBucketTag(ItemStack itemStack) {
      if(this.hasCustomName()) {
         itemStack.setHoverName(this.getCustomName());
      }

   }

   protected abstract ItemStack getBucketItemStack();

   protected boolean canRandomSwim() {
      return true;
   }

   protected abstract SoundEvent getFlopSound();

   protected SoundEvent getSwimSound() {
      return SoundEvents.FISH_SWIM;
   }

   static class FishMoveControl extends MoveControl {
      private final AbstractFish fish;

      FishMoveControl(AbstractFish fish) {
         super(fish);
         this.fish = fish;
      }

      public void tick() {
         if(this.fish.isUnderLiquid(FluidTags.WATER)) {
            this.fish.setDeltaMovement(this.fish.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
         }

         if(this.operation == MoveControl.Operation.MOVE_TO && !this.fish.getNavigation().isDone()) {
            double var1 = this.wantedX - this.fish.x;
            double var3 = this.wantedY - this.fish.y;
            double var5 = this.wantedZ - this.fish.z;
            double var7 = (double)Mth.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
            var3 = var3 / var7;
            float var9 = (float)(Mth.atan2(var5, var1) * 57.2957763671875D) - 90.0F;
            this.fish.yRot = this.rotlerp(this.fish.yRot, var9, 90.0F);
            this.fish.yBodyRot = this.fish.yRot;
            float var10 = (float)(this.speedModifier * this.fish.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            this.fish.setSpeed(Mth.lerp(0.125F, this.fish.getSpeed(), var10));
            this.fish.setDeltaMovement(this.fish.getDeltaMovement().add(0.0D, (double)this.fish.getSpeed() * var3 * 0.1D, 0.0D));
         } else {
            this.fish.setSpeed(0.0F);
         }
      }
   }

   static class FishSwimGoal extends RandomSwimmingGoal {
      private final AbstractFish fish;

      public FishSwimGoal(AbstractFish fish) {
         super(fish, 1.0D, 40);
         this.fish = fish;
      }

      public boolean canUse() {
         return this.fish.canRandomSwim() && super.canUse();
      }
   }
}
