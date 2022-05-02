package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;

public class StrollToPoiList extends Behavior {
   private final MemoryModuleType strollToMemoryType;
   private final MemoryModuleType mustBeCloseToMemoryType;
   private final float speed;
   private final int closeEnoughDist;
   private final int maxDistanceFromPoi;
   private long nextOkStartTime;
   @Nullable
   private GlobalPos targetPos;

   public StrollToPoiList(MemoryModuleType strollToMemoryType, float speed, int closeEnoughDist, int maxDistanceFromPoi, MemoryModuleType mustBeCloseToMemoryType) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, strollToMemoryType, MemoryStatus.VALUE_PRESENT, mustBeCloseToMemoryType, MemoryStatus.VALUE_PRESENT));
      this.strollToMemoryType = strollToMemoryType;
      this.speed = speed;
      this.closeEnoughDist = closeEnoughDist;
      this.maxDistanceFromPoi = maxDistanceFromPoi;
      this.mustBeCloseToMemoryType = mustBeCloseToMemoryType;
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
      Optional<List<GlobalPos>> var3 = villager.getBrain().getMemory(this.strollToMemoryType);
      Optional<GlobalPos> var4 = villager.getBrain().getMemory(this.mustBeCloseToMemoryType);
      if(var3.isPresent() && var4.isPresent()) {
         List<GlobalPos> var5 = (List)var3.get();
         if(!var5.isEmpty()) {
            this.targetPos = (GlobalPos)var5.get(serverLevel.getRandom().nextInt(var5.size()));
            return this.targetPos != null && Objects.equals(serverLevel.getDimension().getType(), this.targetPos.dimension()) && ((GlobalPos)var4.get()).pos().closerThan(villager.position(), (double)this.maxDistanceFromPoi);
         }
      }

      return false;
   }

   protected void start(ServerLevel serverLevel, Villager villager, long var3) {
      if(var3 > this.nextOkStartTime && this.targetPos != null) {
         villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(this.targetPos.pos(), this.speed, this.closeEnoughDist)));
         this.nextOkStartTime = var3 + 100L;
      }

   }
}
