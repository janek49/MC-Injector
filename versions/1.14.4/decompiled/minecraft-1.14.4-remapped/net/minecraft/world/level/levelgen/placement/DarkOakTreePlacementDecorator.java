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
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class DarkOakTreePlacementDecorator extends FeatureDecorator {
   public DarkOakTreePlacementDecorator(Function function) {
      super(function);
   }

   public Stream getPositions(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, NoneDecoratorConfiguration noneDecoratorConfiguration, BlockPos blockPos) {
      return IntStream.range(0, 16).mapToObj((var3) -> {
         int var4 = var3 / 4;
         int var5 = var3 % 4;
         int var6 = var4 * 4 + 1 + random.nextInt(3);
         int var7 = var5 * 4 + 1 + random.nextInt(3);
         return levelAccessor.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos.offset(var6, 0, var7));
      });
   }
}
