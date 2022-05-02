package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PoweredBlock extends Block {
   public PoweredBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public boolean isSignalSource(BlockState blockState) {
      return true;
   }

   public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return 15;
   }
}
