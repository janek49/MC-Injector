package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.SkeletonTrapGoal;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

public class SkeletonHorse extends AbstractHorse {
   private final SkeletonTrapGoal skeletonTrapGoal = new SkeletonTrapGoal(this);
   private boolean isTrap;
   private int trapTime;

   public SkeletonHorse(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
      this.getAttribute(JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength());
   }

   protected void addBehaviourGoals() {
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return this.isUnderLiquid(FluidTags.WATER)?SoundEvents.SKELETON_HORSE_AMBIENT_WATER:SoundEvents.SKELETON_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.SKELETON_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      super.getHurtSound(damageSource);
      return SoundEvents.SKELETON_HORSE_HURT;
   }

   protected SoundEvent getSwimSound() {
      if(this.onGround) {
         if(!this.isVehicle()) {
            return SoundEvents.SKELETON_HORSE_STEP_WATER;
         }

         ++this.gallopSoundCounter;
         if(this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
            return SoundEvents.SKELETON_HORSE_GALLOP_WATER;
         }

         if(this.gallopSoundCounter <= 5) {
            return SoundEvents.SKELETON_HORSE_STEP_WATER;
         }
      }

      return SoundEvents.SKELETON_HORSE_SWIM;
   }

   protected void playSwimSound(float f) {
      if(this.onGround) {
         super.playSwimSound(0.3F);
      } else {
         super.playSwimSound(Math.min(0.1F, f * 25.0F));
      }

   }

   protected void playJumpSound() {
      if(this.isInWater()) {
         this.playSound(SoundEvents.SKELETON_HORSE_JUMP_WATER, 0.4F, 1.0F);
      } else {
         super.playJumpSound();
      }

   }

   public MobType getMobType() {
      return MobType.UNDEAD;
   }

   public double getRideHeight() {
      return super.getRideHeight() - 0.1875D;
   }

   public void aiStep() {
      super.aiStep();
      if(this.isTrap() && this.trapTime++ >= 18000) {
         this.remove();
      }

   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putBoolean("SkeletonTrap", this.isTrap());
      compoundTag.putInt("SkeletonTrapTime", this.trapTime);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.setTrap(compoundTag.getBoolean("SkeletonTrap"));
      this.trapTime = compoundTag.getInt("SkeletonTrapTime");
   }

   public boolean rideableUnderWater() {
      return true;
   }

   protected float getWaterSlowDown() {
      return 0.96F;
   }

   public boolean isTrap() {
      return this.isTrap;
   }

   public void setTrap(boolean trap) {
      if(trap != this.isTrap) {
         this.isTrap = trap;
         if(trap) {
            this.goalSelector.addGoal(1, this.skeletonTrapGoal);
         } else {
            this.goalSelector.removeGoal(this.skeletonTrapGoal);
         }

      }
   }

   @Nullable
   public AgableMob getBreedOffspring(AgableMob agableMob) {
      return (AgableMob)EntityType.SKELETON_HORSE.create(this.level);
   }

   public boolean mobInteract(Player player, InteractionHand interactionHand) {
      ItemStack var3 = player.getItemInHand(interactionHand);
      if(var3.getItem() instanceof SpawnEggItem) {
         return super.mobInteract(player, interactionHand);
      } else if(!this.isTamed()) {
         return false;
      } else if(this.isBaby()) {
         return super.mobInteract(player, interactionHand);
      } else if(player.isSneaking()) {
         this.openInventory(player);
         return true;
      } else if(this.isVehicle()) {
         return super.mobInteract(player, interactionHand);
      } else {
         if(!var3.isEmpty()) {
            if(var3.getItem() == Items.SADDLE && !this.isSaddled()) {
               this.openInventory(player);
               return true;
            }

            if(var3.interactEnemy(player, this, interactionHand)) {
               return true;
            }
         }

         this.doPlayerRide(player);
         return true;
      }
   }
}
