package net.minecraft.world.level.biome;

import net.minecraft.world.level.biome.BiomeSourceSettings;

public class TheEndBiomeSourceSettings implements BiomeSourceSettings {
   private long seed;

   public TheEndBiomeSourceSettings setSeed(long seed) {
      this.seed = seed;
      return this;
   }

   public long getSeed() {
      return this.seed;
   }
}
