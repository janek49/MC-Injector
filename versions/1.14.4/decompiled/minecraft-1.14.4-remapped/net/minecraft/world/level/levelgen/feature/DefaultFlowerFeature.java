package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FlowerFeature;

public class DefaultFlowerFeature extends FlowerFeature {
   public DefaultFlowerFeature(Function function) {
      super(function);
   }

   public BlockState getRandomFlower(Random random, BlockPos blockPos) {
      return random.nextFloat() > 0.6666667F?Blocks.DANDELION.defaultBlockState():Blocks.POPPY.defaultBlockState();
   }
}
