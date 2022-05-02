package net.minecraft.world.level.levelgen.placement;

import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.DecoratorNoiseDependant;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class NoiseHeightmapDoubleDecorator extends FeatureDecorator {
   public NoiseHeightmapDoubleDecorator(Function function) {
      super(function);
   }

   public Stream getPositions(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, DecoratorNoiseDependant decoratorNoiseDependant, BlockPos blockPos) {
      double var6 = Biome.BIOME_INFO_NOISE.getValue((double)blockPos.getX() / 200.0D, (double)blockPos.getZ() / 200.0D);
      int var8 = var6 < decoratorNoiseDependant.noiseLevel?decoratorNoiseDependant.belowNoise:decoratorNoiseDependant.aboveNoise;
      return IntStream.range(0, var8).mapToObj((var3) -> {
         int var4 = random.nextInt(16);
         int var5 = random.nextInt(16);
         int var6 = levelAccessor.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos.offset(var4, 0, var5)).getY() * 2;
         if(var6 <= 0) {
            return null;
         } else {
            int var7 = random.nextInt(var6);
            return blockPos.offset(var4, var7, var5);
         }
      }).filter(Objects::nonNull);
   }
}
