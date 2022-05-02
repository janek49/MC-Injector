package net.minecraft.world.level.block;

import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;

public class GlassBlock extends AbstractGlassBlock {
   public GlassBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }
}
