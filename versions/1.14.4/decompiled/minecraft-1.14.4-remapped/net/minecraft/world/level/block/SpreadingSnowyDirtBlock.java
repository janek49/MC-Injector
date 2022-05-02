package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LayerLightEngine;

public abstract class SpreadingSnowyDirtBlock extends SnowyDirtBlock {
   protected SpreadingSnowyDirtBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   private static boolean canBeGrass(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockPos blockPos = blockPos.above();
      BlockState var4 = levelReader.getBlockState(blockPos);
      if(var4.getBlock() == Blocks.SNOW && ((Integer)var4.getValue(SnowLayerBlock.LAYERS)).intValue() == 1) {
         return true;
      } else {
         int var5 = LayerLightEngine.getLightBlockInto(levelReader, blockState, blockPos, var4, blockPos, Direction.UP, var4.getLightBlock(levelReader, blockPos));
         return var5 < levelReader.getMaxLightLevel();
      }
   }

   private static boolean canPropagate(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockPos blockPos = blockPos.above();
      return canBeGrass(blockState, levelReader, blockPos) && !levelReader.getFluidState(blockPos).is(FluidTags.WATER);
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!level.isClientSide) {
         if(!canBeGrass(blockState, level, blockPos)) {
            level.setBlockAndUpdate(blockPos, Blocks.DIRT.defaultBlockState());
         } else {
            if(level.getMaxLocalRawBrightness(blockPos.above()) >= 9) {
               BlockState blockState = this.defaultBlockState();

               for(int var6 = 0; var6 < 4; ++var6) {
                  BlockPos var7 = blockPos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                  if(level.getBlockState(var7).getBlock() == Blocks.DIRT && canPropagate(blockState, level, var7)) {
                     level.setBlockAndUpdate(var7, (BlockState)blockState.setValue(SNOWY, Boolean.valueOf(level.getBlockState(var7.above()).getBlock() == Blocks.SNOW)));
                  }
               }
            }

         }
      }
   }
}
