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
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class NetherSurfaceBuilder extends SurfaceBuilder {
   private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();
   private static final BlockState NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
   private static final BlockState GRAVEL = Blocks.GRAVEL.defaultBlockState();
   private static final BlockState SOUL_SAND = Blocks.SOUL_SAND.defaultBlockState();
   protected long seed;
   protected PerlinNoise decorationNoise;

   public NetherSurfaceBuilder(Function function) {
      super(function);
   }

   public void apply(Random random, ChunkAccess chunkAccess, Biome biome, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, SurfaceBuilderBaseConfiguration surfaceBuilderBaseConfiguration) {
      int var15 = var11 + 1;
      int var16 = var4 & 15;
      int var17 = var5 & 15;
      double var18 = 0.03125D;
      boolean var20 = this.decorationNoise.getValue((double)var4 * 0.03125D, (double)var5 * 0.03125D, 0.0D) + random.nextDouble() * 0.2D > 0.0D;
      boolean var21 = this.decorationNoise.getValue((double)var4 * 0.03125D, 109.0D, (double)var5 * 0.03125D) + random.nextDouble() * 0.2D > 0.0D;
      int var22 = (int)(var7 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
      BlockPos.MutableBlockPos var23 = new BlockPos.MutableBlockPos();
      int var24 = -1;
      BlockState var25 = NETHERRACK;
      BlockState var26 = NETHERRACK;

      for(int var27 = 127; var27 >= 0; --var27) {
         var23.set(var16, var27, var17);
         BlockState var28 = chunkAccess.getBlockState(var23);
         if(var28.getBlock() != null && !var28.isAir()) {
            if(var28.getBlock() == var9.getBlock()) {
               if(var24 == -1) {
                  if(var22 <= 0) {
                     var25 = AIR;
                     var26 = NETHERRACK;
                  } else if(var27 >= var15 - 4 && var27 <= var15 + 1) {
                     var25 = NETHERRACK;
                     var26 = NETHERRACK;
                     if(var21) {
                        var25 = GRAVEL;
                        var26 = NETHERRACK;
                     }

                     if(var20) {
                        var25 = SOUL_SAND;
                        var26 = SOUL_SAND;
                     }
                  }

                  if(var27 < var15 && (var25 == null || var25.isAir())) {
                     var25 = var10;
                  }

                  var24 = var22;
                  if(var27 >= var15 - 1) {
                     chunkAccess.setBlockState(var23, var25, false);
                  } else {
                     chunkAccess.setBlockState(var23, var26, false);
                  }
               } else if(var24 > 0) {
                  --var24;
                  chunkAccess.setBlockState(var23, var26, false);
               }
            }
         } else {
            var24 = -1;
         }
      }

   }

   public void initNoise(long seed) {
      if(this.seed != seed || this.decorationNoise == null) {
         this.decorationNoise = new PerlinNoise(new WorldgenRandom(seed), 4);
      }

      this.seed = seed;
   }
}
