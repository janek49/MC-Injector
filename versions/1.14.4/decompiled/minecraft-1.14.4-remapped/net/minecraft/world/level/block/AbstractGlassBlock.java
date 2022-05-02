package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractGlassBlock extends HalfTransparentBlock {
   protected AbstractGlassBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return 1.0F;
   }

   public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return true;
   }

   public boolean isViewBlocking(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return false;
   }

   public boolean isRedstoneConductor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return false;
   }

   public boolean isValidSpawn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType entityType) {
      return false;
   }
}
