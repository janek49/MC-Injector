package net.minecraft.world.level.levelgen.carver;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

public class ConfiguredWorldCarver {
   public final WorldCarver worldCarver;
   public final CarverConfiguration config;

   public ConfiguredWorldCarver(WorldCarver worldCarver, CarverConfiguration config) {
      this.worldCarver = worldCarver;
      this.config = config;
   }

   public boolean isStartChunk(Random random, int var2, int var3) {
      return this.worldCarver.isStartChunk(random, var2, var3, this.config);
   }

   public boolean carve(ChunkAccess chunkAccess, Random random, int var3, int var4, int var5, int var6, int var7, BitSet bitSet) {
      return this.worldCarver.carve(chunkAccess, random, var3, var4, var5, var6, var7, bitSet, this.config);
   }
}
