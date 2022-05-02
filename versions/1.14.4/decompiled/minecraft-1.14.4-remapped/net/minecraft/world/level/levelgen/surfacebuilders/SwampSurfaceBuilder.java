package net.minecraft.world.level.levelgen.surfacebuilders;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;

public class SwampSurfaceBuilder extends SurfaceBuilder {
   public SwampSurfaceBuilder(Function function) {
      super(function);
   }

   public void apply(Random random, ChunkAccess chunkAccess, Biome biome, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, SurfaceBuilderBaseConfiguration surfaceBuilderBaseConfiguration) {
      double var15 = Biome.BIOME_INFO_NOISE.getValue((double)var4 * 0.25D, (double)var5 * 0.25D);
      if(var15 > 0.0D) {
         int var17 = var4 & 15;
         int var18 = var5 & 15;
         BlockPos.MutableBlockPos var19 = new BlockPos.MutableBlockPos();

         for(int var20 = var6; var20 >= 0; --var20) {
            var19.set(var17, var20, var18);
            if(!chunkAccess.getBlockState(var19).isAir()) {
               if(var20 == 62 && chunkAccess.getBlockState(var19).getBlock() != var10.getBlock()) {
                  chunkAccess.setBlockState(var19, var10, false);
               }
               break;
            }
         }
      }

      SurfaceBuilder.DEFAULT.apply(random, chunkAccess, biome, var4, var5, var6, var7, var9, var10, var11, var12, surfaceBuilderBaseConfiguration);
   }
}
