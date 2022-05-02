package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class NopePlacementDecorator extends SimpleFeatureDecorator {
   public NopePlacementDecorator(Function function) {
      super(function);
   }

   public Stream place(Random random, NoneDecoratorConfiguration noneDecoratorConfiguration, BlockPos blockPos) {
      return Stream.of(blockPos);
   }
}
