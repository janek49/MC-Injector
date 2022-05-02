package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

public class SetWalkTargetFromBlockMemory extends Behavior {
   private final MemoryModuleType memoryType;
   private final float speed;
   private final int closeEnoughDist;
   private final int tooFarDistance;
   private final int tooLongUnreachableDuration;

   public SetWalkTargetFromBlockMemory(MemoryModuleType memoryType, float speed, int closeEnoughDist, int tooFarDistance, int tooLongUnreachableDuration) {
      super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, memoryType, MemoryStatus.VALUE_PRESENT));
      this.memoryType = memoryType;
      this.speed = speed;
      this.closeEnoughDist = closeEnoughDist;
      this.tooFarDistance = tooFarDistance;
      this.tooLongUnreachableDuration = tooLongUnreachableDuration;
   }

   private void dropPOI(Villager villager, long var2) {
      Brain<?> var4 = villager.getBrain();
      villager.releasePoi(this.memoryType);
      var4.eraseMemory(this.memoryType);
      var4.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object)Long.valueOf(var2));
   }

   protected void start(ServerLevel serverLevel, Villager villager, long var3) {
      Brain<?> var5 = villager.getBrain();
      var5.getMemory(this.memoryType).ifPresent((globalPos) -> {
         if(this.tiredOfTryingToFindTarget(serverLevel, villager)) {
            this.dropPOI(villager, var3);
         } else if(this.tooFar(serverLevel, villager, globalPos)) {
            Vec3 var7 = null;
            int var8 = 0;

            for(int var9 = 1000; var8 < 1000 && (var7 == null || this.tooFar(serverLevel, villager, GlobalPos.of(villager.dimension, new BlockPos(var7)))); ++var8) {
               var7 = RandomPos.getPosTowards(villager, 15, 7, new Vec3(globalPos.pos()));
            }

            if(var8 == 1000) {
               this.dropPOI(villager, var3);
               return;
            }

            var5.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var7, this.speed, this.closeEnoughDist)));
         } else if(!this.closeEnough(serverLevel, villager, globalPos)) {
            var5.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(globalPos.pos(), this.speed, this.closeEnoughDist)));
         }

      });
   }

   private boolean tiredOfTryingToFindTarget(ServerLevel serverLevel, Villager villager) {
      Optional<Long> var3 = villager.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      return var3.isPresent()?serverLevel.getGameTime() - ((Long)var3.get()).longValue() > (long)this.tooLongUnreachableDuration:false;
   }

   private boolean tooFar(ServerLevel serverLevel, Villager villager, GlobalPos globalPos) {
      return globalPos.dimension() != serverLevel.getDimension().getType() || globalPos.pos().distManhattan(new BlockPos(villager)) > this.tooFarDistance;
   }

   private boolean closeEnough(ServerLevel serverLevel, Villager villager, GlobalPos globalPos) {
      return globalPos.dimension() == serverLevel.getDimension().getType() && globalPos.pos().distManhattan(new BlockPos(villager)) <= this.closeEnoughDist;
   }
}
