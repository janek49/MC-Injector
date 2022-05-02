package net.minecraft.world.level.levelgen;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.LayerConfiguration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;

public class FlatLevelSource extends ChunkGenerator {
   private final Biome biomeWrapper = this.getBiomeFromSettings();
   private final PhantomSpawner phantomSpawner = new PhantomSpawner();
   private final CatSpawner catSpawner = new CatSpawner();

   public FlatLevelSource(LevelAccessor levelAccessor, BiomeSource biomeSource, FlatLevelGeneratorSettings flatLevelGeneratorSettings) {
      super(levelAccessor, biomeSource, flatLevelGeneratorSettings);
   }

   private Biome getBiomeFromSettings() {
      Biome biome = ((FlatLevelGeneratorSettings)this.settings).getBiome();
      FlatLevelSource.FlatLevelBiomeWrapper var2 = new FlatLevelSource.FlatLevelBiomeWrapper(biome.getSurfaceBuilder(), biome.getPrecipitation(), biome.getBiomeCategory(), biome.getDepth(), biome.getScale(), biome.getTemperature(), biome.getDownfall(), biome.getWaterColor(), biome.getWaterFogColor(), biome.getParent());
      Map<String, Map<String, String>> var3 = ((FlatLevelGeneratorSettings)this.settings).getStructuresOptions();

      for(String var5 : var3.keySet()) {
         ConfiguredFeature<?>[] vars6 = (ConfiguredFeature[])FlatLevelGeneratorSettings.STRUCTURE_FEATURES.get(var5);
         if(vars6 != null) {
            for(ConfiguredFeature<?> var10 : vars6) {
               var2.addFeature((GenerationStep.Decoration)FlatLevelGeneratorSettings.STRUCTURE_FEATURES_STEP.get(var10), var10);
               ConfiguredFeature<?> var11 = ((DecoratedFeatureConfiguration)var10.config).feature;
               if(var11.feature instanceof StructureFeature) {
                  StructureFeature<FeatureConfiguration> var12 = (StructureFeature)var11.feature;
                  FeatureConfiguration var13 = biome.getStructureConfiguration(var12);
                  var2.addStructureStart(var12, var13 != null?var13:(FeatureConfiguration)FlatLevelGeneratorSettings.STRUCTURE_FEATURES_DEFAULT.get(var10));
               }
            }
         }
      }

      boolean var4 = (!((FlatLevelGeneratorSettings)this.settings).isVoidGen() || biome == Biomes.THE_VOID) && var3.containsKey("decoration");
      if(var4) {
         List<GenerationStep.Decoration> var5 = Lists.newArrayList();
         var5.add(GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
         var5.add(GenerationStep.Decoration.SURFACE_STRUCTURES);

         for(GenerationStep.Decoration var9 : GenerationStep.Decoration.values()) {
            if(!var5.contains(var9)) {
               for(ConfiguredFeature<?> var11 : biome.getFeaturesForStep(var9)) {
                  var2.addFeature(var9, var11);
               }
            }
         }
      }

      BlockState[] vars5 = ((FlatLevelGeneratorSettings)this.settings).getLayers();

      for(int var6 = 0; var6 < vars5.length; ++var6) {
         BlockState var7 = vars5[var6];
         if(var7 != null && !Heightmap.Types.MOTION_BLOCKING.isOpaque().test(var7)) {
            ((FlatLevelGeneratorSettings)this.settings).deleteLayer(var6);
            var2.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, Biome.makeComposite(Feature.FILL_LAYER, new LayerConfiguration(var6, var7), FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
         }
      }

      return var2;
   }

   public void buildSurfaceAndBedrock(ChunkAccess chunkAccess) {
   }

   public int getSpawnHeight() {
      ChunkAccess var1 = this.level.getChunk(0, 0);
      return var1.getHeight(Heightmap.Types.MOTION_BLOCKING, 8, 8);
   }

   protected Biome getCarvingBiome(ChunkAccess chunkAccess) {
      return this.biomeWrapper;
   }

   protected Biome getDecorationBiome(WorldGenRegion worldGenRegion, BlockPos blockPos) {
      return this.biomeWrapper;
   }

   public void fillFromNoise(LevelAccessor levelAccessor, ChunkAccess chunkAccess) {
      BlockState[] vars3 = ((FlatLevelGeneratorSettings)this.settings).getLayers();
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();
      Heightmap var5 = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
      Heightmap var6 = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

      for(int var7 = 0; var7 < vars3.length; ++var7) {
         BlockState var8 = vars3[var7];
         if(var8 != null) {
            for(int var9 = 0; var9 < 16; ++var9) {
               for(int var10 = 0; var10 < 16; ++var10) {
                  chunkAccess.setBlockState(var4.set(var9, var7, var10), var8, false);
                  var5.update(var9, var7, var10, var8);
                  var6.update(var9, var7, var10, var8);
               }
            }
         }
      }

   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types heightmap$Types) {
      BlockState[] vars4 = ((FlatLevelGeneratorSettings)this.settings).getLayers();

      for(int var5 = vars4.length - 1; var5 >= 0; --var5) {
         BlockState var6 = vars4[var5];
         if(var6 != null && heightmap$Types.isOpaque().test(var6)) {
            return var5 + 1;
         }
      }

      return 0;
   }

   public void tickCustomSpawners(ServerLevel serverLevel, boolean var2, boolean var3) {
      this.phantomSpawner.tick(serverLevel, var2, var3);
      this.catSpawner.tick(serverLevel, var2, var3);
   }

   public boolean isBiomeValidStartForStructure(Biome biome, StructureFeature structureFeature) {
      return this.biomeWrapper.isValidStart(structureFeature);
   }

   @Nullable
   public FeatureConfiguration getStructureConfiguration(Biome biome, StructureFeature structureFeature) {
      return this.biomeWrapper.getStructureConfiguration(structureFeature);
   }

   @Nullable
   public BlockPos findNearestMapFeature(Level level, String string, BlockPos var3, int var4, boolean var5) {
      return !((FlatLevelGeneratorSettings)this.settings).getStructuresOptions().keySet().contains(string.toLowerCase(Locale.ROOT))?null:super.findNearestMapFeature(level, string, var3, var4, var5);
   }

   class FlatLevelBiomeWrapper extends Biome {
      protected FlatLevelBiomeWrapper(ConfiguredSurfaceBuilder configuredSurfaceBuilder, Biome.Precipitation biome$Precipitation, Biome.BiomeCategory biome$BiomeCategory, float var5, float var6, float var7, float var8, int var9, int var10, String string) {
         super((new Biome.BiomeBuilder()).surfaceBuilder(configuredSurfaceBuilder).precipitation(biome$Precipitation).biomeCategory(biome$BiomeCategory).depth(var5).scale(var6).temperature(var7).downfall(var8).waterColor(var9).waterFogColor(var10).parent(string));
      }
   }
}
