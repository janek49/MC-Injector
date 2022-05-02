package net.minecraft.world.entity.animal.horse;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class SkeletonTrapGoal extends Goal {
   private final SkeletonHorse horse;

   public SkeletonTrapGoal(SkeletonHorse horse) {
      this.horse = horse;
   }

   public boolean canUse() {
      return this.horse.level.hasNearbyAlivePlayer(this.horse.x, this.horse.y, this.horse.z, 10.0D);
   }

   public void tick() {
      DifficultyInstance var1 = this.horse.level.getCurrentDifficultyAt(new BlockPos(this.horse));
      this.horse.setTrap(false);
      this.horse.setTamed(true);
      this.horse.setAge(0);
      ((ServerLevel)this.horse.level).addGlobalEntity(new LightningBolt(this.horse.level, this.horse.x, this.horse.y, this.horse.z, true));
      Skeleton var2 = this.createSkeleton(var1, this.horse);
      var2.startRiding(this.horse);

      for(int var3 = 0; var3 < 3; ++var3) {
         AbstractHorse var4 = this.createHorse(var1);
         Skeleton var5 = this.createSkeleton(var1, var4);
         var5.startRiding(var4);
         var4.push(this.horse.getRandom().nextGaussian() * 0.5D, 0.0D, this.horse.getRandom().nextGaussian() * 0.5D);
      }

   }

   private AbstractHorse createHorse(DifficultyInstance difficultyInstance) {
      SkeletonHorse var2 = (SkeletonHorse)EntityType.SKELETON_HORSE.create(this.horse.level);
      var2.finalizeSpawn(this.horse.level, difficultyInstance, MobSpawnType.TRIGGERED, (SpawnGroupData)null, (CompoundTag)null);
      var2.setPos(this.horse.x, this.horse.y, this.horse.z);
      var2.invulnerableTime = 60;
      var2.setPersistenceRequired();
      var2.setTamed(true);
      var2.setAge(0);
      var2.level.addFreshEntity(var2);
      return var2;
   }

   private Skeleton createSkeleton(DifficultyInstance difficultyInstance, AbstractHorse abstractHorse) {
      Skeleton skeleton = (Skeleton)EntityType.SKELETON.create(abstractHorse.level);
      skeleton.finalizeSpawn(abstractHorse.level, difficultyInstance, MobSpawnType.TRIGGERED, (SpawnGroupData)null, (CompoundTag)null);
      skeleton.setPos(abstractHorse.x, abstractHorse.y, abstractHorse.z);
      skeleton.invulnerableTime = 60;
      skeleton.setPersistenceRequired();
      if(skeleton.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
         skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
      }

      skeleton.setItemSlot(EquipmentSlot.MAINHAND, EnchantmentHelper.enchantItem(skeleton.getRandom(), skeleton.getMainHandItem(), (int)(5.0F + difficultyInstance.getSpecialMultiplier() * (float)skeleton.getRandom().nextInt(18)), false));
      skeleton.setItemSlot(EquipmentSlot.HEAD, EnchantmentHelper.enchantItem(skeleton.getRandom(), skeleton.getItemBySlot(EquipmentSlot.HEAD), (int)(5.0F + difficultyInstance.getSpecialMultiplier() * (float)skeleton.getRandom().nextInt(18)), false));
      skeleton.level.addFreshEntity(skeleton);
      return skeleton;
   }
}
