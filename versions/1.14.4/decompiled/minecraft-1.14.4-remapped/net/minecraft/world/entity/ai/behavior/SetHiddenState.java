package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class SetHiddenState extends Behavior {
   private final int closeEnoughDist;
   private final int stayHiddenTicks;
   private int ticksHidden;

   public SetHiddenState(int var1, int closeEnoughDist) {
      super(ImmutableMap.of(MemoryModuleType.HIDING_PLACE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.HEARD_BELL_TIME, MemoryStatus.VALUE_PRESENT));
      this.stayHiddenTicks = var1 * 20;
      this.ticksHidden = 0;
      this.closeEnoughDist = closeEnoughDist;
   }

   protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      Brain<?> var5 = livingEntity.getBrain();
      Optional<Long> var6 = var5.getMemory(MemoryModuleType.HEARD_BELL_TIME);
      boolean var7 = ((Long)var6.get()).longValue() + 300L <= var3;
      if(this.ticksHidden <= this.stayHiddenTicks && !var7) {
         BlockPos var8 = ((GlobalPos)var5.getMemory(MemoryModuleType.HIDING_PLACE).get()).pos();
         if(var8.closerThan(new BlockPos(livingEntity), (double)(this.closeEnoughDist + 1))) {
            ++this.ticksHidden;
         }

      } else {
         var5.eraseMemory(MemoryModuleType.HEARD_BELL_TIME);
         var5.eraseMemory(MemoryModuleType.HIDING_PLACE);
         var5.updateActivity(serverLevel.getDayTime(), serverLevel.getGameTime());
         this.ticksHidden = 0;
      }
   }
}
