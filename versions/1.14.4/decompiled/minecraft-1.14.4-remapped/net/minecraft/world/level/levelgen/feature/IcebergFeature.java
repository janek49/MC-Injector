package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.IcebergConfiguration;
import net.minecraft.world.level.material.Material;

public class IcebergFeature extends Feature {
   public IcebergFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, IcebergConfiguration icebergConfiguration) {
      blockPos = new BlockPos(blockPos.getX(), levelAccessor.getSeaLevel(), blockPos.getZ());
      boolean var6 = random.nextDouble() > 0.7D;
      BlockState var7 = icebergConfiguration.state;
      double var8 = random.nextDouble() * 2.0D * 3.141592653589793D;
      int var10 = 11 - random.nextInt(5);
      int var11 = 3 + random.nextInt(3);
      boolean var12 = random.nextDouble() > 0.7D;
      int var13 = 11;
      int var14 = var12?random.nextInt(6) + 6:random.nextInt(15) + 3;
      if(!var12 && random.nextDouble() > 0.9D) {
         var14 += random.nextInt(19) + 7;
      }

      int var15 = Math.min(var14 + random.nextInt(11), 18);
      int var16 = Math.min(var14 + random.nextInt(7) - random.nextInt(5), 11);
      int var17 = var12?var10:11;

      for(int var18 = -var17; var18 < var17; ++var18) {
         for(int var19 = -var17; var19 < var17; ++var19) {
            for(int var20 = 0; var20 < var14; ++var20) {
               int var21 = var12?this.heightDependentRadiusEllipse(var20, var14, var16):this.heightDependentRadiusRound(random, var20, var14, var16);
               if(var12 || var18 < var21) {
                  this.generateIcebergBlock(levelAccessor, random, blockPos, var14, var18, var20, var19, var21, var17, var12, var11, var8, var6, var7);
               }
            }
         }
      }

      this.smooth(levelAccessor, blockPos, var16, var14, var12, var10);

      for(int var18 = -var17; var18 < var17; ++var18) {
         for(int var19 = -var17; var19 < var17; ++var19) {
            for(int var20 = -1; var20 > -var15; --var20) {
               int var21 = var12?Mth.ceil((float)var17 * (1.0F - (float)Math.pow((double)var20, 2.0D) / ((float)var15 * 8.0F))):var17;
               int var22 = this.heightDependentRadiusSteep(random, -var20, var15, var16);
               if(var18 < var22) {
                  this.generateIcebergBlock(levelAccessor, random, blockPos, var15, var18, var20, var19, var22, var21, var12, var11, var8, var6, var7);
               }
            }
         }
      }

      boolean var18 = var12?random.nextDouble() > 0.1D:random.nextDouble() > 0.7D;
      if(var18) {
         this.generateCutOut(random, levelAccessor, var16, var14, blockPos, var12, var10, var8, var11);
      }

      return true;
   }

   private void generateCutOut(Random random, LevelAccessor levelAccessor, int var3, int var4, BlockPos blockPos, boolean var6, int var7, double var8, int var10) {
      int var11 = random.nextBoolean()?-1:1;
      int var12 = random.nextBoolean()?-1:1;
      int var13 = random.nextInt(Math.max(var3 / 2 - 2, 1));
      if(random.nextBoolean()) {
         var13 = var3 / 2 + 1 - random.nextInt(Math.max(var3 - var3 / 2 - 1, 1));
      }

      int var14 = random.nextInt(Math.max(var3 / 2 - 2, 1));
      if(random.nextBoolean()) {
         var14 = var3 / 2 + 1 - random.nextInt(Math.max(var3 - var3 / 2 - 1, 1));
      }

      if(var6) {
         var13 = var14 = random.nextInt(Math.max(var7 - 5, 1));
      }

      BlockPos var15 = new BlockPos(var11 * var13, 0, var12 * var14);
      double var16 = var6?var8 + 1.5707963267948966D:random.nextDouble() * 2.0D * 3.141592653589793D;

      for(int var18 = 0; var18 < var4 - 3; ++var18) {
         int var19 = this.heightDependentRadiusRound(random, var18, var4, var3);
         this.carve(var19, var18, blockPos, levelAccessor, false, var16, var15, var7, var10);
      }

      for(int var18 = -1; var18 > -var4 + random.nextInt(5); --var18) {
         int var19 = this.heightDependentRadiusSteep(random, -var18, var4, var3);
         this.carve(var19, var18, blockPos, levelAccessor, true, var16, var15, var7, var10);
      }

   }

   private void carve(int var1, int var2, BlockPos var3, LevelAccessor levelAccessor, boolean var5, double var6, BlockPos var8, int var9, int var10) {
      int var11 = var1 + 1 + var9 / 3;
      int var12 = Math.min(var1 - 3, 3) + var10 / 2 - 1;

      for(int var13 = -var11; var13 < var11; ++var13) {
         for(int var14 = -var11; var14 < var11; ++var14) {
            double var15 = this.signedDistanceEllipse(var13, var14, var8, var11, var12, var6);
            if(var15 < 0.0D) {
               BlockPos var17 = var3.offset(var13, var2, var14);
               Block var18 = levelAccessor.getBlockState(var17).getBlock();
               if(this.isIcebergBlock(var18) || var18 == Blocks.SNOW_BLOCK) {
                  if(var5) {
                     this.setBlock(levelAccessor, var17, Blocks.WATER.defaultBlockState());
                  } else {
                     this.setBlock(levelAccessor, var17, Blocks.AIR.defaultBlockState());
                     this.removeFloatingSnowLayer(levelAccessor, var17);
                  }
               }
            }
         }
      }

   }

   private void removeFloatingSnowLayer(LevelAccessor levelAccessor, BlockPos blockPos) {
      if(levelAccessor.getBlockState(blockPos.above()).getBlock() == Blocks.SNOW) {
         this.setBlock(levelAccessor, blockPos.above(), Blocks.AIR.defaultBlockState());
      }

   }

   private void generateIcebergBlock(LevelAccessor levelAccessor, Random random, BlockPos blockPos, int var4, int var5, int var6, int var7, int var8, int var9, boolean var10, int var11, double var12, boolean var14, BlockState blockState) {
      double var16 = var10?this.signedDistanceEllipse(var5, var7, BlockPos.ZERO, var9, this.getEllipseC(var6, var4, var11), var12):this.signedDistanceCircle(var5, var7, BlockPos.ZERO, var8, random);
      if(var16 < 0.0D) {
         BlockPos var18 = blockPos.offset(var5, var6, var7);
         double var19 = var10?-0.5D:(double)(-6 - random.nextInt(3));
         if(var16 > var19 && random.nextDouble() > 0.9D) {
            return;
         }

         this.setIcebergBlock(var18, levelAccessor, random, var4 - var6, var4, var10, var14, blockState);
      }

   }

   private void setIcebergBlock(BlockPos blockPos, LevelAccessor levelAccessor, Random random, int var4, int var5, boolean var6, boolean var7, BlockState blockState) {
      BlockState blockState = levelAccessor.getBlockState(blockPos);
      Block var10 = blockState.getBlock();
      if(blockState.getMaterial() == Material.AIR || var10 == Blocks.SNOW_BLOCK || var10 == Blocks.ICE || var10 == Blocks.WATER) {
         boolean var11 = !var6 || random.nextDouble() > 0.05D;
         int var12 = var6?3:2;
         if(var7 && var10 != Blocks.WATER && (double)var4 <= (double)random.nextInt(Math.max(1, var5 / var12)) + (double)var5 * 0.6D && var11) {
            this.setBlock(levelAccessor, blockPos, Blocks.SNOW_BLOCK.defaultBlockState());
         } else {
            this.setBlock(levelAccessor, blockPos, blockState);
         }
      }

   }

   private int getEllipseC(int var1, int var2, int var3) {
      int var4 = var3;
      if(var1 > 0 && var2 - var1 <= 3) {
         var4 = var3 - (4 - (var2 - var1));
      }

      return var4;
   }

   private double signedDistanceCircle(int var1, int var2, BlockPos blockPos, int var4, Random random) {
      float var6 = 10.0F * Mth.clamp(random.nextFloat(), 0.2F, 0.8F) / (float)var4;
      return (double)var6 + Math.pow((double)(var1 - blockPos.getX()), 2.0D) + Math.pow((double)(var2 - blockPos.getZ()), 2.0D) - Math.pow((double)var4, 2.0D);
   }

   private double signedDistanceEllipse(int var1, int var2, BlockPos blockPos, int var4, int var5, double var6) {
      return Math.pow(((double)(var1 - blockPos.getX()) * Math.cos(var6) - (double)(var2 - blockPos.getZ()) * Math.sin(var6)) / (double)var4, 2.0D) + Math.pow(((double)(var1 - blockPos.getX()) * Math.sin(var6) + (double)(var2 - blockPos.getZ()) * Math.cos(var6)) / (double)var5, 2.0D) - 1.0D;
   }

   private int heightDependentRadiusRound(Random random, int var2, int var3, int var4) {
      float var5 = 3.5F - random.nextFloat();
      float var6 = (1.0F - (float)Math.pow((double)var2, 2.0D) / ((float)var3 * var5)) * (float)var4;
      if(var3 > 15 + random.nextInt(5)) {
         int var7 = var2 < 3 + random.nextInt(6)?var2 / 2:var2;
         var6 = (1.0F - (float)var7 / ((float)var3 * var5 * 0.4F)) * (float)var4;
      }

      return Mth.ceil(var6 / 2.0F);
   }

   private int heightDependentRadiusEllipse(int var1, int var2, int var3) {
      float var4 = 1.0F;
      float var5 = (1.0F - (float)Math.pow((double)var1, 2.0D) / ((float)var2 * 1.0F)) * (float)var3;
      return Mth.ceil(var5 / 2.0F);
   }

   private int heightDependentRadiusSteep(Random random, int var2, int var3, int var4) {
      float var5 = 1.0F + random.nextFloat() / 2.0F;
      float var6 = (1.0F - (float)var2 / ((float)var3 * var5)) * (float)var4;
      return Mth.ceil(var6 / 2.0F);
   }

   private boolean isIcebergBlock(Block block) {
      return block == Blocks.PACKED_ICE || block == Blocks.SNOW_BLOCK || block == Blocks.BLUE_ICE;
   }

   private boolean belowIsAir(BlockGetter blockGetter, BlockPos blockPos) {
      return blockGetter.getBlockState(blockPos.below()).getMaterial() == Material.AIR;
   }

   private void smooth(LevelAccessor levelAccessor, BlockPos blockPos, int var3, int var4, boolean var5, int var6) {
      int var7 = var5?var6:var3 / 2;

      for(int var8 = -var7; var8 <= var7; ++var8) {
         for(int var9 = -var7; var9 <= var7; ++var9) {
            for(int var10 = 0; var10 <= var4; ++var10) {
               BlockPos var11 = blockPos.offset(var8, var10, var9);
               Block var12 = levelAccessor.getBlockState(var11).getBlock();
               if(this.isIcebergBlock(var12) || var12 == Blocks.SNOW) {
                  if(this.belowIsAir(levelAccessor, var11)) {
                     this.setBlock(levelAccessor, var11, Blocks.AIR.defaultBlockState());
                     this.setBlock(levelAccessor, var11.above(), Blocks.AIR.defaultBlockState());
                  } else if(this.isIcebergBlock(var12)) {
                     Block[] vars13 = new Block[]{levelAccessor.getBlockState(var11.west()).getBlock(), levelAccessor.getBlockState(var11.east()).getBlock(), levelAccessor.getBlockState(var11.north()).getBlock(), levelAccessor.getBlockState(var11.south()).getBlock()};
                     int var14 = 0;

                     for(Block var18 : vars13) {
                        if(!this.isIcebergBlock(var18)) {
                           ++var14;
                        }
                     }

                     if(var14 >= 3) {
                        this.setBlock(levelAccessor, var11, Blocks.AIR.defaultBlockState());
                     }
                  }
               }
            }
         }
      }

   }
}
