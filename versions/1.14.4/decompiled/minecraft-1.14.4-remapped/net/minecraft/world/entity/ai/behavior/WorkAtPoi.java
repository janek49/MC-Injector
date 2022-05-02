package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SerializableLong;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosWrapper;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;

public class WorkAtPoi extends Behavior {
   private long lastCheck;

   public WorkAtPoi() {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
      if(serverLevel.getGameTime() - this.lastCheck < 300L) {
         return false;
      } else if(serverLevel.random.nextInt(2) != 0) {
         return false;
      } else {
         this.lastCheck = serverLevel.getGameTime();
         GlobalPos var3 = (GlobalPos)villager.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
         return Objects.equals(var3.dimension(), serverLevel.getDimension().getType()) && var3.pos().closerThan(villager.position(), 1.73D);
      }
   }

   protected void start(ServerLevel serverLevel, Villager villager, long var3) {
      Brain<Villager> var5 = villager.getBrain();
      var5.setMemory(MemoryModuleType.LAST_WORKED_AT_POI, (Object)SerializableLong.of(var3));
      var5.getMemory(MemoryModuleType.JOB_SITE).ifPresent((globalPos) -> {
         var5.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new BlockPosWrapper(globalPos.pos())));
      });
      villager.playWorkSound();
      if(villager.shouldRestock()) {
         villager.restock();
      }

   }
}
