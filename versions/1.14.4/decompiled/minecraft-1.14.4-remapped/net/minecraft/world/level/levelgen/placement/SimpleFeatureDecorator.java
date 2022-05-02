package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public abstract class SimpleFeatureDecorator extends FeatureDecorator {
   public SimpleFeatureDecorator(Function function) {
      super(function);
   }

   public final Stream getPositions(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, DecoratorConfiguration decoratorConfiguration, BlockPos blockPos) {
      return this.place(random, decoratorConfiguration, blockPos);
   }

   protected abstract Stream place(Random var1, DecoratorConfiguration var2, BlockPos var3);
}
