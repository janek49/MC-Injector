package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.pathfinder.Path;

public class MakeLove extends Behavior {
   private long birthTimestamp;

   public MakeLove() {
      super(ImmutableMap.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT), 350, 350);
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
      return this.isBreedingPossible(villager);
   }

   protected boolean canStillUse(ServerLevel serverLevel, Villager villager, long var3) {
      return var3 <= this.birthTimestamp && this.isBreedingPossible(villager);
   }

   protected void start(ServerLevel serverLevel, Villager villager, long var3) {
      Villager villager = this.getBreedingTarget(villager);
      BehaviorUtils.lockGazeAndWalkToEachOther(villager, villager);
      serverLevel.broadcastEntityEvent(villager, (byte)18);
      serverLevel.broadcastEntityEvent(villager, (byte)18);
      int var6 = 275 + villager.getRandom().nextInt(50);
      this.birthTimestamp = var3 + (long)var6;
   }

   protected void tick(ServerLevel serverLevel, Villager villager, long var3) {
      Villager villager = this.getBreedingTarget(villager);
      if(villager.distanceToSqr(villager) <= 5.0D) {
         BehaviorUtils.lockGazeAndWalkToEachOther(villager, villager);
         if(var3 >= this.birthTimestamp) {
            villager.eatAndDigestFood();
            villager.eatAndDigestFood();
            this.tryToGiveBirth(serverLevel, villager, villager);
         } else if(villager.getRandom().nextInt(35) == 0) {
            serverLevel.broadcastEntityEvent(villager, (byte)12);
            serverLevel.broadcastEntityEvent(villager, (byte)12);
         }

      }
   }

   private void tryToGiveBirth(ServerLevel serverLevel, Villager var2, Villager var3) {
      Optional<BlockPos> var4 = this.takeVacantBed(serverLevel, var2);
      if(!var4.isPresent()) {
         serverLevel.broadcastEntityEvent(var3, (byte)13);
         serverLevel.broadcastEntityEvent(var2, (byte)13);
      } else {
         Optional<Villager> var5 = this.breed(var2, var3);
         if(var5.isPresent()) {
            this.giveBedToChild(serverLevel, (Villager)var5.get(), (BlockPos)var4.get());
         } else {
            serverLevel.getPoiManager().release((BlockPos)var4.get());
         }
      }

   }

   protected void stop(ServerLevel serverLevel, Villager villager, long var3) {
      villager.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
   }

   private Villager getBreedingTarget(Villager villager) {
      return (Villager)villager.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
   }

   private boolean isBreedingPossible(Villager villager) {
      Brain<Villager> var2 = villager.getBrain();
      if(!var2.getMemory(MemoryModuleType.BREED_TARGET).isPresent()) {
         return false;
      } else {
         Villager var3 = this.getBreedingTarget(villager);
         return BehaviorUtils.targetIsValid(var2, MemoryModuleType.BREED_TARGET, EntityType.VILLAGER) && villager.canBreed() && var3.canBreed();
      }
   }

   private Optional takeVacantBed(ServerLevel serverLevel, Villager villager) {
      return serverLevel.getPoiManager().take(PoiType.HOME.getPredicate(), (blockPos) -> {
         return this.canReach(villager, blockPos);
      }, new BlockPos(villager), 48);
   }

   private boolean canReach(Villager villager, BlockPos blockPos) {
      Path var3 = villager.getNavigation().createPath(blockPos, PoiType.HOME.getValidRange());
      return var3 != null && var3.canReach();
   }

   private Optional breed(Villager var1, Villager var2) {
      Villager var3 = var1.getBreedOffspring(var2);
      if(var3 == null) {
         return Optional.empty();
      } else {
         var1.setAge(6000);
         var2.setAge(6000);
         var3.setAge(-24000);
         var3.moveTo(var1.x, var1.y, var1.z, 0.0F, 0.0F);
         var1.level.addFreshEntity(var3);
         var1.level.broadcastEntityEvent(var3, (byte)12);
         return Optional.of(var3);
      }
   }

   private void giveBedToChild(ServerLevel serverLevel, Villager villager, BlockPos blockPos) {
      GlobalPos var4 = GlobalPos.of(serverLevel.getDimension().getType(), blockPos);
      villager.getBrain().setMemory(MemoryModuleType.HOME, (Object)var4);
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
