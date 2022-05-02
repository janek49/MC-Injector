package net.minecraft.world.level.newbiome.layer;

import com.google.common.collect.ImmutableList;
import java.util.function.LongFunction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.area.LazyArea;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.context.LazyAreaContext;
import net.minecraft.world.level.newbiome.layer.AddDeepOceanLayer;
import net.minecraft.world.level.newbiome.layer.AddEdgeLayer;
import net.minecraft.world.level.newbiome.layer.AddIslandLayer;
import net.minecraft.world.level.newbiome.layer.AddMushroomIslandLayer;
import net.minecraft.world.level.newbiome.layer.AddSnowLayer;
import net.minecraft.world.level.newbiome.layer.BiomeEdgeLayer;
import net.minecraft.world.level.newbiome.layer.BiomeInitLayer;
import net.minecraft.world.level.newbiome.layer.IslandLayer;
import net.minecraft.world.level.newbiome.layer.Layer;
import net.minecraft.world.level.newbiome.layer.OceanLayer;
import net.minecraft.world.level.newbiome.layer.OceanMixerLayer;
import net.minecraft.world.level.newbiome.layer.RareBiomeLargeLayer;
import net.minecraft.world.level.newbiome.layer.RareBiomeSpotLayer;
import net.minecraft.world.level.newbiome.layer.RegionHillsLayer;
import net.minecraft.world.level.newbiome.layer.RemoveTooMuchOceanLayer;
import net.minecraft.world.level.newbiome.layer.RiverInitLayer;
import net.minecraft.world.level.newbiome.layer.RiverLayer;
import net.minecraft.world.level.newbiome.layer.RiverMixerLayer;
import net.minecraft.world.level.newbiome.layer.ShoreLayer;
import net.minecraft.world.level.newbiome.layer.SmoothLayer;
import net.minecraft.world.level.newbiome.layer.VoronoiZoom;
import net.minecraft.world.level.newbiome.layer.ZoomLayer;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;

public class Layers {
   protected static final int WARM_OCEAN = Registry.BIOME.getId(Biomes.WARM_OCEAN);
   protected static final int LUKEWARM_OCEAN = Registry.BIOME.getId(Biomes.LUKEWARM_OCEAN);
   protected static final int OCEAN = Registry.BIOME.getId(Biomes.OCEAN);
   protected static final int COLD_OCEAN = Registry.BIOME.getId(Biomes.COLD_OCEAN);
   protected static final int FROZEN_OCEAN = Registry.BIOME.getId(Biomes.FROZEN_OCEAN);
   protected static final int DEEP_WARM_OCEAN = Registry.BIOME.getId(Biomes.DEEP_WARM_OCEAN);
   protected static final int DEEP_LUKEWARM_OCEAN = Registry.BIOME.getId(Biomes.DEEP_LUKEWARM_OCEAN);
   protected static final int DEEP_OCEAN = Registry.BIOME.getId(Biomes.DEEP_OCEAN);
   protected static final int DEEP_COLD_OCEAN = Registry.BIOME.getId(Biomes.DEEP_COLD_OCEAN);
   protected static final int DEEP_FROZEN_OCEAN = Registry.BIOME.getId(Biomes.DEEP_FROZEN_OCEAN);

   private static AreaFactory zoom(long var0, AreaTransformer1 areaTransformer1, AreaFactory var3, int var4, LongFunction longFunction) {
      AreaFactory<T> var6 = var3;

      for(int var7 = 0; var7 < var4; ++var7) {
         var6 = areaTransformer1.run((BigContext)longFunction.apply(var0 + (long)var7), var6);
      }

      return var6;
   }

