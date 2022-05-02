package net.minecraft.world.level.levelgen.surfacebuilders;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;

public class MountainSurfaceBuilder extends SurfaceBuilder {
   public MountainSurfaceBuilder(Function function) {
      super(function);
   }

   public void apply(Random random, ChunkAccess chunkAccess, Biome biome, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, SurfaceBuilderBaseConfiguration surfaceBuilderBaseConfiguration) {
      if(var7 > 1.0D) {
         SurfaceBuilder.DEFAULT.apply(random, chunkAccess, biome, var4, var5, var6, var7, var9, var10, var11, var12, SurfaceBuilder.CONFIG_STONE);
      } else {
         SurfaceBuilder.DEFAULT.apply(random, chunkAccess, biome, var4, var5, var6, var7, var9, var10, var11, var12, SurfaceBuilder.CONFIG_GRASS);
      }

   }
}
