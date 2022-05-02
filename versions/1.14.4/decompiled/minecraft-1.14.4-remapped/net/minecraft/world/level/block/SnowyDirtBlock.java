package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class SnowyDirtBlock extends Block {
   public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

   protected SnowyDirtBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(SNOWY, Boolean.valueOf(false)));
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(direction != Direction.UP) {
         return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
      } else {
         Block var7 = var3.getBlock();
         return (BlockState)var1.setValue(SNOWY, Boolean.valueOf(var7 == Blocks.SNOW_BLOCK || var7 == Blocks.SNOW));
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      Block var2 = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos().above()).getBlock();
      return (BlockState)this.defaultBlockState().setValue(SNOWY, Boolean.valueOf(var2 == Blocks.SNOW_BLOCK || var2 == Blocks.SNOW));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{SNOWY});
   }
}
