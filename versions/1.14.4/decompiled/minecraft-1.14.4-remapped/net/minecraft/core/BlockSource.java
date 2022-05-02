package net.minecraft.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.LocatableSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockSource extends LocatableSource {
   double x();

   double y();

   double z();

   BlockPos getPos();

   BlockState getBlockState();

   BlockEntity getEntity();
}
