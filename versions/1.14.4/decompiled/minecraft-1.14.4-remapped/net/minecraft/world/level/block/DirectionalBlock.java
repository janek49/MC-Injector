package net.minecraft.world.level.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class DirectionalBlock extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.FACING;

   protected DirectionalBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }
}
