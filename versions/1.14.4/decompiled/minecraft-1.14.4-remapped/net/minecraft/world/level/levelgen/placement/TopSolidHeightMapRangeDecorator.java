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
import net.minecraft.world.level.levelgen.placement.DecoratorRange;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class TopSolidHeightMapRangeDecorator extends FeatureDecorator {
   public TopSolidHeightMapRangeDecorator(Function function) {
      super(function);
   }

   public Stream getPositions(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, DecoratorRange decoratorRange, BlockPos blockPos) {
      int var6 = random.nextInt(decoratorRange.max - decoratorRange.min) + decoratorRange.min;
      return IntStream.range(0, var6).mapToObj((var3) -> {
         int var4 = random.nextInt(16);
         int var5 = random.nextInt(16);
         int var6 = levelAccessor.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, blockPos.getX() + var4, blockPos.getZ() + var5);
         return new BlockPos(blockPos.getX() + var4, var6, blockPos.getZ() + var5);
      });
   }
}
