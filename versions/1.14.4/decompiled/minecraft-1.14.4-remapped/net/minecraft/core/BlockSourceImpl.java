package net.minecraft.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockSourceImpl implements BlockSource {
   private final Level level;
   private final BlockPos pos;

   public BlockSourceImpl(Level level, BlockPos pos) {
      this.level = level;
      this.pos = pos;
   }

   public Level getLevel() {
      return this.level;
   }

   public double x() {
      return (double)this.pos.getX() + 0.5D;
   }

   public double y() {
      return (double)this.pos.getY() + 0.5D;
   }

   public double z() {
      return (double)this.pos.getZ() + 0.5D;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public BlockState getBlockState() {
      return this.level.getBlockState(this.pos);
   }

   public BlockEntity getEntity() {
      return this.level.getBlockEntity(this.pos);
   }
}
