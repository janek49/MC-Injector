package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TradeWithVillager extends Behavior {
   private Set trades = ImmutableSet.of();

   public TradeWithVillager() {
      super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
      return BehaviorUtils.targetIsValid(villager.getBrain(), MemoryModuleType.INTERACTION_TARGET, EntityType.VILLAGER);
   }

   protected boolean canStillUse(ServerLevel serverLevel, Villager villager, long var3) {
      return this.checkExtraStartConditions(serverLevel, villager);
   }

   protected void start(ServerLevel serverLevel, Villager villager, long var3) {
      Villager villager = (Villager)villager.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
      BehaviorUtils.lockGazeAndWalkToEachOther(villager, villager);
      this.trades = figureOutWhatIAmWillingToTrade(villager, villager);
   }

   protected void tick(ServerLevel serverLevel, Villager villager, long var3) {
      Villager villager = (Villager)villager.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
      if(villager.distanceToSqr(villager) <= 5.0D) {
         BehaviorUtils.lockGazeAndWalkToEachOther(villager, villager);
         villager.gossip(villager, var3);
         if(villager.hasExcessFood() && (villager.getVillagerData().getProfession() == VillagerProfession.FARMER || villager.wantsMoreFood())) {
            throwHalfStack(villager, Villager.FOOD_POINTS.keySet(), villager);
         }

         if(!this.trades.isEmpty() && villager.getInventory().hasAnyOf(this.trades)) {
            throwHalfStack(villager, this.trades, villager);
         }

      }
   }

   protected void stop(ServerLevel serverLevel, Villager villager, long var3) {
      villager.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
   }

   private static Set figureOutWhatIAmWillingToTrade(Villager var0, Villager var1) {
      ImmutableSet<Item> var2 = var1.getVillagerData().getProfession().getRequestedItems();
      ImmutableSet<Item> var3 = var0.getVillagerData().getProfession().getRequestedItems();
      return (Set)var2.stream().filter((item) -> {
         return !var3.contains(item);
      }).collect(Collectors.toSet());
   }

   private static void throwHalfStack(Villager villager, Set set, LivingEntity livingEntity) {
      SimpleContainer var3 = villager.getInventory();
      ItemStack var4 = ItemStack.EMPTY;
      int var5 = 0;

      while(var5 < var3.getContainerSize()) {
         ItemStack var6;
         Item var7;
         int var8;
         label22: {
            var6 = var3.getItem(var5);
            if(!var6.isEmpty()) {
               var7 = var6.getItem();
               if(set.contains(var7)) {
                  if(var6.getCount() > var6.getMaxStackSize() / 2) {
                     var8 = var6.getCount() / 2;
                     break label22;
                  }

                  if(var6.getCount() > 24) {
                     var8 = var6.getCount() - 24;
                     break label22;
                  }
               }
            }

            ++var5;
            continue;
         }

         var6.shrink(var8);
         var4 = new ItemStack(var7, var8);
         break;
      }

      if(!var4.isEmpty()) {
         BehaviorUtils.throwItem(villager, var4, livingEntity);
      }

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

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Villager)var2, var3);
   }
}
