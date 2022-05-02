package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;

public class DecoratedFeature extends Feature {
   public DecoratedFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DecoratedFeatureConfiguration decoratedFeatureConfiguration) {
      return decoratedFeatureConfiguration.decorator.place(levelAccessor, chunkGenerator, random, blockPos, decoratedFeatureConfiguration.feature);
   }

   public String toString() {
      return String.format("< %s [%s] >", new Object[]{this.getClass().getSimpleName(), Registry.FEATURE.getKey(this)});
   }
}
