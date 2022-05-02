package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.BushConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;

public class BushFeature extends Feature {
   public BushFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, BushConfiguration bushConfiguration) {
      int var6 = 0;
      BlockState var7 = bushConfiguration.state;

      for(int var8 = 0; var8 < 64; ++var8) {
         BlockPos var9 = blockPos.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
         if(levelAccessor.isEmptyBlock(var9) && (!levelAccessor.getDimension().isHasCeiling() || var9.getY() < 255) && var7.canSurvive(levelAccessor, var9)) {
            levelAccessor.setBlock(var9, var7, 2);
            ++var6;
         }
      }

      return var6 > 0;
   }
}
