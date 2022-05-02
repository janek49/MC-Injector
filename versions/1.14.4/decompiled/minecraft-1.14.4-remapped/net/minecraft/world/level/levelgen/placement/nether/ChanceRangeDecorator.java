package net.minecraft.world.level.levelgen.placement.nether;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.DecoratorChanceRange;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class ChanceRangeDecorator extends SimpleFeatureDecorator {
   public ChanceRangeDecorator(Function function) {
      super(function);
   }

   public Stream place(Random random, DecoratorChanceRange decoratorChanceRange, BlockPos blockPos) {
      if(random.nextFloat() < decoratorChanceRange.chance) {
         int var4 = random.nextInt(16);
         int var5 = random.nextInt(decoratorChanceRange.top - decoratorChanceRange.topOffset) + decoratorChanceRange.bottomOffset;
         int var6 = random.nextInt(16);
         return Stream.of(blockPos.offset(var4, var5, var6));
      } else {
         return Stream.empty();
      }
   }
}
