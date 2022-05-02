package net.minecraft.world.level.levelgen.surfacebuilders;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.material.Material;

public class FrozenOceanSurfaceBuilder extends SurfaceBuilder {
   protected static final BlockState PACKED_ICE = Blocks.PACKED_ICE.defaultBlockState();
   protected static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.defaultBlockState();
   private static final BlockState AIR = Blocks.AIR.defaultBlockState();
   private static final BlockState GRAVEL = Blocks.GRAVEL.defaultBlockState();
   private static final BlockState ICE = Blocks.ICE.defaultBlockState();
   private PerlinSimplexNoise icebergNoise;
   private PerlinSimplexNoise icebergRoofNoise;
   private long seed;

   public FrozenOceanSurfaceBuilder(Function function) {
      super(function);
   }

   public void apply(Random random, ChunkAccess chunkAccess, Biome biome, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, SurfaceBuilderBaseConfiguration surfaceBuilderBaseConfiguration) {
      double var15 = 0.0D;
      double var17 = 0.0D;
      BlockPos.MutableBlockPos var19 = new BlockPos.MutableBlockPos();
      float var20 = biome.getTemperature(var19.set(var4, 63, var5));
      double var21 = Math.min(Math.abs(var7), this.icebergNoise.getValue((double)var4 * 0.1D, (double)var5 * 0.1D));
      if(var21 > 1.8D) {
         double var23 = 0.09765625D;
         double var25 = Math.abs(this.icebergRoofNoise.getValue((double)var4 * 0.09765625D, (double)var5 * 0.09765625D));
         var15 = var21 * var21 * 1.2D;
         double var27 = Math.ceil(var25 * 40.0D) + 14.0D;
         if(var15 > var27) {
            var15 = var27;
         }

         if(var20 > 0.1F) {
            var15 -= 2.0D;
         }

         if(var15 > 2.0D) {
            var17 = (double)var11 - var15 - 7.0D;
            var15 = var15 + (double)var11;
         } else {
            var15 = 0.0D;
         }
      }

      int var23 = var4 & 15;
      int var24 = var5 & 15;
      BlockState var25 = biome.getSurfaceBuilderConfig().getUnderMaterial();
      BlockState var26 = biome.getSurfaceBuilderConfig().getTopMaterial();
      int var27 = (int)(var7 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
      int var28 = -1;
      int var29 = 0;
      int var30 = 2 + random.nextInt(4);
      int var31 = var11 + 18 + random.nextInt(10);

      for(int var32 = Math.max(var6, (int)var15 + 1); var32 >= 0; --var32) {
         var19.set(var23, var32, var24);
         if(chunkAccess.getBlockState(var19).isAir() && var32 < (int)var15 && random.nextDouble() > 0.01D) {
            chunkAccess.setBlockState(var19, PACKED_ICE, false);
         } else if(chunkAccess.getBlockState(var19).getMaterial() == Material.WATER && var32 > (int)var17 && var32 < var11 && var17 != 0.0D && random.nextDouble() > 0.15D) {
            chunkAccess.setBlockState(var19, PACKED_ICE, false);
         }

         BlockState var33 = chunkAccess.getBlockState(var19);
         if(var33.isAir()) {
            var28 = -1;
         } else if(var33.getBlock() != var9.getBlock()) {
            if(var33.getBlock() == Blocks.PACKED_ICE && var29 <= var30 && var32 > var31) {
               chunkAccess.setBlockState(var19, SNOW_BLOCK, false);
               ++var29;
            }
         } else if(var28 == -1) {
            if(var27 <= 0) {
               var26 = AIR;
               var25 = var9;
            } else if(var32 >= var11 - 4 && var32 <= var11 + 1) {
               var26 = biome.getSurfaceBuilderConfig().getTopMaterial();
               var25 = biome.getSurfaceBuilderConfig().getUnderMaterial();
            }

            if(var32 < var11 && (var26 == null || var26.isAir())) {
               if(biome.getTemperature(var19.set(var4, var32, var5)) < 0.15F) {
                  var26 = ICE;
               } else {
                  var26 = var10;
               }
            }

            var28 = var27;
            if(var32 >= var11 - 1) {
               chunkAccess.setBlockState(var19, var26, false);
            } else if(var32 < var11 - 7 - var27) {
               var26 = AIR;
               var25 = var9;
               chunkAccess.setBlockState(var19, GRAVEL, false);
            } else {
               chunkAccess.setBlockState(var19, var25, false);
            }
         } else if(var28 > 0) {
            --var28;
            chunkAccess.setBlockState(var19, var25, false);
            if(var28 == 0 && var25.getBlock() == Blocks.SAND && var27 > 1) {
               var28 = random.nextInt(4) + Math.max(0, var32 - 63);
               var25 = var25.getBlock() == Blocks.RED_SAND?Blocks.RED_SANDSTONE.defaultBlockState():Blocks.SANDSTONE.defaultBlockState();
            }
         }
      }

   }

   public void initNoise(long seed) {
      if(this.seed != seed || this.icebergNoise == null || this.icebergRoofNoise == null) {
         Random var3 = new WorldgenRandom(seed);
         this.icebergNoise = new PerlinSimplexNoise(var3, 4);
         this.icebergRoofNoise = new PerlinSimplexNoise(var3, 1);
      }

      this.seed = seed;
   }
}
