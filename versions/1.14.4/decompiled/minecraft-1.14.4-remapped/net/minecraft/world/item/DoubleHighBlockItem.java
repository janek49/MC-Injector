package net.minecraft.world.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DoubleHighBlockItem extends BlockItem {
   public DoubleHighBlockItem(Block block, Item.Properties item$Properties) {
      super(block, item$Properties);
   }

   protected boolean placeBlock(BlockPlaceContext blockPlaceContext, BlockState blockState) {
      blockPlaceContext.getLevel().setBlock(blockPlaceContext.getClickedPos().above(), Blocks.AIR.defaultBlockState(), 27);
      return super.placeBlock(blockPlaceContext, blockState);
   }
}
