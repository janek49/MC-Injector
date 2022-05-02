package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public enum EmptyBlockGetter implements BlockGetter {
   INSTANCE;

   @Nullable
   public BlockEntity getBlockEntity(BlockPos blockPos) {
      return null;
   }

   public BlockState getBlockState(BlockPos blockPos) {
      return Blocks.AIR.defaultBlockState();
   }

   public FluidState getFluidState(BlockPos blockPos) {
      return Fluids.EMPTY.defaultFluidState();
   }
}
