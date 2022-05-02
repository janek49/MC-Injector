package net.minecraft.world.level.levelgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class TheEndGeneratorSettings extends ChunkGeneratorSettings {
   private BlockPos spawnPosition;

   public TheEndGeneratorSettings setSpawnPosition(BlockPos spawnPosition) {
      this.spawnPosition = spawnPosition;
      return this;
   }

   public BlockPos getSpawnPosition() {
      return this.spawnPosition;
   }
}
