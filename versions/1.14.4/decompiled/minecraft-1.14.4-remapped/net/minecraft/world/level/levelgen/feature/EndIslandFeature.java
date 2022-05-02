package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class EndIslandFeature extends Feature {
   public EndIslandFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      float var6 = (float)(random.nextInt(3) + 4);

      for(int var7 = 0; var6 > 0.5F; --var7) {
         for(int var8 = Mth.floor(-var6); var8 <= Mth.ceil(var6); ++var8) {
            for(int var9 = Mth.floor(-var6); var9 <= Mth.ceil(var6); ++var9) {
               if((float)(var8 * var8 + var9 * var9) <= (var6 + 1.0F) * (var6 + 1.0F)) {
                  this.setBlock(levelAccessor, blockPos.offset(var8, var7, var9), Blocks.END_STONE.defaultBlockState());
               }
            }
         }

         var6 = (float)((double)var6 - ((double)random.nextInt(2) + 0.5D));
      }

      return true;
   }
}
