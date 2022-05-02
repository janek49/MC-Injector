package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public interface SimpleWaterloggedBlock extends BucketPickup, LiquidBlockContainer {
   default boolean canPlaceLiquid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
      return !((Boolean)blockState.getValue(BlockStateProperties.WATERLOGGED)).booleanValue() && fluid == Fluids.WATER;
   }

   default boolean placeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
      if(!((Boolean)blockState.getValue(BlockStateProperties.WATERLOGGED)).booleanValue() && fluidState.getType() == Fluids.WATER) {
         if(!levelAccessor.isClientSide()) {
            levelAccessor.setBlock(blockPos, (BlockState)blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true)), 3);
            levelAccessor.getLiquidTicks().scheduleTick(blockPos, fluidState.getType(), fluidState.getType().getTickDelay(levelAccessor));
         }

         return true;
      } else {
         return false;
      }
   }

   default Fluid takeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
      if(((Boolean)blockState.getValue(BlockStateProperties.WATERLOGGED)).booleanValue()) {
         levelAccessor.setBlock(blockPos, (BlockState)blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)), 3);
         return Fluids.WATER;
      } else {
         return Fluids.EMPTY;
      }
   }
}
