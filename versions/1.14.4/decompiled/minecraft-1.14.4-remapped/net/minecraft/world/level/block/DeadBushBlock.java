package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DeadBushBlock extends BushBlock {
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

   protected DeadBushBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      Block var4 = blockState.getBlock();
      return var4 == Blocks.SAND || var4 == Blocks.RED_SAND || var4 == Blocks.TERRACOTTA || var4 == Blocks.WHITE_TERRACOTTA || var4 == Blocks.ORANGE_TERRACOTTA || var4 == Blocks.MAGENTA_TERRACOTTA || var4 == Blocks.LIGHT_BLUE_TERRACOTTA || var4 == Blocks.YELLOW_TERRACOTTA || var4 == Blocks.LIME_TERRACOTTA || var4 == Blocks.PINK_TERRACOTTA || var4 == Blocks.GRAY_TERRACOTTA || var4 == Blocks.LIGHT_GRAY_TERRACOTTA || var4 == Blocks.CYAN_TERRACOTTA || var4 == Blocks.PURPLE_TERRACOTTA || var4 == Blocks.BLUE_TERRACOTTA || var4 == Blocks.BROWN_TERRACOTTA || var4 == Blocks.GREEN_TERRACOTTA || var4 == Blocks.RED_TERRACOTTA || var4 == Blocks.BLACK_TERRACOTTA || var4 == Blocks.DIRT || var4 == Blocks.COARSE_DIRT || var4 == Blocks.PODZOL;
   }
}
