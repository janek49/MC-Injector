package net.minecraft.world.level.biome;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSourceSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class FixedBiomeSource extends BiomeSource {
   private final Biome biome;

   public FixedBiomeSource(FixedBiomeSourceSettings fixedBiomeSourceSettings) {
      this.biome = fixedBiomeSourceSettings.getBiome();
   }

   public Biome getBiome(int var1, int var2) {
      return this.biome;
   }

   public Biome[] getBiomeBlock(int var1, int var2, int var3, int var4, boolean var5) {
      Biome[] biomes = new Biome[var3 * var4];
      Arrays.fill(biomes, 0, var3 * var4, this.biome);
      return biomes;
   }

   @Nullable
   public BlockPos findBiome(int var1, int var2, int var3, List list, Random random) {
      return list.contains(this.biome)?new BlockPos(var1 - var3 + random.nextInt(var3 * 2 + 1), 0, var2 - var3 + random.nextInt(var3 * 2 + 1)):null;
   }

   public boolean canGenerateStructure(StructureFeature structureFeature) {
      Map var10000 = this.supportedStructures;
      Biome var10002 = this.biome;
      this.biome.getClass();
      return ((Boolean)var10000.computeIfAbsent(structureFeature, var10002::isValidStart)).booleanValue();
   }

   public Set getSurfaceBlocks() {
      if(this.surfaceBlocks.isEmpty()) {
         this.surfaceBlocks.add(this.biome.getSurfaceBuilderConfig().getTopMaterial());
      }

      return this.surfaceBlocks;
   }

   public Set getBiomesWithin(int var1, int var2, int var3) {
      return Sets.newHashSet(new Biome[]{this.biome});
   }
}
