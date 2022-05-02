package net.minecraft.world.level.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;

public class StainedGlassBlock extends AbstractGlassBlock implements BeaconBeamBlock {
   private final DyeColor color;

   public StainedGlassBlock(DyeColor color, Block.Properties block$Properties) {
      super(block$Properties);
      this.color = color;
   }

   public DyeColor getColor() {
      return this.color;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.TRANSLUCENT;
   }
}
