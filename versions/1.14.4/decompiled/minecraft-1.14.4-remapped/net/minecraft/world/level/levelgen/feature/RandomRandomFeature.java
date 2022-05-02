package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.RandomRandomFeatureConfig;

public class RandomRandomFeature extends Feature {
   public RandomRandomFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, RandomRandomFeatureConfig randomRandomFeatureConfig) {
      int var6 = random.nextInt(5) - 3 + randomRandomFeatureConfig.count;

      for(int var7 = 0; var7 < var6; ++var7) {
         int var8 = random.nextInt(randomRandomFeatureConfig.features.size());
         ConfiguredFeature<?> var9 = (ConfiguredFeature)randomRandomFeatureConfig.features.get(var8);
         var9.place(levelAccessor, chunkGenerator, random, blockPos);
      }

      return true;
   }
}
