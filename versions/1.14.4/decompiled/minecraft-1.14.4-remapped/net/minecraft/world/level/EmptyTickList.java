package net.minecraft.world.level;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickPriority;

public class EmptyTickList implements TickList {
   private static final EmptyTickList INSTANCE = new EmptyTickList();

   public static EmptyTickList empty() {
      return INSTANCE;
   }

   public boolean hasScheduledTick(BlockPos blockPos, Object object) {
      return false;
   }

   public void scheduleTick(BlockPos blockPos, Object object, int var3) {
   }

   public void scheduleTick(BlockPos blockPos, Object object, int var3, TickPriority tickPriority) {
   }

   public boolean willTickThisTick(BlockPos blockPos, Object object) {
      return false;
   }

   public void addAll(Stream stream) {
   }
}
