package net.minecraft.world.level.block;

import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ShearableDoublePlantBlock extends DoublePlantBlock {
   public static final EnumProperty HALF = DoublePlantBlock.HALF;

   public ShearableDoublePlantBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
      boolean var3 = super.canBeReplaced(blockState, blockPlaceContext);
      return var3 && blockPlaceContext.getItemInHand().getItem() == this.asItem()?false:var3;
   }
}
