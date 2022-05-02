package net.minecraft.world.entity.monster;

import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;

public abstract class Monster extends PathfinderMob implements Enemy {
   protected Monster(EntityType entityType, Level level) {
      super(entityType, level);
      this.xpReward = 5;
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   public void aiStep() {
      this.updateSwingTime();
      this.updateNoActionTime();
      super.aiStep();
   }

   protected void updateNoActionTime() {
      float var1 = this.getBrightness();
      if(var1 > 0.5F) {
         this.noActionTime += 2;
      }

   }

   public void tick() {
      super.tick();
      if(!this.level.isClientSide && this.level.getDifficulty() == Difficulty.PEACEFUL) {
         this.remove();
      }

   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.HOSTILE_SWIM;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.HOSTILE_SPLASH;
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      return this.isInvulnerableTo(damageSource)?false:super.hurt(damageSource, var2);
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.HOSTILE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.HOSTILE_DEATH;
   }

   protected SoundEvent getFallDamageSound(int i) {
      return i > 4?SoundEvents.HOSTILE_BIG_FALL:SoundEvents.HOSTILE_SMALL_FALL;
   }

   public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
      return 0.5F - levelReader.getBrightness(blockPos);
   }

   public static boolean isDarkEnoughToSpawn(LevelAccessor levelAccessor, BlockPos blockPos, Random random) {
      if(levelAccessor.getBrightness(LightLayer.SKY, blockPos) > random.nextInt(32)) {
         return false;
      } else {
         int var3 = levelAccessor.getLevel().isThundering()?levelAccessor.getMaxLocalRawBrightness(blockPos, 10):levelAccessor.getMaxLocalRawBrightness(blockPos);
         return var3 <= random.nextInt(8);
      }
   }

   public static boolean checkMonsterSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      return levelAccessor.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(levelAccessor, blockPos, random) && checkMobSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, random);
   }

   public static boolean checkAnyLightMonsterSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      return levelAccessor.getDifficulty() != Difficulty.PEACEFUL && checkMobSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, random);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
   }

   protected boolean shouldDropExperience() {
      return true;
   }

   public boolean isPreventingPlayerRest(Player player) {
      return true;
   }

   public ItemStack getProjectile(ItemStack itemStack) {
      if(itemStack.getItem() instanceof ProjectileWeaponItem) {
         Predicate<ItemStack> var2 = ((ProjectileWeaponItem)itemStack.getItem()).getSupportedHeldProjectiles();
         ItemStack var3 = ProjectileWeaponItem.getHeldProjectile(this, var2);
         return var3.isEmpty()?new ItemStack(Items.ARROW):var3;
      } else {
         return ItemStack.EMPTY;
      }
   }
}
