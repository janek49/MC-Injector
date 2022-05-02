package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class StrollToPoi extends Behavior {
   private final MemoryModuleType memoryType;
   private final int closeEnoughDist;
   private final int maxDistanceFromPoi;
   private long nextOkStartTime;

   public StrollToPoi(MemoryModuleType memoryType, int closeEnoughDist, int maxDistanceFromPoi) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, memoryType, MemoryStatus.VALUE_PRESENT));
      this.memoryType = memoryType;
      this.closeEnoughDist = closeEnoughDist;
      this.maxDistanceFromPoi = maxDistanceFromPoi;
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, PathfinderMob pathfinderMob) {
      Optional<GlobalPos> var3 = pathfinderMob.getBrain().getMemory(this.memoryType);
      return var3.isPresent() && Objects.equals(serverLevel.getDimension().getType(), ((GlobalPos)var3.get()).dimension()) && ((GlobalPos)var3.get()).pos().closerThan(pathfinderMob.position(), (double)this.maxDistanceFromPoi);
   }

   protected void start(ServerLevel serverLevel, PathfinderMob pathfinderMob, long var3) {
      if(var3 > this.nextOkStartTime) {
         Brain<?> var5 = pathfinderMob.getBrain();
         Optional<GlobalPos> var6 = var5.getMemory(this.memoryType);
         var6.ifPresent((globalPos) -> {
            var5.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(globalPos.pos(), 0.4F, this.closeEnoughDist)));
         });
         this.nextOkStartTime = var3 + 80L;
      }

   }
}
