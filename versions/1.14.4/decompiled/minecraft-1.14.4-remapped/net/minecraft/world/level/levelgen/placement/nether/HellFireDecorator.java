package net.minecraft.world.level.levelgen.placement.nether;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequency;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class HellFireDecorator extends SimpleFeatureDecorator {
   public HellFireDecorator(Function function) {
      super(function);
   }

   public Stream place(Random random, DecoratorFrequency decoratorFrequency, BlockPos blockPos) {
      List<BlockPos> var4 = Lists.newArrayList();

      for(int var5 = 0; var5 < random.nextInt(random.nextInt(decoratorFrequency.count) + 1) + 1; ++var5) {
         int var6 = random.nextInt(16);
         int var7 = random.nextInt(120) + 4;
         int var8 = random.nextInt(16);
         var4.add(blockPos.offset(var6, var7, var8));
      }

      return var4.stream();
   }
}
