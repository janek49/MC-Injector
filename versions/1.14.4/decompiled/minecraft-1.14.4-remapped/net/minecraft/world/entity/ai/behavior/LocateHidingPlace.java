package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class LocateHidingPlace extends Behavior {
   private final float speed;
   private final int radius;
   private final int closeEnoughDist;
   private Optional currentPos = Optional.empty();

   public LocateHidingPlace(int radius, float speed, int closeEnoughDist) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryStatus.REGISTERED, MemoryModuleType.HIDING_PLACE, MemoryStatus.REGISTERED));
      this.radius = radius;
      this.speed = speed;
      this.closeEnoughDist = closeEnoughDist;
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
      Optional<BlockPos> var3 = serverLevel.getPoiManager().find((poiType) -> {
         return poiType == PoiType.HOME;
      }, (blockPos) -> {
         return true;
      }, new BlockPos(livingEntity), this.closeEnoughDist + 1, PoiManager.Occupancy.ANY);
      if(var3.isPresent() && ((BlockPos)var3.get()).closerThan(livingEntity.position(), (double)this.closeEnoughDist)) {
         this.currentPos = var3;
      } else {
         this.currentPos = Optional.empty();
      }

      return true;
   }

   protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      Brain<?> var5 = livingEntity.getBrain();
      Optional<BlockPos> var6 = this.currentPos;
      if(!var6.isPresent()) {
         var6 = serverLevel.getPoiManager().getRandom((poiType) -> {
            return poiType == PoiType.HOME;
         }, (blockPos) -> {
            return true;
         }, PoiManager.Occupancy.ANY, new BlockPos(livingEntity), this.radius, livingEntity.getRandom());
         if(!var6.isPresent()) {
            Optional<GlobalPos> var7 = var5.getMemory(MemoryModuleType.HOME);
            if(var7.isPresent()) {
               var6 = Optional.of(((GlobalPos)var7.get()).pos());
            }
         }
      }

      if(var6.isPresent()) {
         var5.eraseMemory(MemoryModuleType.PATH);
         var5.eraseMemory(MemoryModuleType.LOOK_TARGET);
         var5.eraseMemory(MemoryModuleType.BREED_TARGET);
         var5.eraseMemory(MemoryModuleType.INTERACTION_TARGET);
         var5.setMemory(MemoryModuleType.HIDING_PLACE, (Object)GlobalPos.of(serverLevel.getDimension().getType(), (BlockPos)var6.get()));
         if(!((BlockPos)var6.get()).closerThan(livingEntity.position(), (double)this.closeEnoughDist)) {
            var5.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget((BlockPos)var6.get(), this.speed, this.closeEnoughDist)));
         }
      }

   }
}
