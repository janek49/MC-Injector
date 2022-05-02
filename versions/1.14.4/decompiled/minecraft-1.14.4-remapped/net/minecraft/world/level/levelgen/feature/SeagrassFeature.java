package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallSeagrass;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SeagrassFeatureConfiguration;

public class SeagrassFeature extends Feature {
   public SeagrassFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SeagrassFeatureConfiguration seagrassFeatureConfiguration) {
      int var6 = 0;

      for(int var7 = 0; var7 < seagrassFeatureConfiguration.count; ++var7) {
         int var8 = random.nextInt(8) - random.nextInt(8);
         int var9 = random.nextInt(8) - random.nextInt(8);
         int var10 = levelAccessor.getHeight(Heightmap.Types.OCEAN_FLOOR, blockPos.getX() + var8, blockPos.getZ() + var9);
         BlockPos var11 = new BlockPos(blockPos.getX() + var8, var10, blockPos.getZ() + var9);
         if(levelAccessor.getBlockState(var11).getBlock() == Blocks.WATER) {
            boolean var12 = random.nextDouble() < seagrassFeatureConfiguration.tallSeagrassProbability;
            BlockState var13 = var12?Blocks.TALL_SEAGRASS.defaultBlockState():Blocks.SEAGRASS.defaultBlockState();
            if(var13.canSurvive(levelAccessor, var11)) {
               if(var12) {
                  BlockState var14 = (BlockState)var13.setValue(TallSeagrass.HALF, DoubleBlockHalf.UPPER);
                  BlockPos var15 = var11.above();
                  if(levelAccessor.getBlockState(var15).getBlock() == Blocks.WATER) {
                     levelAccessor.setBlock(var11, var13, 2);
                     levelAccessor.setBlock(var15, var14, 2);
                  }
               } else {
                  levelAccessor.setBlock(var11, var13, 2);
               }

               ++var6;
            }
         }
      }

      return var6 > 0;
   }
}
