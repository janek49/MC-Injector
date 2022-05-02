package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.pathfinder.Path;

public class AcquirePoi extends Behavior {
   private final PoiType poiType;
   private final MemoryModuleType memoryType;
   private final boolean onlyIfAdult;
   private long lastUpdate;
   private final Long2LongMap batchCache = new Long2LongOpenHashMap();
   private int triedCount;

   public AcquirePoi(PoiType poiType, MemoryModuleType memoryType, boolean onlyIfAdult) {
      super(ImmutableMap.of(memoryType, MemoryStatus.VALUE_ABSENT));
      this.poiType = poiType;
      this.memoryType = memoryType;
      this.onlyIfAdult = onlyIfAdult;
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, PathfinderMob pathfinderMob) {
      return this.onlyIfAdult && pathfinderMob.isBaby()?false:serverLevel.getGameTime() - this.lastUpdate >= 20L;
   }

   protected void start(ServerLevel serverLevel, PathfinderMob pathfinderMob, long var3) {
      this.triedCount = 0;
      this.lastUpdate = serverLevel.getGameTime() + (long)serverLevel.getRandom().nextInt(20);
      PoiManager var5 = serverLevel.getPoiManager();
      Predicate<BlockPos> var6 = (blockPos) -> {
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
      Stream<BlockPos> var7 = var5.findAll(this.poiType.getPredicate(), var6, new BlockPos(pathfinderMob), 48, PoiManager.Occupancy.HAS_SPACE);
      Path var8 = pathfinderMob.getNavigation().createPath(var7, this.poiType.getValidRange());
      if(var8 != null && var8.canReach()) {
         BlockPos var9 = var8.getTarget();
         var5.getType(var9).ifPresent((poiType) -> {
            var5.take(this.poiType.getPredicate(), (var1) -> {
               return var1.equals(var9);
            }, var9, 1);
            pathfinderMob.getBrain().setMemory(this.memoryType, (Object)GlobalPos.of(serverLevel.getDimension().getType(), var9));
            DebugPackets.sendPoiTicketCountPacket(serverLevel, var9);
         });
      } else if(this.triedCount < 5) {
         this.batchCache.long2LongEntrySet().removeIf((long2LongMap$Entry) -> {
            return long2LongMap$Entry.getLongValue() < this.lastUpdate;
         });
      }

   }
}
