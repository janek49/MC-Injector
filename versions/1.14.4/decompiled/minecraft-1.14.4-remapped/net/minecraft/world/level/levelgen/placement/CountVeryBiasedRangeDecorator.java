package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.DecoratorCountRange;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class CountVeryBiasedRangeDecorator extends SimpleFeatureDecorator {
   public CountVeryBiasedRangeDecorator(Function function) {
      super(function);
   }

   public Stream place(Random random, DecoratorCountRange decoratorCountRange, BlockPos blockPos) {
      return IntStream.range(0, decoratorCountRange.count).mapToObj((var3) -> {
         int var4 = random.nextInt(16);
         int var5 = random.nextInt(16);
         int var6 = random.nextInt(random.nextInt(random.nextInt(decoratorCountRange.maximum - decoratorCountRange.topOffset) + decoratorCountRange.bottomOffset) + decoratorCountRange.bottomOffset);
         return blockPos.offset(var4, var6, var5);
      });
   }
}
