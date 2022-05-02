package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FlowerFeature;

public class SwampFlowerFeature extends FlowerFeature {
   public SwampFlowerFeature(Function function) {
      super(function);
   }

   public BlockState getRandomFlower(Random random, BlockPos blockPos) {
      return Blocks.BLUE_ORCHID.defaultBlockState();
   }
}
