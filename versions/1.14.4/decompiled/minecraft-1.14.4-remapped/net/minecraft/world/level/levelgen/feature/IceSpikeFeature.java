package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class IceSpikeFeature extends Feature {
   public IceSpikeFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      while(levelAccessor.isEmptyBlock(blockPos) && blockPos.getY() > 2) {
         blockPos = blockPos.below();
      }

      if(levelAccessor.getBlockState(blockPos).getBlock() != Blocks.SNOW_BLOCK) {
         return false;
      } else {
         blockPos = blockPos.above(random.nextInt(4));
         int var6 = random.nextInt(4) + 7;
         int var7 = var6 / 4 + random.nextInt(2);
         if(var7 > 1 && random.nextInt(60) == 0) {
            blockPos = blockPos.above(10 + random.nextInt(30));
         }

         for(int var8 = 0; var8 < var6; ++var8) {
            float var9 = (1.0F - (float)var8 / (float)var6) * (float)var7;
            int var10 = Mth.ceil(var9);

            for(int var11 = -var10; var11 <= var10; ++var11) {
               float var12 = (float)Mth.abs(var11) - 0.25F;

               for(int var13 = -var10; var13 <= var10; ++var13) {
                  float var14 = (float)Mth.abs(var13) - 0.25F;
                  if((var11 == 0 && var13 == 0 || var12 * var12 + var14 * var14 <= var9 * var9) && (var11 != -var10 && var11 != var10 && var13 != -var10 && var13 != var10 || random.nextFloat() <= 0.75F)) {
                     BlockState var15 = levelAccessor.getBlockState(blockPos.offset(var11, var8, var13));
                     Block var16 = var15.getBlock();
                     if(var15.isAir() || Block.equalsDirt(var16) || var16 == Blocks.SNOW_BLOCK || var16 == Blocks.ICE) {
                        this.setBlock(levelAccessor, blockPos.offset(var11, var8, var13), Blocks.PACKED_ICE.defaultBlockState());
                     }

                     if(var8 != 0 && var10 > 1) {
                        var15 = levelAccessor.getBlockState(blockPos.offset(var11, -var8, var13));
                        var16 = var15.getBlock();
                        if(var15.isAir() || Block.equalsDirt(var16) || var16 == Blocks.SNOW_BLOCK || var16 == Blocks.ICE) {
                           this.setBlock(levelAccessor, blockPos.offset(var11, -var8, var13), Blocks.PACKED_ICE.defaultBlockState());
                        }
                     }
                  }
               }
            }
         }

         int var8 = var7 - 1;
         if(var8 < 0) {
            var8 = 0;
         } else if(var8 > 1) {
            var8 = 1;
         }

         for(int var9 = -var8; var9 <= var8; ++var9) {
            for(int var10 = -var8; var10 <= var8; ++var10) {
               BlockPos var11 = blockPos.offset(var9, -1, var10);
               int var12 = 50;
               if(Math.abs(var9) == 1 && Math.abs(var10) == 1) {
                  var12 = random.nextInt(5);
               }

               while(var11.getY() > 50) {
                  BlockState var13 = levelAccessor.getBlockState(var11);
                  Block var14 = var13.getBlock();
                  if(!var13.isAir() && !Block.equalsDirt(var14) && var14 != Blocks.SNOW_BLOCK && var14 != Blocks.ICE && var14 != Blocks.PACKED_ICE) {
                     break;
                  }

                  this.setBlock(levelAccessor, var11, Blocks.PACKED_ICE.defaultBlockState());
                  var11 = var11.below();
                  --var12;
                  if(var12 <= 0) {
                     var11 = var11.below(random.nextInt(5) + 1);
                     var12 = random.nextInt(5);
                  }
               }
            }
         }

         return true;
      }
   }
}