   public static ImmutableList getDefaultLayers(LevelType levelType, OverworldGeneratorSettings overworldGeneratorSettings, LongFunction longFunction) {
      AreaFactory<T> var3 = IslandLayer.INSTANCE.run((BigContext)longFunction.apply(1L));
      var3 = ZoomLayer.FUZZY.run((BigContext)longFunction.apply(2000L), var3);
      var3 = AddIslandLayer.INSTANCE.run((BigContext)longFunction.apply(1L), var3);
      var3 = ZoomLayer.NORMAL.run((BigContext)longFunction.apply(2001L), var3);
      var3 = AddIslandLayer.INSTANCE.run((BigContext)longFunction.apply(2L), var3);
      var3 = AddIslandLayer.INSTANCE.run((BigContext)longFunction.apply(50L), var3);
      var3 = AddIslandLayer.INSTANCE.run((BigContext)longFunction.apply(70L), var3);
      var3 = RemoveTooMuchOceanLayer.INSTANCE.run((BigContext)longFunction.apply(2L), var3);
      AreaFactory<T> var4 = OceanLayer.INSTANCE.run((BigContext)longFunction.apply(2L));
      var4 = zoom(2001L, ZoomLayer.NORMAL, var4, 6, longFunction);
      var3 = AddSnowLayer.INSTANCE.run((BigContext)longFunction.apply(2L), var3);
      var3 = AddIslandLayer.INSTANCE.run((BigContext)longFunction.apply(3L), var3);
      var3 = AddEdgeLayer.CoolWarm.INSTANCE.run((BigContext)longFunction.apply(2L), var3);
      var3 = AddEdgeLayer.HeatIce.INSTANCE.run((BigContext)longFunction.apply(2L), var3);
      var3 = AddEdgeLayer.IntroduceSpecial.INSTANCE.run((BigContext)longFunction.apply(3L), var3);
      var3 = ZoomLayer.NORMAL.run((BigContext)longFunction.apply(2002L), var3);
      var3 = ZoomLayer.NORMAL.run((BigContext)longFunction.apply(2003L), var3);
      var3 = AddIslandLayer.INSTANCE.run((BigContext)longFunction.apply(4L), var3);
      var3 = AddMushroomIslandLayer.INSTANCE.run((BigContext)longFunction.apply(5L), var3);
      var3 = AddDeepOceanLayer.INSTANCE.run((BigContext)longFunction.apply(4L), var3);
      var3 = zoom(1000L, ZoomLayer.NORMAL, var3, 0, longFunction);
      int var5 = 4;
      int var6 = var5;
      if(overworldGeneratorSettings != null) {
         var5 = overworldGeneratorSettings.getBiomeSize();
         var6 = overworldGeneratorSettings.getRiverSize();
      }

      if(levelType == LevelType.LARGE_BIOMES) {
         var5 = 6;
      }

      AreaFactory var7 = zoom(1000L, ZoomLayer.NORMAL, var3, 0, longFunction);
      var7 = RiverInitLayer.INSTANCE.run((BigContext)longFunction.apply(100L), var7);
      AreaFactory var8 = (new BiomeInitLayer(levelType, overworldGeneratorSettings)).run((BigContext)longFunction.apply(200L), var3);
      var8 = RareBiomeLargeLayer.INSTANCE.run((BigContext)longFunction.apply(1001L), var8);
      var8 = zoom(1000L, ZoomLayer.NORMAL, var8, 2, longFunction);
      var8 = BiomeEdgeLayer.INSTANCE.run((BigContext)longFunction.apply(1000L), var8);
      AreaFactory var9 = zoom(1000L, ZoomLayer.NORMAL, var7, 2, longFunction);
      var8 = RegionHillsLayer.INSTANCE.run((BigContext)longFunction.apply(1000L), var8, var9);
      var7 = zoom(1000L, ZoomLayer.NORMAL, var7, 2, longFunction);
      var7 = zoom(1000L, ZoomLayer.NORMAL, var7, var6, longFunction);
      var7 = RiverLayer.INSTANCE.run((BigContext)longFunction.apply(1L), var7);
      var7 = SmoothLayer.INSTANCE.run((BigContext)longFunction.apply(1000L), var7);
      var8 = RareBiomeSpotLayer.INSTANCE.run((BigContext)longFunction.apply(1001L), var8);

      for(int var10 = 0; var10 < var5; ++var10) {
         var8 = ZoomLayer.NORMAL.run((BigContext)longFunction.apply((long)(1000 + var10)), var8);
         if(var10 == 0) {
            var8 = AddIslandLayer.INSTANCE.run((BigContext)longFunction.apply(3L), var8);
         }

         if(var10 == 1 || var5 == 1) {
            var8 = ShoreLayer.INSTANCE.run((BigContext)longFunction.apply(1000L), var8);
         }
      }

      var8 = SmoothLayer.INSTANCE.run((BigContext)longFunction.apply(1000L), var8);
      var8 = RiverMixerLayer.INSTANCE.run((BigContext)longFunction.apply(100L), var8, var7);
      var8 = OceanMixerLayer.INSTANCE.run((BigContext)longFunction.apply(100L), var8, var4);
      AreaFactory<T> var11 = VoronoiZoom.INSTANCE.run((BigContext)longFunction.apply(10L), var8);
      return ImmutableList.of(var8, var11, var8);
   }

   public static Layer[] getDefaultLayers(long var0, LevelType levelType, OverworldGeneratorSettings overworldGeneratorSettings) {
      int var4 = 25;
      ImmutableList<AreaFactory<LazyArea>> var5 = getDefaultLayers(levelType, overworldGeneratorSettings, (var2) -> {
         return new LazyAreaContext(25, var0, var2);
      });
      Layer var6 = new Layer((AreaFactory)var5.get(0));
      Layer var7 = new Layer((AreaFactory)var5.get(1));
      Layer var8 = new Layer((AreaFactory)var5.get(2));
      return new Layer[]{var6, var7, var8};
   }

   public static boolean isSame(int var0, int var1) {
      if(var0 == var1) {
         return true;
      } else {
         Biome var2 = (Biome)Registry.BIOME.byId(var0);
         Biome var3 = (Biome)Registry.BIOME.byId(var1);
         return var2 != null && var3 != null?(var2 != Biomes.WOODED_BADLANDS_PLATEAU && var2 != Biomes.BADLANDS_PLATEAU?(var2.getBiomeCategory() != Biome.BiomeCategory.NONE && var3.getBiomeCategory() != Biome.BiomeCategory.NONE && var2.getBiomeCategory() == var3.getBiomeCategory()?true:var2 == var3):var3 == Biomes.WOODED_BADLANDS_PLATEAU || var3 == Biomes.BADLANDS_PLATEAU):false;
      }
   }

   protected static boolean isOcean(int i) {
      return i == WARM_OCEAN || i == LUKEWARM_OCEAN || i == OCEAN || i == COLD_OCEAN || i == FROZEN_OCEAN || i == DEEP_WARM_OCEAN || i == DEEP_LUKEWARM_OCEAN || i == DEEP_OCEAN || i == DEEP_COLD_OCEAN || i == DEEP_FROZEN_OCEAN;
   }

   protected static boolean isShallowOcean(int i) {
      return i == WARM_OCEAN || i == LUKEWARM_OCEAN || i == OCEAN || i == COLD_OCEAN || i == FROZEN_OCEAN;
   }
}
