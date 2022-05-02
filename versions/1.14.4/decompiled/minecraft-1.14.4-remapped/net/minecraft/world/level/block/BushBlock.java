package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BushBlock extends Block {
   protected BushBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      Block var4 = blockState.getBlock();
      return var4 == Blocks.GRASS_BLOCK || var4 == Blocks.DIRT || var4 == Blocks.COARSE_DIRT || var4 == Blocks.PODZOL || var4 == Blocks.FARMLAND;
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return !var1.canSurvive(levelAccessor, var5)?Blocks.AIR.defaultBlockState():super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockPos blockPos = blockPos.below();
      return this.mayPlaceOn(levelReader.getBlockState(blockPos), levelReader, blockPos);
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return true;
   }
}
