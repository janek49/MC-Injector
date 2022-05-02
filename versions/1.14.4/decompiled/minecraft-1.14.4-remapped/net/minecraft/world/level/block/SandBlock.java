package net.minecraft.world.level.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SandBlock extends FallingBlock {
   private final int dustColor;

   public SandBlock(int dustColor, Block.Properties block$Properties) {
      super(block$Properties);
      this.dustColor = dustColor;
   }

   public int getDustColor(BlockState blockState) {
      return this.dustColor;
   }
}
