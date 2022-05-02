package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public abstract class CoralFeature extends Feature {
   public CoralFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      BlockState var6 = ((Block)BlockTags.CORAL_BLOCKS.getRandomElement(random)).defaultBlockState();
      return this.placeFeature(levelAccessor, random, blockPos, var6);
   }

   protected abstract boolean placeFeature(LevelAccessor var1, Random var2, BlockPos var3, BlockState var4);

   protected boolean placeCoralBlock(LevelAccessor levelAccessor, Random random, BlockPos blockPos, BlockState blockState) {
      BlockPos blockPos = blockPos.above();
      BlockState var6 = levelAccessor.getBlockState(blockPos);
      if((var6.getBlock() == Blocks.WATER || var6.is(BlockTags.CORALS)) && levelAccessor.getBlockState(blockPos).getBlock() == Blocks.WATER) {
         levelAccessor.setBlock(blockPos, blockState, 3);
         if(random.nextFloat() < 0.25F) {
            levelAccessor.setBlock(blockPos, ((Block)BlockTags.CORALS.getRandomElement(random)).defaultBlockState(), 2);
         } else if(random.nextFloat() < 0.05F) {
            levelAccessor.setBlock(blockPos, (BlockState)Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.PICKLES, Integer.valueOf(random.nextInt(4) + 1)), 2);
         }

         for(Direction var8 : Direction.Plane.HORIZONTAL) {
            if(random.nextFloat() < 0.2F) {
               BlockPos var9 = blockPos.relative(var8);
               if(levelAccessor.getBlockState(var9).getBlock() == Blocks.WATER) {
                  BlockState var10 = (BlockState)((Block)BlockTags.WALL_CORALS.getRandomElement(random)).defaultBlockState().setValue(BaseCoralWallFanBlock.FACING, var8);
                  levelAccessor.setBlock(var9, var10, 2);
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
