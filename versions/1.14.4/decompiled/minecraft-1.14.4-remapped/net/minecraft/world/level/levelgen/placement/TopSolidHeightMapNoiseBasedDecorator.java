package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.DecoratorNoiseCountFactor;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class TopSolidHeightMapNoiseBasedDecorator extends FeatureDecorator {
   public TopSolidHeightMapNoiseBasedDecorator(Function function) {
      super(function);
   }

   public Stream getPositions(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, DecoratorNoiseCountFactor decoratorNoiseCountFactor, BlockPos blockPos) {
      double var6 = Biome.BIOME_INFO_NOISE.getValue((double)blockPos.getX() / decoratorNoiseCountFactor.noiseFactor, (double)blockPos.getZ() / decoratorNoiseCountFactor.noiseFactor);
      int var8 = (int)Math.ceil((var6 + decoratorNoiseCountFactor.noiseOffset) * (double)decoratorNoiseCountFactor.noiseToCountRatio);
      return IntStream.range(0, var8).mapToObj((var4) -> {
         int var5 = random.nextInt(16);
         int var6 = random.nextInt(16);
         int var7 = levelAccessor.getHeight(decoratorNoiseCountFactor.heightmap, blockPos.getX() + var5, blockPos.getZ() + var6);
         return new BlockPos(blockPos.getX() + var5, var7, blockPos.getZ() + var6);
      });
   }
}
