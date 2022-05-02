package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class InsideBrownianWalk extends Behavior {
   private final float speed;

   public InsideBrownianWalk(float speed) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.speed = speed;
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, PathfinderMob pathfinderMob) {
      return !serverLevel.canSeeSky(new BlockPos(pathfinderMob));
   }

   protected void start(ServerLevel serverLevel, PathfinderMob pathfinderMob, long var3) {
      BlockPos var5 = new BlockPos(pathfinderMob);
      List<BlockPos> var6 = (List)BlockPos.betweenClosedStream(var5.offset(-1, -1, -1), var5.offset(1, 1, 1)).map(BlockPos::immutable).collect(Collectors.toList());
      Collections.shuffle(var6);
      Optional<BlockPos> var7 = var6.stream().filter((blockPos) -> {
         return !serverLevel.canSeeSky(blockPos);
      }).filter((blockPos) -> {
         return serverLevel.loadedAndEntityCanStandOn(blockPos, pathfinderMob);
      }).filter((blockPos) -> {
         return serverLevel.noCollision(pathfinderMob);
      }).findFirst();
      var7.ifPresent((blockPos) -> {
         pathfinderMob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(blockPos, this.speed, 0)));
      });
   }
}
