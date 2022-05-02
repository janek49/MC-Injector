package net.minecraft.world.level;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.TickPriority;

public interface TickList {
   boolean hasScheduledTick(BlockPos var1, Object var2);

   default void scheduleTick(BlockPos blockPos, Object object, int var3) {
      this.scheduleTick(blockPos, object, var3, TickPriority.NORMAL);
   }

   void scheduleTick(BlockPos var1, Object var2, int var3, TickPriority var4);

   boolean willTickThisTick(BlockPos var1, Object var2);

   void addAll(Stream var1);
}
