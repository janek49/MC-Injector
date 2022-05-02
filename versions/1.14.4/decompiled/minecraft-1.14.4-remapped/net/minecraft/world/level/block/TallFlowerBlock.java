package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;

public class TallFlowerBlock extends DoublePlantBlock implements BonemealableBlock {
   public TallFlowerBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
      return false;
   }

   public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean var4) {
      return true;
   }

   public boolean isBonemealSuccess(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      return true;
   }

   public void performBonemeal(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      popResource(level, blockPos, new ItemStack(this));
   }
}
