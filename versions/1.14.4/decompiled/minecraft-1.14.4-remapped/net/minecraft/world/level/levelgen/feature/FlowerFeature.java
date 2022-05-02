package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public abstract class FlowerFeature extends Feature {
   public FlowerFeature(Function function) {
      super(function, false);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      BlockState var6 = this.getRandomFlower(random, blockPos);
      int var7 = 0;

      for(int var8 = 0; var8 < 64; ++var8) {
         BlockPos var9 = blockPos.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
         if(levelAccessor.isEmptyBlock(var9) && var9.getY() < 255 && var6.canSurvive(levelAccessor, var9)) {
            levelAccessor.setBlock(var9, var6, 2);
            ++var7;
         }
      }

      return var7 > 0;
   }

   public abstract BlockState getRandomFlower(Random var1, BlockPos var2);
}
