package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TrappedChestBlock extends ChestBlock {
   public TrappedChestBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new TrappedChestBlockEntity();
   }

   protected Stat getOpenChestStat() {
      return Stats.CUSTOM.get(Stats.TRIGGER_TRAPPED_CHEST);
   }

   public boolean isSignalSource(BlockState blockState) {
      return true;
   }

   public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return Mth.clamp(ChestBlockEntity.getOpenCount(blockGetter, blockPos), 0, 15);
   }

   public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return direction == Direction.UP?blockState.getSignal(blockGetter, blockPos, direction):0;
   }
}
