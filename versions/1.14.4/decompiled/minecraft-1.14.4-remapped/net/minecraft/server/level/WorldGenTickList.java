package net.minecraft.server.level;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickPriority;

public class WorldGenTickList implements TickList {
   private final Function index;

   public WorldGenTickList(Function index) {
      this.index = index;
   }

   public boolean hasScheduledTick(BlockPos blockPos, Object object) {
      return ((TickList)this.index.apply(blockPos)).hasScheduledTick(blockPos, object);
   }

   public void scheduleTick(BlockPos blockPos, Object object, int var3, TickPriority tickPriority) {
      ((TickList)this.index.apply(blockPos)).scheduleTick(blockPos, object, var3, tickPriority);
   }

   public boolean willTickThisTick(BlockPos blockPos, Object object) {
      return false;
   }

   public void addAll(Stream stream) {
      stream.forEach((tickNextTickData) -> {
         ((TickList)this.index.apply(tickNextTickData.pos)).addAll(Stream.of(tickNextTickData));
      });
   }
}
