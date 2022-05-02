package net.minecraft.world.level.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class GravelBlock extends FallingBlock {
   public GravelBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public int getDustColor(BlockState blockState) {
      return -8356741;
   }
}
