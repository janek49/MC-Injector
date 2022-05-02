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

public class ForestRockPlacementDecorator extends FeatureDecorator {
   public ForestRockPlacementDecorator(Function function) {
      super(function);
   }

   public Stream getPositions(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, DecoratorFrequency decoratorFrequency, BlockPos blockPos) {
      int var6 = random.nextInt(decoratorFrequency.count);
      return IntStream.range(0, var6).mapToObj((var3) -> {
         int var4 = random.nextInt(16);
         int var5 = random.nextInt(16);
         return levelAccessor.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos.offset(var4, 0, var5));
      });
   }
}
