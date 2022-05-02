package net.minecraft.world.level.levelgen.placement.nether;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequency;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class LightGemChanceDecorator extends SimpleFeatureDecorator {
   public LightGemChanceDecorator(Function function) {
      super(function);
   }

   public Stream place(Random random, DecoratorFrequency decoratorFrequency, BlockPos blockPos) {
      return IntStream.range(0, random.nextInt(random.nextInt(decoratorFrequency.count) + 1)).mapToObj((var2) -> {
         int var3 = random.nextInt(16);
         int var4 = random.nextInt(120) + 4;
         int var5 = random.nextInt(16);
         return blockPos.offset(var3, var4, var5);
      });
   }
}
