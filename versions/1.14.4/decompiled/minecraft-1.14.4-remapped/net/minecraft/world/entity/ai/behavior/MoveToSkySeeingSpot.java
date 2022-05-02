package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class MoveToSkySeeingSpot extends Behavior {
   private final float speed;

   public MoveToSkySeeingSpot(float speed) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.speed = speed;
   }

   protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      Optional<Vec3> var5 = Optional.ofNullable(this.getOutdoorPosition(serverLevel, livingEntity));
      if(var5.isPresent()) {
         livingEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, var5.map((vec3) -> {
            return new WalkTarget(vec3, this.speed, 0);
         }));
      }

   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
      return !serverLevel.canSeeSky(new BlockPos(livingEntity.x, livingEntity.getBoundingBox().minY, livingEntity.z));
   }

   @Nullable
   private Vec3 getOutdoorPosition(ServerLevel serverLevel, LivingEntity livingEntity) {
      Random var3 = livingEntity.getRandom();
      BlockPos var4 = new BlockPos(livingEntity.x, livingEntity.getBoundingBox().minY, livingEntity.z);

      for(int var5 = 0; var5 < 10; ++var5) {
         BlockPos var6 = var4.offset(var3.nextInt(20) - 10, var3.nextInt(6) - 3, var3.nextInt(20) - 10);
         if(hasNoBlocksAbove(serverLevel, livingEntity)) {
            return new Vec3((double)var6.getX(), (double)var6.getY(), (double)var6.getZ());
         }
      }

      return null;
   }

   public static boolean hasNoBlocksAbove(ServerLevel serverLevel, LivingEntity livingEntity) {
      return serverLevel.canSeeSky(new BlockPos(livingEntity)) && (double)serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(livingEntity)).getY() <= livingEntity.y;
   }
}
