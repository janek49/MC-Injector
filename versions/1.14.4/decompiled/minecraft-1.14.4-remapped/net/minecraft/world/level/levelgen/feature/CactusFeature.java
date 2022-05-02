package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class CactusFeature extends Feature {
   public CactusFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      for(int var6 = 0; var6 < 10; ++var6) {
         BlockPos var7 = blockPos.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
         if(levelAccessor.isEmptyBlock(var7)) {
            int var8 = 1 + random.nextInt(random.nextInt(3) + 1);

            for(int var9 = 0; var9 < var8; ++var9) {
               if(Blocks.CACTUS.defaultBlockState().canSurvive(levelAccessor, var7)) {
                  levelAccessor.setBlock(var7.above(var9), Blocks.CACTUS.defaultBlockState(), 2);
               }
            }
         }
      }

      return true;
   }
}
