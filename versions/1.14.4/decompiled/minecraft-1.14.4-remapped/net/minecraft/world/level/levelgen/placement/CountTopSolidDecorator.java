package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequency;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class CountTopSolidDecorator extends FeatureDecorator {
   public CountTopSolidDecorator(Function function) {
      super(function);
   }

   public Stream getPositions(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, DecoratorFrequency decoratorFrequency, BlockPos blockPos) {
      return IntStream.range(0, decoratorFrequency.count).mapToObj((var3) -> {
         int var4 = random.nextInt(16) + blockPos.getX();
         int var5 = random.nextInt(16) + blockPos.getZ();
         return new BlockPos(var4, levelAccessor.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, var4, var5), var5);
      });
   }
}
