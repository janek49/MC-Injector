package net.minecraft.world.level.levelgen.flat;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.LakeConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.PillagerOutpostConfiguration;
import net.minecraft.world.level.levelgen.feature.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.VillageConfiguration;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.placement.LakeChanceDecoratorConfig;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatLevelGeneratorSettings extends ChunkGeneratorSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ConfiguredFeature MINESHAFT_COMPOSITE_FEATURE = Biome.makeComposite(Feature.MINESHAFT, new MineshaftConfiguration(0.004D, MineshaftFeature.Type.NORMAL), FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
   private static final ConfiguredFeature VILLAGE_COMPOSITE_FEATURE = Biome.makeComposite(Feature.VILLAGE, new VillageConfiguration("village/plains/town_centers", 6), FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
   private static final ConfiguredFeature STRONGHOLD_COMPOSITE_FEATURE = Biome.makeComposite(Feature.STRONGHOLD, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
   private static final ConfiguredFeature SWAMPHUT_COMPOSITE_FEATURE = Biome.makeComposite(Feature.SWAMP_HUT, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
   private static final ConfiguredFeature DESERT_PYRAMID_COMPOSITE_FEATURE = Biome.makeComposite(Feature.DESERT_PYRAMID, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
   private static final ConfiguredFeature JUNGLE_PYRAMID_COMPOSITE_FEATURE = Biome.makeComposite(Feature.JUNGLE_TEMPLE, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
   private static final ConfiguredFeature IGLOO_COMPOSITE_FEATURE = Biome.makeComposite(Feature.IGLOO, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
   private static final ConfiguredFeature SHIPWRECK_COMPOSITE_FEATURE = Biome.makeComposite(Feature.SHIPWRECK, new ShipwreckConfiguration(false), FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
   private static final ConfiguredFeature OCEAN_MONUMENT_COMPOSITE_FEATURE = Biome.makeComposite(Feature.OCEAN_MONUMENT, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
   private static final ConfiguredFeature WATER_LAKE_COMPOSITE_FEATURE = Biome.makeComposite(Feature.LAKE, new LakeConfiguration(Blocks.WATER.defaultBlockState()), FeatureDecorator.WATER_LAKE, new LakeChanceDecoratorConfig(4));
   private static final ConfiguredFeature LAVA_LAKE_COMPOSITE_FEATURE = Biome.makeComposite(Feature.LAKE, new LakeConfiguration(Blocks.LAVA.defaultBlockState()), FeatureDecorator.LAVA_LAKE, new LakeChanceDecoratorConfig(80));
   private static final ConfiguredFeature ENDCITY_COMPOSITE_FEATURE = Biome.makeComposite(Feature.END_CITY, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
   private static final ConfiguredFeature WOOLAND_MANSION_COMPOSITE_FEATURE = Biome.makeComposite(Feature.WOODLAND_MANSION, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
   private static final ConfiguredFeature FORTRESS_COMPOSITE_FEATURE = Biome.makeComposite(Feature.NETHER_BRIDGE, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
   private static final ConfiguredFeature OCEAN_RUIN_COMPOSITE_FEATURE = Biome.makeComposite(Feature.OCEAN_RUIN, new OceanRuinConfiguration(OceanRuinFeature.Type.COLD, 0.3F, 0.1F), FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
   private static final ConfiguredFeature PILLAGER_OUTPOST_COMPOSITE_FEATURE = Biome.makeComposite(Feature.PILLAGER_OUTPOST, new PillagerOutpostConfiguration(0.004D), FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
   public static final Map STRUCTURE_FEATURES_STEP = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put(MINESHAFT_COMPOSITE_FEATURE, GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
      hashMap.put(VILLAGE_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
      hashMap.put(STRONGHOLD_COMPOSITE_FEATURE, GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
      hashMap.put(SWAMPHUT_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
      hashMap.put(DESERT_PYRAMID_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
      hashMap.put(JUNGLE_PYRAMID_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
      hashMap.put(IGLOO_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
      hashMap.put(SHIPWRECK_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
      hashMap.put(OCEAN_RUIN_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
      hashMap.put(WATER_LAKE_COMPOSITE_FEATURE, GenerationStep.Decoration.LOCAL_MODIFICATIONS);
      hashMap.put(LAVA_LAKE_COMPOSITE_FEATURE, GenerationStep.Decoration.LOCAL_MODIFICATIONS);
      hashMap.put(ENDCITY_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
      hashMap.put(WOOLAND_MANSION_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
      hashMap.put(FORTRESS_COMPOSITE_FEATURE, GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
      hashMap.put(OCEAN_MONUMENT_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
      hashMap.put(PILLAGER_OUTPOST_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
   });
   public static final Map STRUCTURE_FEATURES = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put("mineshaft", new ConfiguredFeature[]{MINESHAFT_COMPOSITE_FEATURE});
      hashMap.put("village", new ConfiguredFeature[]{VILLAGE_COMPOSITE_FEATURE});
      hashMap.put("stronghold", new ConfiguredFeature[]{STRONGHOLD_COMPOSITE_FEATURE});
      hashMap.put("biome_1", new ConfiguredFeature[]{SWAMPHUT_COMPOSITE_FEATURE, DESERT_PYRAMID_COMPOSITE_FEATURE, JUNGLE_PYRAMID_COMPOSITE_FEATURE, IGLOO_COMPOSITE_FEATURE, OCEAN_RUIN_COMPOSITE_FEATURE, SHIPWRECK_COMPOSITE_FEATURE});
      hashMap.put("oceanmonument", new ConfiguredFeature[]{OCEAN_MONUMENT_COMPOSITE_FEATURE});
      hashMap.put("lake", new ConfiguredFeature[]{WATER_LAKE_COMPOSITE_FEATURE});
      hashMap.put("lava_lake", new ConfiguredFeature[]{LAVA_LAKE_COMPOSITE_FEATURE});
      hashMap.put("endcity", new ConfiguredFeature[]{ENDCITY_COMPOSITE_FEATURE});
      hashMap.put("mansion", new ConfiguredFeature[]{WOOLAND_MANSION_COMPOSITE_FEATURE});
      hashMap.put("fortress", new ConfiguredFeature[]{FORTRESS_COMPOSITE_FEATURE});
      hashMap.put("pillager_outpost", new ConfiguredFeature[]{PILLAGER_OUTPOST_COMPOSITE_FEATURE});
   });
   public static final Map STRUCTURE_FEATURES_DEFAULT = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put(MINESHAFT_COMPOSITE_FEATURE, new MineshaftConfiguration(0.004D, MineshaftFeature.Type.NORMAL));
      hashMap.put(VILLAGE_COMPOSITE_FEATURE, new VillageConfiguration("village/plains/town_centers", 6));
      hashMap.put(STRONGHOLD_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
      hashMap.put(SWAMPHUT_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
      hashMap.put(DESERT_PYRAMID_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
      hashMap.put(JUNGLE_PYRAMID_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
      hashMap.put(IGLOO_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
      hashMap.put(OCEAN_RUIN_COMPOSITE_FEATURE, new OceanRuinConfiguration(OceanRuinFeature.Type.COLD, 0.3F, 0.9F));
      hashMap.put(SHIPWRECK_COMPOSITE_FEATURE, new ShipwreckConfiguration(false));
      hashMap.put(OCEAN_MONUMENT_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
      hashMap.put(ENDCITY_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
      hashMap.put(WOOLAND_MANSION_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
      hashMap.put(FORTRESS_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
      hashMap.put(PILLAGER_OUTPOST_COMPOSITE_FEATURE, new PillagerOutpostConfiguration(0.004D));
   });
   private final List layersInfo = Lists.newArrayList();
   private final Map structuresOptions = Maps.newHashMap();
   private Biome biome;
   private final BlockState[] layers = new BlockState[256];
   private boolean voidGen;
   private int seaLevel;

   @Nullable
   public static Block byString(String string) {
      try {
         ResourceLocation var1 = new ResourceLocation(string);
         return (Block)Registry.BLOCK.getOptional(var1).orElse((Object)null);
      } catch (IllegalArgumentException var2) {
         LOGGER.warn("Invalid blockstate: {}", string, var2);
         return null;
      }
   }

   public Biome getBiome() {
      return this.biome;
   }

   public void setBiome(Biome biome) {
      this.biome = biome;
   }

   public Map getStructuresOptions() {
      return this.structuresOptions;
   }

   public List getLayersInfo() {
      return this.layersInfo;
   }

   public void updateLayers() {
      int var1 = 0;

      for(FlatLayerInfo var3 : this.layersInfo) {
         var3.setStart(var1);
         var1 += var3.getHeight();
      }

      this.seaLevel = 0;
      this.voidGen = true;
      var1 = 0;

      for(FlatLayerInfo var3 : this.layersInfo) {
         for(int var4 = var3.getStart(); var4 < var3.getStart() + var3.getHeight(); ++var4) {
            BlockState var5 = var3.getBlockState();
            if(var5.getBlock() != Blocks.AIR) {
               this.voidGen = false;
               this.layers[var4] = var5;
            }
         }

         if(var3.getBlockState().getBlock() == Blocks.AIR) {
            var1 += var3.getHeight();
         } else {
            this.seaLevel += var3.getHeight() + var1;
            var1 = 0;
         }
      }

   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < this.layersInfo.size(); ++var2) {
         if(var2 > 0) {
            var1.append(",");
         }

         var1.append(this.layersInfo.get(var2));
      }

      var1.append(";");
      var1.append(Registry.BIOME.getKey(this.biome));
      var1.append(";");
      if(!this.structuresOptions.isEmpty()) {
         int var2 = 0;

         for(Entry<String, Map<String, String>> var4 : this.structuresOptions.entrySet()) {
            if(var2++ > 0) {
               var1.append(",");
            }

            var1.append(((String)var4.getKey()).toLowerCase(Locale.ROOT));
            Map<String, String> var5 = (Map)var4.getValue();
            if(!var5.isEmpty()) {
               var1.append("(");
               int var6 = 0;

               for(Entry<String, String> var8 : var5.entrySet()) {
                  if(var6++ > 0) {
                     var1.append(" ");
                  }

                  var1.append((String)var8.getKey());
                  var1.append("=");
                  var1.append((String)var8.getValue());
               }

               var1.append(")");
            }
         }
      }

      return var1.toString();
   }

   @Nullable
   private static FlatLayerInfo getLayerInfoFromString(String string, int var1) {
      String[] vars2 = string.split("\\*", 2);
      int var3;
      if(vars2.length == 2) {
         try {
            var3 = Math.max(Integer.parseInt(vars2[0]), 0);
         } catch (NumberFormatException var9) {
            LOGGER.error("Error while parsing flat world string => {}", var9.getMessage());
            return null;
         }
      } else {
         var3 = 1;
      }

      int var4 = Math.min(var1 + var3, 256);
      int var5 = var4 - var1;

      Block var6;
      try {
         var6 = byString(vars2[vars2.length - 1]);
      } catch (Exception var8) {
         LOGGER.error("Error while parsing flat world string => {}", var8.getMessage());
         return null;
      }

      if(var6 == null) {
         LOGGER.error("Error while parsing flat world string => Unknown block, {}", vars2[vars2.length - 1]);
         return null;
      } else {
         FlatLayerInfo var7 = new FlatLayerInfo(var5, var6);
         var7.setStart(var1);
         return var7;
      }
   }

   private static List getLayersInfoFromString(String string) {
      List<FlatLayerInfo> list = Lists.newArrayList();
      String[] vars2 = string.split(",");
      int var3 = 0;

      for(String var7 : vars2) {
         FlatLayerInfo var8 = getLayerInfoFromString(var7, var3);
         if(var8 == null) {
            return Collections.emptyList();
         }

         list.add(var8);
         var3 += var8.getHeight();
      }

      return list;
   }

   public Dynamic toObject(DynamicOps dynamicOps) {
      T var2 = dynamicOps.createList(this.layersInfo.stream().map((flatLayerInfo) -> {
         return dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("height"), dynamicOps.createInt(flatLayerInfo.getHeight()), dynamicOps.createString("block"), dynamicOps.createString(Registry.BLOCK.getKey(flatLayerInfo.getBlockState().getBlock()).toString())));
      }));
      T var3 = dynamicOps.createMap((Map)this.structuresOptions.entrySet().stream().map((map$Entry) -> {
         return Pair.of(dynamicOps.createString(((String)map$Entry.getKey()).toLowerCase(Locale.ROOT)), dynamicOps.createMap((Map)((Map)map$Entry.getValue()).entrySet().stream().map((map$Entry) -> {
            return Pair.of(dynamicOps.createString((String)map$Entry.getKey()), dynamicOps.createString((String)map$Entry.getValue()));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("layers"), var2, dynamicOps.createString("biome"), dynamicOps.createString(Registry.BIOME.getKey(this.biome).toString()), dynamicOps.createString("structures"), var3)));
   }

   public static FlatLevelGeneratorSettings fromObject(Dynamic object) {
      FlatLevelGeneratorSettings flatLevelGeneratorSettings = (FlatLevelGeneratorSettings)ChunkGeneratorType.FLAT.createSettings();
      List<Pair<Integer, Block>> var2 = object.get("layers").asList((dynamic) -> {
         return Pair.of(Integer.valueOf(dynamic.get("height").asInt(1)), byString(dynamic.get("block").asString("")));
      });
      if(var2.stream().anyMatch((pair) -> {
         return pair.getSecond() == null;
      })) {
         return getDefault();
      } else {
         List<FlatLayerInfo> var3 = (List)var2.stream().map((pair) -> {
            return new FlatLayerInfo(((Integer)pair.getFirst()).intValue(), (Block)pair.getSecond());
         }).collect(Collectors.toList());
         if(var3.isEmpty()) {
            return getDefault();
         } else {
            flatLevelGeneratorSettings.getLayersInfo().addAll(var3);
            flatLevelGeneratorSettings.updateLayers();
            flatLevelGeneratorSettings.setBiome((Biome)Registry.BIOME.get(new ResourceLocation(object.get("biome").asString(""))));
            object.get("structures").flatMap(Dynamic::getMapValues).ifPresent((map) -> {
               map.keySet().forEach((dynamic) -> {
                  dynamic.asString().map((string) -> {
                     return (Map)flatLevelGeneratorSettings.getStructuresOptions().put(string, Maps.newHashMap());
                  });
               });
            });
            return flatLevelGeneratorSettings;
         }
      }
   }

   public static FlatLevelGeneratorSettings fromString(String string) {
      Iterator<String> var1 = Splitter.on(';').split(string).iterator();
      if(!var1.hasNext()) {
         return getDefault();
      } else {
         FlatLevelGeneratorSettings var2 = (FlatLevelGeneratorSettings)ChunkGeneratorType.FLAT.createSettings();
         List<FlatLayerInfo> var3 = getLayersInfoFromString((String)var1.next());
         if(var3.isEmpty()) {
            return getDefault();
         } else {
            var2.getLayersInfo().addAll(var3);
            var2.updateLayers();
            Biome var4 = var1.hasNext()?(Biome)Registry.BIOME.get(new ResourceLocation((String)var1.next())):null;
            var2.setBiome(var4 == null?Biomes.PLAINS:var4);
            if(var1.hasNext()) {
               String[] vars5 = ((String)var1.next()).toLowerCase(Locale.ROOT).split(",");

               for(String var9 : vars5) {
                  String[] vars10 = var9.split("\\(", 2);
                  if(!vars10[0].isEmpty()) {
                     var2.addStructure(vars10[0]);
                     if(vars10.length > 1 && vars10[1].endsWith(")") && vars10[1].length() > 1) {
                        String[] vars11 = vars10[1].substring(0, vars10[1].length() - 1).split(" ");

                        for(String var15 : vars11) {
                           String[] vars16 = var15.split("=", 2);
                           if(vars16.length == 2) {
                              var2.addStructureOption(vars10[0], vars16[0], vars16[1]);
                           }
                        }
                     }
                  }
               }
            } else {
               var2.getStructuresOptions().put("village", Maps.newHashMap());
            }

            return var2;
         }
      }
   }

   private void addStructure(String string) {
      Map<String, String> var2 = Maps.newHashMap();
      this.structuresOptions.put(string, var2);
   }

   private void addStructureOption(String var1, String var2, String var3) {
      ((Map)this.structuresOptions.get(var1)).put(var2, var3);
      if("village".equals(var1) && "distance".equals(var2)) {
         this.villagesSpacing = Mth.getInt(var3, this.villagesSpacing, 9);
      }

      if("biome_1".equals(var1) && "distance".equals(var2)) {
         this.templesSpacing = Mth.getInt(var3, this.templesSpacing, 9);
      }

      if("stronghold".equals(var1)) {
         if("distance".equals(var2)) {
            this.strongholdsDistance = Mth.getInt(var3, this.strongholdsDistance, 1);
         } else if("count".equals(var2)) {
            this.strongholdsCount = Mth.getInt(var3, this.strongholdsCount, 1);
         } else if("spread".equals(var2)) {
            this.strongholdsSpread = Mth.getInt(var3, this.strongholdsSpread, 1);
         }
      }

      if("oceanmonument".equals(var1)) {
         if("separation".equals(var2)) {
            this.monumentsSeparation = Mth.getInt(var3, this.monumentsSeparation, 1);
         } else if("spacing".equals(var2)) {
            this.monumentsSpacing = Mth.getInt(var3, this.monumentsSpacing, 1);
         }
      }

      if("endcity".equals(var1) && "distance".equals(var2)) {
         this.endCitySpacing = Mth.getInt(var3, this.endCitySpacing, 1);
      }

      if("mansion".equals(var1) && "distance".equals(var2)) {
         this.woodlandMansionSpacing = Mth.getInt(var3, this.woodlandMansionSpacing, 1);
      }

   }

   public static FlatLevelGeneratorSettings getDefault() {
      FlatLevelGeneratorSettings flatLevelGeneratorSettings = (FlatLevelGeneratorSettings)ChunkGeneratorType.FLAT.createSettings();
      flatLevelGeneratorSettings.setBiome(Biomes.PLAINS);
      flatLevelGeneratorSettings.getLayersInfo().add(new FlatLayerInfo(1, Blocks.BEDROCK));
      flatLevelGeneratorSettings.getLayersInfo().add(new FlatLayerInfo(2, Blocks.DIRT));
      flatLevelGeneratorSettings.getLayersInfo().add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
      flatLevelGeneratorSettings.updateLayers();
      flatLevelGeneratorSettings.getStructuresOptions().put("village", Maps.newHashMap());
      return flatLevelGeneratorSettings;
   }

   public boolean isVoidGen() {
      return this.voidGen;
   }

   public BlockState[] getLayers() {
      return this.layers;
   }

   public void deleteLayer(int i) {
      this.layers[i] = null;
   }
}
