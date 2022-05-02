package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class IronBarsBlock extends CrossCollisionBlock {
   protected IronBarsBlock(Block.Properties block$Properties) {
      super(1.0F, 1.0F, 16.0F, 16.0F, 16.0F, block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, Boolean.valueOf(false))).setValue(EAST, Boolean.valueOf(false))).setValue(SOUTH, Boolean.valueOf(false))).setValue(WEST, Boolean.valueOf(false))).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockGetter var2 = blockPlaceContext.getLevel();
      BlockPos var3 = blockPlaceContext.getClickedPos();
      FluidState var4 = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
      BlockPos var5 = var3.north();
      BlockPos var6 = var3.south();
      BlockPos var7 = var3.west();
      BlockPos var8 = var3.east();
      BlockState var9 = var2.getBlockState(var5);
      BlockState var10 = var2.getBlockState(var6);
      BlockState var11 = var2.getBlockState(var7);
      BlockState var12 = var2.getBlockState(var8);
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(NORTH, Boolean.valueOf(this.attachsTo(var9, var9.isFaceSturdy(var2, var5, Direction.SOUTH))))).setValue(SOUTH, Boolean.valueOf(this.attachsTo(var10, var10.isFaceSturdy(var2, var6, Direction.NORTH))))).setValue(WEST, Boolean.valueOf(this.attachsTo(var11, var11.isFaceSturdy(var2, var7, Direction.EAST))))).setValue(EAST, Boolean.valueOf(this.attachsTo(var12, var12.isFaceSturdy(var2, var8, Direction.WEST))))).setValue(WATERLOGGED, Boolean.valueOf(var4.getType() == Fluids.WATER));
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(((Boolean)var1.getValue(WATERLOGGED)).booleanValue()) {
         levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      }

      return direction.getAxis().isHorizontal()?(BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(direction), Boolean.valueOf(this.attachsTo(var3, var3.isFaceSturdy(levelAccessor, var6, direction.getOpposite())))):super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public boolean skipRendering(BlockState var1, BlockState var2, Direction direction) {
      if(var2.getBlock() == this) {
         if(!direction.getAxis().isHorizontal()) {
            return true;
         }

         if(((Boolean)var1.getValue((Property)PROPERTY_BY_DIRECTION.get(direction))).booleanValue() && ((Boolean)var2.getValue((Property)PROPERTY_BY_DIRECTION.get(direction.getOpposite()))).booleanValue()) {
            return true;
         }
      }

      return super.skipRendering(var1, var2, direction);
   }

   public final boolean attachsTo(BlockState blockState, boolean var2) {
      Block var3 = blockState.getBlock();
      return !isExceptionForConnection(var3) && var2 || var3 instanceof IronBarsBlock;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT_MIPPED;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{NORTH, EAST, WEST, SOUTH, WATERLOGGED});
   }
}
