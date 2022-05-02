package net.minecraft.world.entity.animal.horse;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.Level;

public class Donkey extends AbstractChestedHorse {
   public Donkey(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.DONKEY_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.DONKEY_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      super.getHurtSound(damageSource);
      return SoundEvents.DONKEY_HURT;
   }

   public boolean canMate(Animal animal) {
      return animal == this?false:(!(animal instanceof Donkey) && !(animal instanceof Horse)?false:this.canParent() && ((AbstractHorse)animal).canParent());
   }

   public AgableMob getBreedOffspring(AgableMob agableMob) {
      EntityType<? extends AbstractHorse> var2 = agableMob instanceof Horse?EntityType.MULE:EntityType.DONKEY;
      AbstractHorse var3 = (AbstractHorse)var2.create(this.level);
      this.setOffspringAttributes(agableMob, var3);
      return var3;
   }
}
