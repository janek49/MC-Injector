package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SpringConfiguration;

public class SpringFeature extends Feature {
   public SpringFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SpringConfiguration springConfiguration) {
      if(!Block.equalsStone(levelAccessor.getBlockState(blockPos.above()).getBlock())) {
         return false;
      } else if(!Block.equalsStone(levelAccessor.getBlockState(blockPos.below()).getBlock())) {
         return false;
      } else {
         BlockState var6 = levelAccessor.getBlockState(blockPos);
         if(!var6.isAir() && !Block.equalsStone(var6.getBlock())) {
            return false;
         } else {
            int var7 = 0;
            int var8 = 0;
            if(Block.equalsStone(levelAccessor.getBlockState(blockPos.west()).getBlock())) {
               ++var8;
            }

            if(Block.equalsStone(levelAccessor.getBlockState(blockPos.east()).getBlock())) {
               ++var8;
            }

            if(Block.equalsStone(levelAccessor.getBlockState(blockPos.north()).getBlock())) {
               ++var8;
            }

            if(Block.equalsStone(levelAccessor.getBlockState(blockPos.south()).getBlock())) {
               ++var8;
            }

            int var9 = 0;
            if(levelAccessor.isEmptyBlock(blockPos.west())) {
               ++var9;
            }

            if(levelAccessor.isEmptyBlock(blockPos.east())) {
               ++var9;
            }

            if(levelAccessor.isEmptyBlock(blockPos.north())) {
               ++var9;
            }

            if(levelAccessor.isEmptyBlock(blockPos.south())) {
               ++var9;
            }

            if(var8 == 3 && var9 == 1) {
               levelAccessor.setBlock(blockPos, springConfiguration.state.createLegacyBlock(), 2);
               levelAccessor.getLiquidTicks().scheduleTick(blockPos, springConfiguration.state.getType(), 0);
               ++var7;
            }

            return var7 > 0;
         }
      }
   }
}
