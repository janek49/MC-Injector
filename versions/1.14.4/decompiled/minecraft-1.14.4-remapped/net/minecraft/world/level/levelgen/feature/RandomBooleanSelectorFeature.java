package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.RandomBooleanFeatureConfig;

public class RandomBooleanSelectorFeature extends Feature {
   public RandomBooleanSelectorFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, RandomBooleanFeatureConfig randomBooleanFeatureConfig) {
      boolean var6 = random.nextBoolean();
      return var6?randomBooleanFeatureConfig.featureTrue.place(levelAccessor, chunkGenerator, random, blockPos):randomBooleanFeatureConfig.featureFalse.place(levelAccessor, chunkGenerator, random, blockPos);
   }
}
