package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class WitherSkeleton extends AbstractSkeleton {
   public WitherSkeleton(EntityType entityType, Level level) {
      super(entityType, level);
      this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.WITHER_SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.WITHER_SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WITHER_SKELETON_DEATH;
   }

   SoundEvent getStepSound() {
      return SoundEvents.WITHER_SKELETON_STEP;
   }

   protected void dropCustomDeathLoot(DamageSource damageSource, int var2, boolean var3) {
      super.dropCustomDeathLoot(damageSource, var2, var3);
      Entity var4 = damageSource.getEntity();
      if(var4 instanceof Creeper) {
         Creeper var5 = (Creeper)var4;
         if(var5.canDropMobsSkull()) {
            var5.increaseDroppedSkulls();
            this.spawnAtLocation(Items.WITHER_SKELETON_SKULL);
         }
      }

   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
   }

   protected void populateDefaultEquipmentEnchantments(DifficultyInstance difficultyInstance) {
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      SpawnGroupData var6 = super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
      this.reassessWeaponGoal();
      return var6;
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return 2.1F;
   }

   public boolean doHurtTarget(Entity entity) {
      if(!super.doHurtTarget(entity)) {
         return false;
      } else {
         if(entity instanceof LivingEntity) {
            ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.WITHER, 200));
         }

         return true;
      }
   }

   protected AbstractArrow getArrow(ItemStack itemStack, float var2) {
      AbstractArrow abstractArrow = super.getArrow(itemStack, var2);
      abstractArrow.setSecondsOnFire(100);
      return abstractArrow;
   }

   public boolean canBeAffected(MobEffectInstance mobEffectInstance) {
      return mobEffectInstance.getEffect() == MobEffects.WITHER?false:super.canBeAffected(mobEffectInstance);
   }
}
