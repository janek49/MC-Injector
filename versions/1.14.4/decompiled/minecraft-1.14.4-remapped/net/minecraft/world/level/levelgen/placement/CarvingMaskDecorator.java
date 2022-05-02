package net.minecraft.world.level.levelgen.placement;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.DecoratorCarvingMaskConfig;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class CarvingMaskDecorator extends FeatureDecorator {
   public CarvingMaskDecorator(Function function) {
      super(function);
   }

   public Stream getPositions(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, DecoratorCarvingMaskConfig decoratorCarvingMaskConfig, BlockPos blockPos) {
      ChunkAccess var6 = levelAccessor.getChunk(blockPos);
      ChunkPos var7 = var6.getPos();
      BitSet var8 = var6.getCarvingMask(decoratorCarvingMaskConfig.step);
      return IntStream.range(0, var8.length()).filter((var3) -> {
         return var8.get(var3) && random.nextFloat() < decoratorCarvingMaskConfig.probability;
      }).mapToObj((var1) -> {
         int var2 = var1 & 15;
         int var3 = var1 >> 4 & 15;
         int var4 = var1 >> 8;
         return new BlockPos(var7.getMinBlockX() + var2, var4, var7.getMinBlockZ() + var3);
      });
   }
}
