package net.minecraft.world.level.biome;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSourceSettings;
import net.minecraft.world.level.biome.Biomes;

public class CheckerboardBiomeSourceSettings implements BiomeSourceSettings {
   private Biome[] allowedBiomes = new Biome[]{Biomes.PLAINS};
   private int size = 1;

   public CheckerboardBiomeSourceSettings setAllowedBiomes(Biome[] allowedBiomes) {
      this.allowedBiomes = allowedBiomes;
      return this;
   }

   public CheckerboardBiomeSourceSettings setSize(int size) {
      this.size = size;
      return this;
   }

   public Biome[] getAllowedBiomes() {
      return this.allowedBiomes;
   }

   public int getSize() {
      return this.size;
   }
}
