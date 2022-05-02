package net.minecraft.world.level.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;

public class StainedGlassPaneBlock extends IronBarsBlock implements BeaconBeamBlock {
   private final DyeColor color;

   public StainedGlassPaneBlock(DyeColor color, Block.Properties block$Properties) {
      super(block$Properties);
      this.color = color;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, Boolean.valueOf(false))).setValue(EAST, Boolean.valueOf(false))).setValue(SOUTH, Boolean.valueOf(false))).setValue(WEST, Boolean.valueOf(false))).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public DyeColor getColor() {
      return this.color;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.TRANSLUCENT;
   }
}
