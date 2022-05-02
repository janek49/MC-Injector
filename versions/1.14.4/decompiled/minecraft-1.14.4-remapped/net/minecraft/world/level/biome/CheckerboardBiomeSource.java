package net.minecraft.world.level.biome;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.CheckerboardBiomeSourceSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class CheckerboardBiomeSource extends BiomeSource {
   private final Biome[] allowedBiomes;
   private final int bitShift;

   public CheckerboardBiomeSource(CheckerboardBiomeSourceSettings checkerboardBiomeSourceSettings) {
      this.allowedBiomes = checkerboardBiomeSourceSettings.getAllowedBiomes();
      this.bitShift = checkerboardBiomeSourceSettings.getSize() + 4;
   }

   public Biome getBiome(int var1, int var2) {
      return this.allowedBiomes[Math.abs(((var1 >> this.bitShift) + (var2 >> this.bitShift)) % this.allowedBiomes.length)];
   }

   public Biome[] getBiomeBlock(int var1, int var2, int var3, int var4, boolean var5) {
      Biome[] biomes = new Biome[var3 * var4];

      for(int var7 = 0; var7 < var4; ++var7) {
         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = Math.abs(((var1 + var7 >> this.bitShift) + (var2 + var8 >> this.bitShift)) % this.allowedBiomes.length);
            Biome var10 = this.allowedBiomes[var9];
            biomes[var7 * var3 + var8] = var10;
         }
      }

      return biomes;
   }

   @Nullable
   public BlockPos findBiome(int var1, int var2, int var3, List list, Random random) {
      return null;
   }

   public boolean canGenerateStructure(StructureFeature structureFeature) {
      return ((Boolean)this.supportedStructures.computeIfAbsent(structureFeature, (structureFeature) -> {
         for(Biome var5 : this.allowedBiomes) {
            if(var5.isValidStart(structureFeature)) {
               return Boolean.valueOf(true);
            }
         }

         return Boolean.valueOf(false);
      })).booleanValue();
   }

   public Set getSurfaceBlocks() {
      if(this.surfaceBlocks.isEmpty()) {
         for(Biome var4 : this.allowedBiomes) {
            this.surfaceBlocks.add(var4.getSurfaceBuilderConfig().getTopMaterial());
         }
      }

      return this.surfaceBlocks;
   }

   public Set getBiomesWithin(int var1, int var2, int var3) {
      return Sets.newHashSet(this.allowedBiomes);
   }
}
