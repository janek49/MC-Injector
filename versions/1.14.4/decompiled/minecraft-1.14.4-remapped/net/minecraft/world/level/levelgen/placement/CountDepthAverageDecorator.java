package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.DepthAverageConfigation;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class CountDepthAverageDecorator extends SimpleFeatureDecorator {
   public CountDepthAverageDecorator(Function function) {
      super(function);
   }

   public Stream place(Random random, DepthAverageConfigation depthAverageConfigation, BlockPos blockPos) {
      int var4 = depthAverageConfigation.count;
      int var5 = depthAverageConfigation.baseline;
      int var6 = depthAverageConfigation.spread;
      return IntStream.range(0, var4).mapToObj((var4) -> {
         int var5 = random.nextInt(16);
         int var6 = random.nextInt(var6) + random.nextInt(var6) - var6 + var5;
         int var7 = random.nextInt(16);
         return blockPos.offset(var5, var6, var7);
      });
   }
}
