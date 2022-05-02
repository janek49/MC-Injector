package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeConfiguration;
import net.minecraft.world.level.material.Material;

public class LakeFeature extends Feature {
   private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

   public LakeFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, LakeConfiguration lakeConfiguration) {
      while(blockPos.getY() > 5 && levelAccessor.isEmptyBlock(blockPos)) {
         blockPos = blockPos.below();
      }

      if(blockPos.getY() <= 4) {
         return false;
      } else {
         blockPos = blockPos.below(4);
         ChunkPos var6 = new ChunkPos(blockPos);
         if(!levelAccessor.getChunk(var6.x, var6.z, ChunkStatus.STRUCTURE_REFERENCES).getReferencesForFeature(Feature.VILLAGE.getFeatureName()).isEmpty()) {
            return false;
         } else {
            boolean[] vars7 = new boolean[2048];
            int var8 = random.nextInt(4) + 4;

            for(int var9 = 0; var9 < var8; ++var9) {
               double var10 = random.nextDouble() * 6.0D + 3.0D;
               double var12 = random.nextDouble() * 4.0D + 2.0D;
               double var14 = random.nextDouble() * 6.0D + 3.0D;
               double var16 = random.nextDouble() * (16.0D - var10 - 2.0D) + 1.0D + var10 / 2.0D;
               double var18 = random.nextDouble() * (8.0D - var12 - 4.0D) + 2.0D + var12 / 2.0D;
               double var20 = random.nextDouble() * (16.0D - var14 - 2.0D) + 1.0D + var14 / 2.0D;

               for(int var22 = 1; var22 < 15; ++var22) {
                  for(int var23 = 1; var23 < 15; ++var23) {
                     for(int var24 = 1; var24 < 7; ++var24) {
                        double var25 = ((double)var22 - var16) / (var10 / 2.0D);
                        double var27 = ((double)var24 - var18) / (var12 / 2.0D);
                        double var29 = ((double)var23 - var20) / (var14 / 2.0D);
                        double var31 = var25 * var25 + var27 * var27 + var29 * var29;
                        if(var31 < 1.0D) {
                           vars7[(var22 * 16 + var23) * 8 + var24] = true;
                        }
                     }
                  }
               }
            }

            for(int var9 = 0; var9 < 16; ++var9) {
               for(int var10 = 0; var10 < 16; ++var10) {
                  for(int var11 = 0; var11 < 8; ++var11) {
                     boolean var12 = !vars7[(var9 * 16 + var10) * 8 + var11] && (var9 < 15 && vars7[((var9 + 1) * 16 + var10) * 8 + var11] || var9 > 0 && vars7[((var9 - 1) * 16 + var10) * 8 + var11] || var10 < 15 && vars7[(var9 * 16 + var10 + 1) * 8 + var11] || var10 > 0 && vars7[(var9 * 16 + (var10 - 1)) * 8 + var11] || var11 < 7 && vars7[(var9 * 16 + var10) * 8 + var11 + 1] || var11 > 0 && vars7[(var9 * 16 + var10) * 8 + (var11 - 1)]);
                     if(var12) {
                        Material var13 = levelAccessor.getBlockState(blockPos.offset(var9, var11, var10)).getMaterial();
                        if(var11 >= 4 && var13.isLiquid()) {
                           return false;
                        }

                        if(var11 < 4 && !var13.isSolid() && levelAccessor.getBlockState(blockPos.offset(var9, var11, var10)) != lakeConfiguration.state) {
                           return false;
                        }
                     }
                  }
               }
            }

            for(int var9 = 0; var9 < 16; ++var9) {
               for(int var10 = 0; var10 < 16; ++var10) {
                  for(int var11 = 0; var11 < 8; ++var11) {
                     if(vars7[(var9 * 16 + var10) * 8 + var11]) {
                        levelAccessor.setBlock(blockPos.offset(var9, var11, var10), var11 >= 4?AIR:lakeConfiguration.state, 2);
                     }
                  }
               }
            }

            for(int var9 = 0; var9 < 16; ++var9) {
               for(int var10 = 0; var10 < 16; ++var10) {
                  for(int var11 = 4; var11 < 8; ++var11) {
                     if(vars7[(var9 * 16 + var10) * 8 + var11]) {
                        BlockPos var12 = blockPos.offset(var9, var11 - 1, var10);
                        if(Block.equalsDirt(levelAccessor.getBlockState(var12).getBlock()) && levelAccessor.getBrightness(LightLayer.SKY, blockPos.offset(var9, var11, var10)) > 0) {
                           Biome var13 = levelAccessor.getBiome(var12);
                           if(var13.getSurfaceBuilderConfig().getTopMaterial().getBlock() == Blocks.MYCELIUM) {
                              levelAccessor.setBlock(var12, Blocks.MYCELIUM.defaultBlockState(), 2);
                           } else {
                              levelAccessor.setBlock(var12, Blocks.GRASS_BLOCK.defaultBlockState(), 2);
                           }
                        }
                     }
                  }
               }
            }

            if(lakeConfiguration.state.getMaterial() == Material.LAVA) {
               for(int var9 = 0; var9 < 16; ++var9) {
                  for(int var10 = 0; var10 < 16; ++var10) {
                     for(int var11 = 0; var11 < 8; ++var11) {
                        boolean var12 = !vars7[(var9 * 16 + var10) * 8 + var11] && (var9 < 15 && vars7[((var9 + 1) * 16 + var10) * 8 + var11] || var9 > 0 && vars7[((var9 - 1) * 16 + var10) * 8 + var11] || var10 < 15 && vars7[(var9 * 16 + var10 + 1) * 8 + var11] || var10 > 0 && vars7[(var9 * 16 + (var10 - 1)) * 8 + var11] || var11 < 7 && vars7[(var9 * 16 + var10) * 8 + var11 + 1] || var11 > 0 && vars7[(var9 * 16 + var10) * 8 + (var11 - 1)]);
                        if(var12 && (var11 < 4 || random.nextInt(2) != 0) && levelAccessor.getBlockState(blockPos.offset(var9, var11, var10)).getMaterial().isSolid()) {
                           levelAccessor.setBlock(blockPos.offset(var9, var11, var10), Blocks.STONE.defaultBlockState(), 2);
                        }
                     }
                  }
               }
            }

            if(lakeConfiguration.state.getMaterial() == Material.WATER) {
               for(int var9 = 0; var9 < 16; ++var9) {
                  for(int var10 = 0; var10 < 16; ++var10) {
                     int var11 = 4;
                     BlockPos var12 = blockPos.offset(var9, 4, var10);
                     if(levelAccessor.getBiome(var12).shouldFreeze(levelAccessor, var12, false)) {
                        levelAccessor.setBlock(var12, Blocks.ICE.defaultBlockState(), 2);
                     }
                  }
               }
            }

            return true;
         }
      }
   }
}
