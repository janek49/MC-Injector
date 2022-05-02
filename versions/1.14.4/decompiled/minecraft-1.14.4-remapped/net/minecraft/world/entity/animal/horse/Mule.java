package net.minecraft.world.entity.animal.horse;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.level.Level;

public class Mule extends AbstractChestedHorse {
   public Mule(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.MULE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.MULE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      super.getHurtSound(damageSource);
      return SoundEvents.MULE_HURT;
   }

   protected void playChestEquipsSound() {
      this.playSound(SoundEvents.MULE_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
   }
}
