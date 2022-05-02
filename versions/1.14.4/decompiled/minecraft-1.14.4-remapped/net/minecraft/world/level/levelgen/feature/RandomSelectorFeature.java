package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.RandomFeatureConfig;
import net.minecraft.world.level.levelgen.feature.WeightedConfiguredFeature;

public class RandomSelectorFeature extends Feature {
   public RandomSelectorFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, RandomFeatureConfig randomFeatureConfig) {
      for(WeightedConfiguredFeature<?> var7 : randomFeatureConfig.features) {
         if(random.nextFloat() < var7.chance.floatValue()) {
            return var7.place(levelAccessor, chunkGenerator, random, blockPos);
         }
      }

      return randomFeatureConfig.defaultFeature.place(levelAccessor, chunkGenerator, random, blockPos);
   }
}
