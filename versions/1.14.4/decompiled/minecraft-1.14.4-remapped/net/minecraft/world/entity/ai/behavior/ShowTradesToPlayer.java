package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityPosWrapper;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

public class ShowTradesToPlayer extends Behavior {
   @Nullable
   private ItemStack playerItemStack;
   private final List displayItems = Lists.newArrayList();
   private int cycleCounter;
   private int displayIndex;
   private int lookTime;

   public ShowTradesToPlayer(int var1, int var2) {
      super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT), var1, var2);
   }

   public boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
      Brain<?> var3 = villager.getBrain();
      if(!var3.getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent()) {
         return false;
      } else {
         LivingEntity var4 = (LivingEntity)var3.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
         return var4.getType() == EntityType.PLAYER && villager.isAlive() && var4.isAlive() && !villager.isBaby() && villager.distanceToSqr(var4) <= 17.0D;
      }
   }

   public boolean canStillUse(ServerLevel serverLevel, Villager villager, long var3) {
      return this.checkExtraStartConditions(serverLevel, villager) && this.lookTime > 0 && villager.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
   }

   public void start(ServerLevel serverLevel, Villager villager, long var3) {
      super.start(serverLevel, villager, var3);
      this.lookAtTarget(villager);
      this.cycleCounter = 0;
      this.displayIndex = 0;
      this.lookTime = 40;
   }

   public void tick(ServerLevel serverLevel, Villager villager, long var3) {
      LivingEntity var5 = this.lookAtTarget(villager);
      this.findItemsToDisplay(var5, villager);
      if(!this.displayItems.isEmpty()) {
         this.displayCyclingItems(villager);
      } else {
         villager.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
         this.lookTime = Math.min(this.lookTime, 40);
      }

      --this.lookTime;
   }

   public void stop(ServerLevel serverLevel, Villager villager, long var3) {
      super.stop(serverLevel, villager, var3);
      villager.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
      villager.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      this.playerItemStack = null;
   }

   private void findItemsToDisplay(LivingEntity livingEntity, Villager villager) {
      boolean var3 = false;
      ItemStack var4 = livingEntity.getMainHandItem();
      if(this.playerItemStack == null || !ItemStack.isSame(this.playerItemStack, var4)) {
         this.playerItemStack = var4;
         var3 = true;
         this.displayItems.clear();
      }

      if(var3 && !this.playerItemStack.isEmpty()) {
         this.updateDisplayItems(villager);
         if(!this.displayItems.isEmpty()) {
            this.lookTime = 900;
            this.displayFirstItem(villager);
         }
      }

   }

   private void displayFirstItem(Villager villager) {
      villager.setItemSlot(EquipmentSlot.MAINHAND, (ItemStack)this.displayItems.get(0));
   }

   private void updateDisplayItems(Villager villager) {
      for(MerchantOffer var3 : villager.getOffers()) {
         if(!var3.isOutOfStock() && this.playerItemStackMatchesCostOfOffer(var3)) {
            this.displayItems.add(var3.getResult());
         }
      }

   }

   private boolean playerItemStackMatchesCostOfOffer(MerchantOffer merchantOffer) {
      return ItemStack.isSame(this.playerItemStack, merchantOffer.getCostA()) || ItemStack.isSame(this.playerItemStack, merchantOffer.getCostB());
   }

   private LivingEntity lookAtTarget(Villager villager) {
      Brain<?> var2 = villager.getBrain();
      LivingEntity var3 = (LivingEntity)var2.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
      var2.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityPosWrapper(var3)));
      return var3;
   }

   private void displayCyclingItems(Villager villager) {
      if(this.displayItems.size() >= 2 && ++this.cycleCounter >= 40) {
         ++this.displayIndex;
         this.cycleCounter = 0;
         if(this.displayIndex > this.displayItems.size() - 1) {
            this.displayIndex = 0;
         }

         villager.setItemSlot(EquipmentSlot.MAINHAND, (ItemStack)this.displayItems.get(this.displayIndex));
      }

   }

   // $FF: synthetic method
   public boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   public void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   public void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   public void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Villager)var2, var3);
   }
}
