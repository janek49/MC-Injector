package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class KelpPlantBlock extends Block implements LiquidBlockContainer {
   private final KelpBlock top;

   protected KelpPlantBlock(KelpBlock top, Block.Properties block$Properties) {
      super(block$Properties);
      this.top = top;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public FluidState getFluidState(BlockState blockState) {
      return Fluids.WATER.getSource(false);
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!blockState.canSurvive(level, blockPos)) {
         level.destroyBlock(blockPos, true);
      }

      super.tick(blockState, level, blockPos, random);
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(direction == Direction.DOWN && !var1.canSurvive(levelAccessor, var5)) {
         levelAccessor.getBlockTicks().scheduleTick(var5, this, 1);
      }

      if(direction == Direction.UP) {
         Block var7 = var3.getBlock();
         if(var7 != this && var7 != this.top) {
            return this.top.getStateForPlacement(levelAccessor);
         }
      }

      levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockPos blockPos = blockPos.below();
      BlockState var5 = levelReader.getBlockState(blockPos);
      Block var6 = var5.getBlock();
      return var6 != Blocks.MAGMA_BLOCK && (var6 == this || var5.isFaceSturdy(levelReader, blockPos, Direction.UP));
   }

   public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      return new ItemStack(Blocks.KELP);
   }

   public boolean canPlaceLiquid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
      return false;
   }

   public boolean placeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
      return false;
   }
}
