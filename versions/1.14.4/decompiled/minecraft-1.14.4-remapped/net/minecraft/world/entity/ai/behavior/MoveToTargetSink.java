package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class MoveToTargetSink extends Behavior {
   @Nullable
   private Path path;
   @Nullable
   private BlockPos lastTargetPos;
   private float speed;
   private int remainingDelay;

   public MoveToTargetSink(int i) {
      super(ImmutableMap.of(MemoryModuleType.PATH, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT), i);
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, Mob mob) {
      Brain<?> var3 = mob.getBrain();
      WalkTarget var4 = (WalkTarget)var3.getMemory(MemoryModuleType.WALK_TARGET).get();
      if(!this.reachedTarget(mob, var4) && this.tryComputePath(mob, var4, serverLevel.getGameTime())) {
         this.lastTargetPos = var4.getTarget().getPos();
         return true;
      } else {
         var3.eraseMemory(MemoryModuleType.WALK_TARGET);
         return false;
      }
   }

   protected boolean canStillUse(ServerLevel serverLevel, Mob mob, long var3) {
      if(this.path != null && this.lastTargetPos != null) {
         Optional<WalkTarget> var5 = mob.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
         PathNavigation var6 = mob.getNavigation();
         return !var6.isDone() && var5.isPresent() && !this.reachedTarget(mob, (WalkTarget)var5.get());
      } else {
         return false;
      }
   }

   protected void stop(ServerLevel serverLevel, Mob mob, long var3) {
      mob.getNavigation().stop();
      mob.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      mob.getBrain().eraseMemory(MemoryModuleType.PATH);
      this.path = null;
   }

   protected void start(ServerLevel serverLevel, Mob mob, long var3) {
      mob.getBrain().setMemory(MemoryModuleType.PATH, (Object)this.path);
      mob.getNavigation().moveTo(this.path, (double)this.speed);
      this.remainingDelay = serverLevel.getRandom().nextInt(10);
   }

   protected void tick(ServerLevel serverLevel, Mob mob, long var3) {
      --this.remainingDelay;
      if(this.remainingDelay <= 0) {
         Path var5 = mob.getNavigation().getPath();
         Brain<?> var6 = mob.getBrain();
         if(this.path != var5) {
            this.path = var5;
            var6.setMemory(MemoryModuleType.PATH, (Object)var5);
         }

         if(var5 != null && this.lastTargetPos != null) {
            WalkTarget var7 = (WalkTarget)var6.getMemory(MemoryModuleType.WALK_TARGET).get();
            if(var7.getTarget().getPos().distSqr(this.lastTargetPos) > 4.0D && this.tryComputePath(mob, var7, serverLevel.getGameTime())) {
               this.lastTargetPos = var7.getTarget().getPos();
               this.start(serverLevel, mob, var3);
            }

         }
      }
   }

   private boolean tryComputePath(Mob mob, WalkTarget walkTarget, long var3) {
      BlockPos var5 = walkTarget.getTarget().getPos();
      this.path = mob.getNavigation().createPath((BlockPos)var5, 0);
      this.speed = walkTarget.getSpeed();
      if(!this.reachedTarget(mob, walkTarget)) {
         Brain<?> var6 = mob.getBrain();
         boolean var7 = this.path != null && this.path.canReach();
         if(var7) {
            var6.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, Optional.empty());
         } else if(!var6.hasMemoryValue(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
            var6.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object)Long.valueOf(var3));
         }

         if(this.path != null) {
            return true;
         }

         Vec3 var8 = RandomPos.getPosTowards((PathfinderMob)mob, 10, 7, new Vec3(var5));
         if(var8 != null) {
            this.path = mob.getNavigation().createPath(var8.x, var8.y, var8.z, 0);
            return this.path != null;
         }
      }

      return false;
   }

   private boolean reachedTarget(Mob mob, WalkTarget walkTarget) {
      return walkTarget.getTarget().getPos().distManhattan(new BlockPos(mob)) <= walkTarget.getCloseEnoughDist();
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Mob)var2, var3);
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Mob)var2, var3);
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Mob)var2, var3);
   }
}
