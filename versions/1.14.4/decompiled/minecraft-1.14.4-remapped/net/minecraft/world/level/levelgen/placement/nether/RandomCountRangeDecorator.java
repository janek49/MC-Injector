package net.minecraft.world.level.levelgen.placement.nether;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.DecoratorCountRange;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class RandomCountRangeDecorator extends SimpleFeatureDecorator {
   public RandomCountRangeDecorator(Function function) {
      super(function);
   }

   public Stream place(Random random, DecoratorCountRange decoratorCountRange, BlockPos blockPos) {
      int var4 = random.nextInt(Math.max(decoratorCountRange.count, 1));
      return IntStream.range(0, var4).mapToObj((var3) -> {
         int var4 = random.nextInt(16);
         int var5 = random.nextInt(decoratorCountRange.maximum - decoratorCountRange.topOffset) + decoratorCountRange.bottomOffset;
         int var6 = random.nextInt(16);
         return blockPos.offset(var4, var5, var6);
      });
   }
}
