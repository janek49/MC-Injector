package net.minecraft.world.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Skeleton extends AbstractSkeleton {
   public Skeleton(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SKELETON_DEATH;
   }

   SoundEvent getStepSound() {
      return SoundEvents.SKELETON_STEP;
   }

   protected void dropCustomDeathLoot(DamageSource damageSource, int var2, boolean var3) {
      super.dropCustomDeathLoot(damageSource, var2, var3);
      Entity var4 = damageSource.getEntity();
      if(var4 instanceof Creeper) {
         Creeper var5 = (Creeper)var4;
         if(var5.canDropMobsSkull()) {
            var5.increaseDroppedSkulls();
            this.spawnAtLocation(Items.SKELETON_SKULL);
         }
      }

   }
}
