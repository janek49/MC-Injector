package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.TallSeagrass;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Seagrass extends BushBlock implements BonemealableBlock, LiquidBlockContainer {
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

   protected Seagrass(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return blockState.isFaceSturdy(blockGetter, blockPos, Direction.UP) && blockState.getBlock() != Blocks.MAGMA_BLOCK;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      FluidState var2 = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
      return var2.is(FluidTags.WATER) && var2.getAmount() == 8?super.getStateForPlacement(blockPlaceContext):null;
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      BlockState var7 = super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
      if(!var7.isAir()) {
         levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      }

      return var7;
   }

   public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean var4) {
      return true;
   }

   public boolean isBonemealSuccess(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      return true;
   }

   public FluidState getFluidState(BlockState blockState) {
      return Fluids.WATER.getSource(false);
   }

   public void performBonemeal(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      BlockState blockState = Blocks.TALL_SEAGRASS.defaultBlockState();
      BlockState var6 = (BlockState)blockState.setValue(TallSeagrass.HALF, DoubleBlockHalf.UPPER);
      BlockPos var7 = blockPos.above();
      if(level.getBlockState(var7).getBlock() == Blocks.WATER) {
         level.setBlock(blockPos, blockState, 2);
         level.setBlock(var7, var6, 2);
      }

   }

   public boolean canPlaceLiquid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
      return false;
   }

   public boolean placeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
      return false;
   }
}
