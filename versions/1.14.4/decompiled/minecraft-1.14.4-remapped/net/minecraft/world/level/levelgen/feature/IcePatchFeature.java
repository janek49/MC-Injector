package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureRadius;

public class IcePatchFeature extends Feature {
   private final Block block = Blocks.PACKED_ICE;

   public IcePatchFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, FeatureRadius featureRadius) {
      while(levelAccessor.isEmptyBlock(blockPos) && blockPos.getY() > 2) {
         blockPos = blockPos.below();
      }

      if(levelAccessor.getBlockState(blockPos).getBlock() != Blocks.SNOW_BLOCK) {
         return false;
      } else {
         int var6 = random.nextInt(featureRadius.radius) + 2;
         int var7 = 1;

         for(int var8 = blockPos.getX() - var6; var8 <= blockPos.getX() + var6; ++var8) {
            for(int var9 = blockPos.getZ() - var6; var9 <= blockPos.getZ() + var6; ++var9) {
               int var10 = var8 - blockPos.getX();
               int var11 = var9 - blockPos.getZ();
               if(var10 * var10 + var11 * var11 <= var6 * var6) {
                  for(int var12 = blockPos.getY() - 1; var12 <= blockPos.getY() + 1; ++var12) {
                     BlockPos var13 = new BlockPos(var8, var12, var9);
                     Block var14 = levelAccessor.getBlockState(var13).getBlock();
                     if(Block.equalsDirt(var14) || var14 == Blocks.SNOW_BLOCK || var14 == Blocks.ICE) {
                        levelAccessor.setBlock(var13, this.block.defaultBlockState(), 2);
                     }
                  }
               }
            }
         }

         return true;
      }
   }
}
