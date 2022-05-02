package net.minecraft.world.level.biome;

import net.minecraft.world.level.biome.BiomeSourceSettings;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import net.minecraft.world.level.storage.LevelData;

public class OverworldBiomeSourceSettings implements BiomeSourceSettings {
   private LevelData levelData;
   private OverworldGeneratorSettings generatorSettings;

   public OverworldBiomeSourceSettings setLevelData(LevelData levelData) {
      this.levelData = levelData;
      return this;
   }

   public OverworldBiomeSourceSettings setGeneratorSettings(OverworldGeneratorSettings generatorSettings) {
      this.generatorSettings = generatorSettings;
      return this;
   }

   public LevelData getLevelData() {
      return this.levelData;
   }

   public OverworldGeneratorSettings getGeneratorSettings() {
      return this.generatorSettings;
   }
}
