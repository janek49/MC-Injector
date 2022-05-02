package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.ReplaceBlockConfiguration;

public class ReplaceBlockFeature extends Feature {
   public ReplaceBlockFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, ReplaceBlockConfiguration replaceBlockConfiguration) {
      if(levelAccessor.getBlockState(blockPos).getBlock() == replaceBlockConfiguration.target.getBlock()) {
         levelAccessor.setBlock(blockPos, replaceBlockConfiguration.state, 2);
      }

      return true;
   }
}
