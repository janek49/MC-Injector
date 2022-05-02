package net.minecraft.world.level.levelgen.placement.nether;

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

public class MagmaDecorator extends FeatureDecorator {
   public MagmaDecorator(Function function) {
      super(function);
   }

   public Stream getPositions(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, DecoratorFrequency decoratorFrequency, BlockPos blockPos) {
      int var6 = levelAccessor.getSeaLevel() / 2 + 1;
      return IntStream.range(0, decoratorFrequency.count).mapToObj((var3) -> {
         int var4 = random.nextInt(16);
         int var5 = var6 - 5 + random.nextInt(10);
         int var6 = random.nextInt(16);
         return blockPos.offset(var4, var5, var6);
      });
   }
}
