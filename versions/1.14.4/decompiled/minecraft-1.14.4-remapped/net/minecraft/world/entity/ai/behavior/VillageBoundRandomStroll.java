package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class VillageBoundRandomStroll extends Behavior {
   private final float speed;
   private final int maxXyDist;
   private final int maxYDist;

   public VillageBoundRandomStroll(float f) {
      this(f, 10, 7);
   }

   public VillageBoundRandomStroll(float speed, int maxXyDist, int maxYDist) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.speed = speed;
      this.maxXyDist = maxXyDist;
      this.maxYDist = maxYDist;
   }

   protected void start(ServerLevel serverLevel, PathfinderMob randomPos, long var3) {
      BlockPos var5 = new BlockPos(randomPos);
      if(serverLevel.isVillage(var5)) {
         this.setRandomPos(randomPos);
      } else {
         SectionPos var6 = SectionPos.of(var5);
         SectionPos var7 = BehaviorUtils.findSectionClosestToVillage(serverLevel, var6, 2);
         if(var7 != var6) {
            this.setTargetedPos(randomPos, var7);
         } else {
            this.setRandomPos(randomPos);
         }
      }

   }

   private void setTargetedPos(PathfinderMob pathfinderMob, SectionPos sectionPos) {
      BlockPos var3 = sectionPos.center();
      Optional<Vec3> var4 = Optional.ofNullable(RandomPos.getPosTowards(pathfinderMob, this.maxXyDist, this.maxYDist, new Vec3((double)var3.getX(), (double)var3.getY(), (double)var3.getZ())));
      pathfinderMob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, var4.map((vec3) -> {
         return new WalkTarget(vec3, this.speed, 0);
      }));
   }

   private void setRandomPos(PathfinderMob randomPos) {
      Optional<Vec3> var2 = Optional.ofNullable(RandomPos.getLandPos(randomPos, this.maxXyDist, this.maxYDist));
      randomPos.getBrain().setMemory(MemoryModuleType.WALK_TARGET, var2.map((vec3) -> {
         return new WalkTarget(vec3, this.speed, 0);
      }));
   }
}
