package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class ReedsFeature extends Feature {
   public ReedsFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      int var6 = 0;

      for(int var7 = 0; var7 < 20; ++var7) {
         BlockPos var8 = blockPos.offset(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
         if(levelAccessor.isEmptyBlock(var8)) {
            BlockPos var9 = var8.below();
            if(levelAccessor.getFluidState(var9.west()).is(FluidTags.WATER) || levelAccessor.getFluidState(var9.east()).is(FluidTags.WATER) || levelAccessor.getFluidState(var9.north()).is(FluidTags.WATER) || levelAccessor.getFluidState(var9.south()).is(FluidTags.WATER)) {
               int var10 = 2 + random.nextInt(random.nextInt(3) + 1);

               for(int var11 = 0; var11 < var10; ++var11) {
                  if(Blocks.SUGAR_CANE.defaultBlockState().canSurvive(levelAccessor, var8)) {
                     levelAccessor.setBlock(var8.above(var11), Blocks.SUGAR_CANE.defaultBlockState(), 2);
                     ++var6;
                  }
               }
            }
         }
      }

      return var6 > 0;
   }
}
