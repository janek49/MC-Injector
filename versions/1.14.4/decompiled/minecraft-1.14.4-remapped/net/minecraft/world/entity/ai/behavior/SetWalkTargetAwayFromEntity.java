package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class SetWalkTargetAwayFromEntity extends Behavior {
   private final MemoryModuleType memory;
   private final float speed;

   public SetWalkTargetAwayFromEntity(MemoryModuleType memory, float speed) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, memory, MemoryStatus.VALUE_PRESENT));
      this.memory = memory;
      this.speed = speed;
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, PathfinderMob pathfinderMob) {
      Entity var3 = (Entity)pathfinderMob.getBrain().getMemory(this.memory).get();
      return pathfinderMob.distanceToSqr(var3) < 36.0D;
   }

   protected void start(ServerLevel serverLevel, PathfinderMob pathfinderMob, long var3) {
      Entity var5 = (Entity)pathfinderMob.getBrain().getMemory(this.memory).get();
      moveAwayFromMob(pathfinderMob, var5, this.speed);
   }

   public static void moveAwayFromMob(PathfinderMob pathfinderMob, Entity entity, float var2) {
      for(int var3 = 0; var3 < 10; ++var3) {
         Vec3 var4 = new Vec3(entity.x, entity.y, entity.z);
         Vec3 var5 = RandomPos.getLandPosAvoid(pathfinderMob, 16, 7, var4);
         if(var5 != null) {
            pathfinderMob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var5, var2, 0)));
            return;
         }
      }

   }
}
