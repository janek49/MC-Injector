package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class StrollAroundPoi extends Behavior {
   private final MemoryModuleType memoryType;
   private long nextOkStartTime;
   private final int maxDistanceFromPoi;

   public StrollAroundPoi(MemoryModuleType memoryType, int maxDistanceFromPoi) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, memoryType, MemoryStatus.VALUE_PRESENT));
      this.memoryType = memoryType;
      this.maxDistanceFromPoi = maxDistanceFromPoi;
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, PathfinderMob pathfinderMob) {
      Optional<GlobalPos> var3 = pathfinderMob.getBrain().getMemory(this.memoryType);
      return var3.isPresent() && Objects.equals(serverLevel.getDimension().getType(), ((GlobalPos)var3.get()).dimension()) && ((GlobalPos)var3.get()).pos().closerThan(pathfinderMob.position(), (double)this.maxDistanceFromPoi);
   }

   protected void start(ServerLevel serverLevel, PathfinderMob pathfinderMob, long var3) {
      if(var3 > this.nextOkStartTime) {
         Optional<Vec3> var5 = Optional.ofNullable(RandomPos.getLandPos(pathfinderMob, 8, 6));
         pathfinderMob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, var5.map((vec3) -> {
            return new WalkTarget(vec3, 0.4F, 1);
         }));
         this.nextOkStartTime = var3 + 180L;
      }

   }
}
