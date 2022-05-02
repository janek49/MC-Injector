package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class GiveGiftToHero extends Behavior {
   private static final Map gifts = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put(VillagerProfession.ARMORER, BuiltInLootTables.ARMORER_GIFT);
      hashMap.put(VillagerProfession.BUTCHER, BuiltInLootTables.BUTCHER_GIFT);
      hashMap.put(VillagerProfession.CARTOGRAPHER, BuiltInLootTables.CARTOGRAPHER_GIFT);
      hashMap.put(VillagerProfession.CLERIC, BuiltInLootTables.CLERIC_GIFT);
      hashMap.put(VillagerProfession.FARMER, BuiltInLootTables.FARMER_GIFT);
      hashMap.put(VillagerProfession.FISHERMAN, BuiltInLootTables.FISHERMAN_GIFT);
      hashMap.put(VillagerProfession.FLETCHER, BuiltInLootTables.FLETCHER_GIFT);
      hashMap.put(VillagerProfession.LEATHERWORKER, BuiltInLootTables.LEATHERWORKER_GIFT);
      hashMap.put(VillagerProfession.LIBRARIAN, BuiltInLootTables.LIBRARIAN_GIFT);
      hashMap.put(VillagerProfession.MASON, BuiltInLootTables.MASON_GIFT);
      hashMap.put(VillagerProfession.SHEPHERD, BuiltInLootTables.SHEPHERD_GIFT);
      hashMap.put(VillagerProfession.TOOLSMITH, BuiltInLootTables.TOOLSMITH_GIFT);
      hashMap.put(VillagerProfession.WEAPONSMITH, BuiltInLootTables.WEAPONSMITH_GIFT);
   });
   private int timeUntilNextGift = 600;
   private boolean giftGivenDuringThisRun;
   private long timeSinceStart;

   public GiveGiftToHero(int i) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryStatus.VALUE_PRESENT), i);
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
      if(!this.isHeroVisible(villager)) {
         return false;
      } else if(this.timeUntilNextGift > 0) {
         --this.timeUntilNextGift;
         return false;
      } else {
         return true;
      }
   }

   protected void start(ServerLevel serverLevel, Villager villager, long timeSinceStart) {
      this.giftGivenDuringThisRun = false;
      this.timeSinceStart = timeSinceStart;
      Player var5 = (Player)this.getNearestTargetableHero(villager).get();
      villager.getBrain().setMemory(MemoryModuleType.INTERACTION_TARGET, (Object)var5);
      BehaviorUtils.lookAtEntity(villager, var5);
   }

   protected boolean canStillUse(ServerLevel serverLevel, Villager villager, long var3) {
      return this.isHeroVisible(villager) && !this.giftGivenDuringThisRun;
   }

   protected void tick(ServerLevel serverLevel, Villager villager, long var3) {
      Player var5 = (Player)this.getNearestTargetableHero(villager).get();
      BehaviorUtils.lookAtEntity(villager, var5);
      if(this.isWithinThrowingDistance(villager, var5)) {
         if(var3 - this.timeSinceStart > 20L) {
            this.throwGift(villager, var5);
            this.giftGivenDuringThisRun = true;
         }
      } else {
         BehaviorUtils.walkToEntity(villager, var5, 5);
      }

   }

   protected void stop(ServerLevel serverLevel, Villager villager, long var3) {
      this.timeUntilNextGift = calculateTimeUntilNextGift(serverLevel);
      villager.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
      villager.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      villager.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
   }

   private void throwGift(Villager villager, LivingEntity livingEntity) {
      for(ItemStack var5 : this.getItemToThrow(villager)) {
         BehaviorUtils.throwItem(villager, var5, livingEntity);
      }

   }

   private List getItemToThrow(Villager villager) {
      if(villager.isBaby()) {
         return ImmutableList.of(new ItemStack(Items.POPPY));
      } else {
         VillagerProfession var2 = villager.getVillagerData().getProfession();
         if(gifts.containsKey(var2)) {
            LootTable var3 = villager.level.getServer().getLootTables().get((ResourceLocation)gifts.get(var2));
            LootContext.Builder var4 = (new LootContext.Builder((ServerLevel)villager.level)).withParameter(LootContextParams.BLOCK_POS, new BlockPos(villager)).withParameter(LootContextParams.THIS_ENTITY, villager).withRandom(villager.getRandom());
            return var3.getRandomItems(var4.create(LootContextParamSets.GIFT));
         } else {
            return ImmutableList.of(new ItemStack(Items.WHEAT_SEEDS));
         }
      }
   }

   private boolean isHeroVisible(Villager villager) {
      return this.getNearestTargetableHero(villager).isPresent();
   }

   private Optional getNearestTargetableHero(Villager villager) {
      return villager.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).filter(this::isHero);
   }

   private boolean isHero(Player player) {
      return player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE);
   }

   private boolean isWithinThrowingDistance(Villager villager, Player player) {
      BlockPos var3 = new BlockPos(player);
      BlockPos var4 = new BlockPos(villager);
      return var4.closerThan(var3, 5.0D);
   }

   private static int calculateTimeUntilNextGift(ServerLevel serverLevel) {
      return 600 + serverLevel.random.nextInt(6001);
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
