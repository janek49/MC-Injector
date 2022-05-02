package net.minecraft.world.level.levelgen.surfacebuilders;

import java.util.Random;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;

public class ConfiguredSurfaceBuilder {
   public final SurfaceBuilder surfaceBuilder;
   public final SurfaceBuilderConfiguration config;

   public ConfiguredSurfaceBuilder(SurfaceBuilder surfaceBuilder, SurfaceBuilderConfiguration config) {
      this.surfaceBuilder = surfaceBuilder;
      this.config = config;
   }

   public void apply(Random random, ChunkAccess chunkAccess, Biome biome, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12) {
      this.surfaceBuilder.apply(random, chunkAccess, biome, var4, var5, var6, var7, var9, var10, var11, var12, this.config);
   }

   public void initNoise(long l) {
      this.surfaceBuilder.initNoise(l);
   }

   public SurfaceBuilderConfiguration getSurfaceBuilderConfiguration() {
      return this.config;
   }
}
