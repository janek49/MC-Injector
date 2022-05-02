package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

public class ZombieHorse extends AbstractHorse {
   public ZombieHorse(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
      this.getAttribute(JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength());
   }

   public MobType getMobType() {
      return MobType.UNDEAD;
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.ZOMBIE_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ZOMBIE_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      super.getHurtSound(damageSource);
      return SoundEvents.ZOMBIE_HORSE_HURT;
   }

   @Nullable
   public AgableMob getBreedOffspring(AgableMob agableMob) {
      return (AgableMob)EntityType.ZOMBIE_HORSE.create(this.level);
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
            if(!this.isSaddled() && var3.getItem() == Items.SADDLE) {
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

   protected void addBehaviourGoals() {
   }
}
