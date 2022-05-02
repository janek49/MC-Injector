package net.minecraft.world.level.levelgen.feature;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreConfiguration;

public class OreFeature extends Feature {
   public OreFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, OreConfiguration oreConfiguration) {
      float var6 = random.nextFloat() * 3.1415927F;
      float var7 = (float)oreConfiguration.size / 8.0F;
      int var8 = Mth.ceil(((float)oreConfiguration.size / 16.0F * 2.0F + 1.0F) / 2.0F);
      double var9 = (double)((float)blockPos.getX() + Mth.sin(var6) * var7);
      double var11 = (double)((float)blockPos.getX() - Mth.sin(var6) * var7);
      double var13 = (double)((float)blockPos.getZ() + Mth.cos(var6) * var7);
      double var15 = (double)((float)blockPos.getZ() - Mth.cos(var6) * var7);
      int var17 = 2;
      double var18 = (double)(blockPos.getY() + random.nextInt(3) - 2);
      double var20 = (double)(blockPos.getY() + random.nextInt(3) - 2);
      int var22 = blockPos.getX() - Mth.ceil(var7) - var8;
      int var23 = blockPos.getY() - 2 - var8;
      int var24 = blockPos.getZ() - Mth.ceil(var7) - var8;
      int var25 = 2 * (Mth.ceil(var7) + var8);
      int var26 = 2 * (2 + var8);

      for(int var27 = var22; var27 <= var22 + var25; ++var27) {
         for(int var28 = var24; var28 <= var24 + var25; ++var28) {
            if(var23 <= levelAccessor.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, var27, var28)) {
               return this.doPlace(levelAccessor, random, oreConfiguration, var9, var11, var13, var15, var18, var20, var22, var23, var24, var25, var26);
            }
         }
      }

      return false;
   }

   protected boolean doPlace(LevelAccessor levelAccessor, Random random, OreConfiguration oreConfiguration, double var4, double var6, double var8, double var10, double var12, double var14, int var16, int var17, int var18, int var19, int var20) {
      int var21 = 0;
      BitSet var22 = new BitSet(var19 * var20 * var19);
      BlockPos.MutableBlockPos var23 = new BlockPos.MutableBlockPos();
      double[] vars24 = new double[oreConfiguration.size * 4];

      for(int var25 = 0; var25 < oreConfiguration.size; ++var25) {
         float var26 = (float)var25 / (float)oreConfiguration.size;
         double var27 = Mth.lerp((double)var26, var4, var6);
         double var29 = Mth.lerp((double)var26, var12, var14);
         double var31 = Mth.lerp((double)var26, var8, var10);
         double var33 = random.nextDouble() * (double)oreConfiguration.size / 16.0D;
         double var35 = ((double)(Mth.sin(3.1415927F * var26) + 1.0F) * var33 + 1.0D) / 2.0D;
         vars24[var25 * 4 + 0] = var27;
         vars24[var25 * 4 + 1] = var29;
         vars24[var25 * 4 + 2] = var31;
         vars24[var25 * 4 + 3] = var35;
      }

      for(int var25 = 0; var25 < oreConfiguration.size - 1; ++var25) {
         if(vars24[var25 * 4 + 3] > 0.0D) {
            for(int var26 = var25 + 1; var26 < oreConfiguration.size; ++var26) {
               if(vars24[var26 * 4 + 3] > 0.0D) {
                  double var27 = vars24[var25 * 4 + 0] - vars24[var26 * 4 + 0];
                  double var29 = vars24[var25 * 4 + 1] - vars24[var26 * 4 + 1];
                  double var31 = vars24[var25 * 4 + 2] - vars24[var26 * 4 + 2];
                  double var33 = vars24[var25 * 4 + 3] - vars24[var26 * 4 + 3];
                  if(var33 * var33 > var27 * var27 + var29 * var29 + var31 * var31) {
                     if(var33 > 0.0D) {
                        vars24[var26 * 4 + 3] = -1.0D;
                     } else {
                        vars24[var25 * 4 + 3] = -1.0D;
                     }
                  }
               }
            }
         }
      }

      for(int var25 = 0; var25 < oreConfiguration.size; ++var25) {
         double var26 = vars24[var25 * 4 + 3];
         if(var26 >= 0.0D) {
            double var28 = vars24[var25 * 4 + 0];
            double var30 = vars24[var25 * 4 + 1];
            double var32 = vars24[var25 * 4 + 2];
            int var34 = Math.max(Mth.floor(var28 - var26), var16);
            int var35 = Math.max(Mth.floor(var30 - var26), var17);
            int var36 = Math.max(Mth.floor(var32 - var26), var18);
            int var37 = Math.max(Mth.floor(var28 + var26), var34);
            int var38 = Math.max(Mth.floor(var30 + var26), var35);
            int var39 = Math.max(Mth.floor(var32 + var26), var36);

            for(int var40 = var34; var40 <= var37; ++var40) {
               double var41 = ((double)var40 + 0.5D - var28) / var26;
               if(var41 * var41 < 1.0D) {
                  for(int var43 = var35; var43 <= var38; ++var43) {
                     double var44 = ((double)var43 + 0.5D - var30) / var26;
                     if(var41 * var41 + var44 * var44 < 1.0D) {
                        for(int var46 = var36; var46 <= var39; ++var46) {
                           double var47 = ((double)var46 + 0.5D - var32) / var26;
                           if(var41 * var41 + var44 * var44 + var47 * var47 < 1.0D) {
                              int var49 = var40 - var16 + (var43 - var17) * var19 + (var46 - var18) * var19 * var20;
                              if(!var22.get(var49)) {
                                 var22.set(var49);
                                 var23.set(var40, var43, var46);
                                 if(oreConfiguration.target.getPredicate().test(levelAccessor.getBlockState(var23))) {
                                    levelAccessor.setBlock(var23, oreConfiguration.state, 2);
                                    ++var21;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return var21 > 0;
   }
}
