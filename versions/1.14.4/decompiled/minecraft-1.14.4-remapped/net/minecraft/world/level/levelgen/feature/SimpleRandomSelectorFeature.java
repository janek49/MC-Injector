package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SimpleRandomFeatureConfig;

public class SimpleRandomSelectorFeature extends Feature {
   public SimpleRandomSelectorFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SimpleRandomFeatureConfig simpleRandomFeatureConfig) {
      int var6 = random.nextInt(simpleRandomFeatureConfig.features.size());
      ConfiguredFeature<?> var7 = (ConfiguredFeature)simpleRandomFeatureConfig.features.get(var6);
      return var7.place(levelAccessor, chunkGenerator, random, blockPos);
   }
}
