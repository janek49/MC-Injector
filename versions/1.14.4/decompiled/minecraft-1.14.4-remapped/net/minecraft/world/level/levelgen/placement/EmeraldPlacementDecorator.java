package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class EmeraldPlacementDecorator extends SimpleFeatureDecorator {
   public EmeraldPlacementDecorator(Function function) {
      super(function);
   }

   public Stream place(Random random, NoneDecoratorConfiguration noneDecoratorConfiguration, BlockPos blockPos) {
      int var4 = 3 + random.nextInt(6);
      return IntStream.range(0, var4).mapToObj((var2) -> {
         int var3 = random.nextInt(16);
         int var4 = random.nextInt(28) + 4;
         int var5 = random.nextInt(16);
         return blockPos.offset(var3, var4, var5);
      });
   }
}
