package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.ShearableDoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TallSeagrass extends ShearableDoublePlantBlock implements LiquidBlockContainer {
   public static final EnumProperty HALF = ShearableDoublePlantBlock.HALF;
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

   public TallSeagrass(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return blockState.isFaceSturdy(blockGetter, blockPos, Direction.UP) && blockState.getBlock() != Blocks.MAGMA_BLOCK;
   }

   public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      return new ItemStack(Blocks.SEAGRASS);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockState blockState = super.getStateForPlacement(blockPlaceContext);
      if(blockState != null) {
         FluidState var3 = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos().above());
         if(var3.is(FluidTags.WATER) && var3.getAmount() == 8) {
            return blockState;
         }
      }

      return null;
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      if(blockState.getValue(HALF) == DoubleBlockHalf.UPPER) {
         BlockState blockState = levelReader.getBlockState(blockPos.below());
         return blockState.getBlock() == this && blockState.getValue(HALF) == DoubleBlockHalf.LOWER;
      } else {
         FluidState var4 = levelReader.getFluidState(blockPos);
         return super.canSurvive(blockState, levelReader, blockPos) && var4.is(FluidTags.WATER) && var4.getAmount() == 8;
      }
   }

   public FluidState getFluidState(BlockState blockState) {
      return Fluids.WATER.getSource(false);
   }

   public boolean canPlaceLiquid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
      return false;
   }

   public boolean placeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
      return false;
   }
}
