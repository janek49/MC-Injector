package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.pathfinder.Path;

public class SetClosestHomeAsWalkTarget extends Behavior {
   private final float speed;
   private final Long2LongMap batchCache = new Long2LongOpenHashMap();
   private int triedCount;
   private long lastUpdate;

   public SetClosestHomeAsWalkTarget(float speed) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryStatus.VALUE_ABSENT));
      this.speed = speed;
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
      if(serverLevel.getGameTime() - this.lastUpdate < 20L) {
         return false;
      } else {
         PathfinderMob var3 = (PathfinderMob)livingEntity;
         PoiManager var4 = serverLevel.getPoiManager();
         Optional<BlockPos> var5 = var4.findClosest(PoiType.HOME.getPredicate(), (blockPos) -> {
            return true;
         }, new BlockPos(livingEntity), 48, PoiManager.Occupancy.ANY);
         return var5.isPresent() && ((BlockPos)var5.get()).distSqr(new Vec3i(var3.x, var3.y, var3.z)) > 4.0D;
      }
   }

   protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      this.triedCount = 0;
      this.lastUpdate = serverLevel.getGameTime() + (long)serverLevel.getRandom().nextInt(20);
      PathfinderMob var5 = (PathfinderMob)livingEntity;
      PoiManager var6 = serverLevel.getPoiManager();
      Predicate<BlockPos> var7 = (blockPos) -> {
         long var2 = blockPos.asLong();
         if(this.batchCache.containsKey(var2)) {
            return false;
         } else if(++this.triedCount >= 5) {
            return false;
         } else {
            this.batchCache.put(var2, this.lastUpdate + 40L);
            return true;
         }
      };
      Stream<BlockPos> var8 = var6.findAll(PoiType.HOME.getPredicate(), var7, new BlockPos(livingEntity), 48, PoiManager.Occupancy.ANY);
      Path var9 = var5.getNavigation().createPath(var8, PoiType.HOME.getValidRange());
      if(var9 != null && var9.canReach()) {
         BlockPos var10 = var9.getTarget();
         Optional<PoiType> var11 = var6.getType(var10);
         if(var11.isPresent()) {
            livingEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var10, this.speed, 1)));
            DebugPackets.sendPoiTicketCountPacket(serverLevel, var10);
         }
      } else if(this.triedCount < 5) {
         this.batchCache.long2LongEntrySet().removeIf((long2LongMap$Entry) -> {
            return long2LongMap$Entry.getLongValue() < this.lastUpdate;
         });
      }

   }
}
