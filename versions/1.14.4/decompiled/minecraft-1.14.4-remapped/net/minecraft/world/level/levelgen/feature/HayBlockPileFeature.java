package net.minecraft.world.level.levelgen.feature;

import java.util.function.Function;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.BlockPileFeature;

public class HayBlockPileFeature extends BlockPileFeature {
   public HayBlockPileFeature(Function function) {
      super(function);
   }

   protected BlockState getBlockState(LevelAccessor levelAccessor) {
      Direction.Axis var2 = Direction.Axis.getRandomAxis(levelAccessor.getRandom());
      return (BlockState)Blocks.HAY_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS, var2);
   }
}
