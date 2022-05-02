package net.minecraft.world.level.levelgen;

import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class NetherGeneratorSettings extends ChunkGeneratorSettings {
   public int getBedrockFloorPosition() {
      return 0;
   }

   public int getBedrockRoofPosition() {
      return 127;
   }
}
