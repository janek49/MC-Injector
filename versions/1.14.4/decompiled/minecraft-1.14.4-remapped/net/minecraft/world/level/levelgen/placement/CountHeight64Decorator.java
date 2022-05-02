package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequency;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class CountHeight64Decorator extends FeatureDecorator {
   public CountHeight64Decorator(Function function) {
      super(function);
   }

   public Stream getPositions(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, DecoratorFrequency decoratorFrequency, BlockPos blockPos) {
      return IntStream.range(0, decoratorFrequency.count).mapToObj((var2) -> {
         int var3 = random.nextInt(16);
         int var4 = 64;
         int var5 = random.nextInt(16);
         return blockPos.offset(var3, 64, var5);
      });
   }
}
