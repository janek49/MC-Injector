package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockConfiguration;

public class SimpleBlockFeature extends Feature {
   public SimpleBlockFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SimpleBlockConfiguration simpleBlockConfiguration) {
      if(simpleBlockConfiguration.placeOn.contains(levelAccessor.getBlockState(blockPos.below())) && simpleBlockConfiguration.placeIn.contains(levelAccessor.getBlockState(blockPos)) && simpleBlockConfiguration.placeUnder.contains(levelAccessor.getBlockState(blockPos.above()))) {
         levelAccessor.setBlock(blockPos, simpleBlockConfiguration.toPlace, 2);
         return true;
      } else {
         return false;
      }
   }
}
