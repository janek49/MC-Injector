package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.DecoratorChance;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class ChancePassthroughDecorator extends SimpleFeatureDecorator {
   public ChancePassthroughDecorator(Function function) {
      super(function);
   }

   public Stream place(Random random, DecoratorChance decoratorChance, BlockPos blockPos) {
      return random.nextFloat() < 1.0F / (float)decoratorChance.chance?Stream.of(blockPos):Stream.empty();
   }
}
