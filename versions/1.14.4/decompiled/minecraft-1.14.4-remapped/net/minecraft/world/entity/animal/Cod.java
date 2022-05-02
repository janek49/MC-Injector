package net.minecraft.world.entity.animal;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Cod extends AbstractSchoolingFish {
   public Cod(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected ItemStack getBucketItemStack() {
      return new ItemStack(Items.COD_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.COD_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.COD_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.COD_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.COD_FLOP;
   }
}
