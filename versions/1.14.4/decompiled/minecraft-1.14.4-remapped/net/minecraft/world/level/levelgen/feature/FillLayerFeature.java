package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LayerConfiguration;

public class FillLayerFeature extends Feature {
   public FillLayerFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, LayerConfiguration layerConfiguration) {
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

      for(int var7 = 0; var7 < 16; ++var7) {
         for(int var8 = 0; var8 < 16; ++var8) {
            int var9 = blockPos.getX() + var7;
            int var10 = blockPos.getZ() + var8;
            int var11 = layerConfiguration.height;
            var6.set(var9, var11, var10);
            if(levelAccessor.getBlockState(var6).isAir()) {
               levelAccessor.setBlock(var6, layerConfiguration.state, 2);
            }
         }
      }

      return true;
   }
}
