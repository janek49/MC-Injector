package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.MoveToSkySeeingSpot;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Celebrate extends Behavior {
   @Nullable
   private Raid currentRaid;

   public Celebrate(int var1, int var2) {
      super(ImmutableMap.of(), var1, var2);
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
      this.currentRaid = serverLevel.getRaidAt(new BlockPos(villager));
      return this.currentRaid != null && this.currentRaid.isVictory() && MoveToSkySeeingSpot.hasNoBlocksAbove(serverLevel, villager);
   }

   protected boolean canStillUse(ServerLevel serverLevel, Villager villager, long var3) {
      return this.currentRaid != null && !this.currentRaid.isStopped();
   }

   protected void stop(ServerLevel serverLevel, Villager villager, long var3) {
      this.currentRaid = null;
      villager.getBrain().updateActivity(serverLevel.getDayTime(), serverLevel.getGameTime());
   }

   protected void tick(ServerLevel serverLevel, Villager villager, long var3) {
      Random var5 = villager.getRandom();
      if(var5.nextInt(100) == 0) {
         villager.playCelebrateSound();
      }

      if(var5.nextInt(200) == 0 && MoveToSkySeeingSpot.hasNoBlocksAbove(serverLevel, villager)) {
         DyeColor var6 = DyeColor.values()[var5.nextInt(DyeColor.values().length)];
         int var7 = var5.nextInt(3);
         ItemStack var8 = this.getFirework(var6, var7);
         FireworkRocketEntity var9 = new FireworkRocketEntity(villager.level, villager.x, villager.y + (double)villager.getEyeHeight(), villager.z, var8);
         villager.level.addFreshEntity(var9);
      }

   }

   private ItemStack getFirework(DyeColor dyeColor, int var2) {
      ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 1);
      ItemStack var4 = new ItemStack(Items.FIREWORK_STAR);
      CompoundTag var5 = var4.getOrCreateTagElement("Explosion");
      List<Integer> var6 = Lists.newArrayList();
      var6.add(Integer.valueOf(dyeColor.getFireworkColor()));
      var5.putIntArray("Colors", var6);
      var5.putByte("Type", (byte)FireworkRocketItem.Shape.BURST.getId());
      CompoundTag var7 = itemStack.getOrCreateTagElement("Fireworks");
      ListTag var8 = new ListTag();
      CompoundTag var9 = var4.getTagElement("Explosion");
      if(var9 != null) {
         var8.add(var9);
      }

      var7.putByte("Flight", (byte)var2);
      if(!var8.isEmpty()) {
         var7.put("Explosions", var8);
      }

      return itemStack;
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (Villager)var2, var3);
   }
}
