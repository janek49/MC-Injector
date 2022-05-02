package net.minecraft.world.level.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.WeighedRandom;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.FlowerFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Biome {
   public static final Logger LOGGER = LogManager.getLogger();
   public static final Set EXPLORABLE_BIOMES = Sets.newHashSet();
   public static final IdMapper MUTATED_BIOMES = new IdMapper();
   protected static final PerlinSimplexNoise TEMPERATURE_NOISE = new PerlinSimplexNoise(new Random(1234L), 1);
   public static final PerlinSimplexNoise BIOME_INFO_NOISE = new PerlinSimplexNoise(new Random(2345L), 1);
   @Nullable
   protected String descriptionId;
   protected final float depth;
   protected final float scale;
   protected final float temperature;
   protected final float downfall;
   protected final int waterColor;
   protected final int waterFogColor;
   @Nullable
   protected final String parent;
   protected final ConfiguredSurfaceBuilder surfaceBuilder;
   protected final Biome.BiomeCategory biomeCategory;
   protected final Biome.Precipitation precipitation;
   protected final Map carvers = Maps.newHashMap();
   protected final Map features = Maps.newHashMap();
   protected final List flowerFeatures = Lists.newArrayList();
   protected final Map validFeatureStarts = Maps.newHashMap();
   private final Map spawners = Maps.newHashMap();
   private final ThreadLocal temperatureCache = ThreadLocal.withInitial(() -> {
      return (Long2FloatLinkedOpenHashMap)Util.make(() -> {
         Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
            protected void rehash(int i) {
            }
         };
         long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
         return long2FloatLinkedOpenHashMap;
      });
   });

   @Nullable
   public static Biome getMutatedVariant(Biome biome) {
      return (Biome)MUTATED_BIOMES.byId(Registry.BIOME.getId(biome));
   }

   public static ConfiguredWorldCarver makeCarver(WorldCarver worldCarver, CarverConfiguration carverConfiguration) {
      return new ConfiguredWorldCarver(worldCarver, carverConfiguration);
   }

   public static ConfiguredFeature makeComposite(Feature feature, FeatureConfiguration featureConfiguration, FeatureDecorator featureDecorator, DecoratorConfiguration decoratorConfiguration) {
      Feature<DecoratedFeatureConfiguration> feature = feature instanceof FlowerFeature?Feature.DECORATED_FLOWER:Feature.DECORATED;
      return new ConfiguredFeature(feature, new DecoratedFeatureConfiguration(feature, featureConfiguration, featureDecorator, decoratorConfiguration));
   }

   protected Biome(Biome.BiomeBuilder biome$BiomeBuilder) {
      if(biome$BiomeBuilder.surfaceBuilder != null && biome$BiomeBuilder.precipitation != null && biome$BiomeBuilder.biomeCategory != null && biome$BiomeBuilder.depth != null && biome$BiomeBuilder.scale != null && biome$BiomeBuilder.temperature != null && biome$BiomeBuilder.downfall != null && biome$BiomeBuilder.waterColor != null && biome$BiomeBuilder.waterFogColor != null) {
         this.surfaceBuilder = biome$BiomeBuilder.surfaceBuilder;
         this.precipitation = biome$BiomeBuilder.precipitation;
         this.biomeCategory = biome$BiomeBuilder.biomeCategory;
         this.depth = biome$BiomeBuilder.depth.floatValue();
         this.scale = biome$BiomeBuilder.scale.floatValue();
         this.temperature = biome$BiomeBuilder.temperature.floatValue();
         this.downfall = biome$BiomeBuilder.downfall.floatValue();
         this.waterColor = biome$BiomeBuilder.waterColor.intValue();
         this.waterFogColor = biome$BiomeBuilder.waterFogColor.intValue();
         this.parent = biome$BiomeBuilder.parent;

         for(GenerationStep.Decoration var5 : GenerationStep.Decoration.values()) {
            this.features.put(var5, Lists.newArrayList());
         }

         for(MobCategory var5 : MobCategory.values()) {
            this.spawners.put(var5, Lists.newArrayList());
         }

      } else {
         throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + biome$BiomeBuilder);
      }
   }

   public boolean isMutated() {
      return this.parent != null;
   }

   public int getSkyColor(float f) {
      f = f / 3.0F;
      f = Mth.clamp(f, -1.0F, 1.0F);
      return Mth.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
   }

   protected void addSpawn(MobCategory mobCategory, Biome.SpawnerData biome$SpawnerData) {
      ((List)this.spawners.get(mobCategory)).add(biome$SpawnerData);
   }

   public List getMobs(MobCategory mobCategory) {
      return (List)this.spawners.get(mobCategory);
   }

   public Biome.Precipitation getPrecipitation() {
      return this.precipitation;
   }

   public boolean isHumid() {
      return this.getDownfall() > 0.85F;
   }

   public float getCreatureProbability() {
      return 0.1F;
   }

   protected float getTemperatureNoCache(BlockPos blockPos) {
      if(blockPos.getY() > 64) {
         float var2 = (float)(TEMPERATURE_NOISE.getValue((double)((float)blockPos.getX() / 8.0F), (double)((float)blockPos.getZ() / 8.0F)) * 4.0D);
         return this.getTemperature() - (var2 + (float)blockPos.getY() - 64.0F) * 0.05F / 30.0F;
      } else {
         return this.getTemperature();
      }
   }

   public final float getTemperature(BlockPos blockPos) {
      long var2 = blockPos.asLong();
      Long2FloatLinkedOpenHashMap var4 = (Long2FloatLinkedOpenHashMap)this.temperatureCache.get();
      float var5 = var4.get(var2);
      if(!Float.isNaN(var5)) {
         return var5;
      } else {
         float var6 = this.getTemperatureNoCache(blockPos);
         if(var4.size() == 1024) {
            var4.removeFirstFloat();
         }

         var4.put(var2, var6);
         return var6;
      }
   }

   public boolean shouldFreeze(LevelReader levelReader, BlockPos blockPos) {
      return this.shouldFreeze(levelReader, blockPos, true);
   }

   public boolean shouldFreeze(LevelReader levelReader, BlockPos blockPos, boolean var3) {
      if(this.getTemperature(blockPos) >= 0.15F) {
         return false;
      } else {
         if(blockPos.getY() >= 0 && blockPos.getY() < 256 && levelReader.getBrightness(LightLayer.BLOCK, blockPos) < 10) {
            BlockState var4 = levelReader.getBlockState(blockPos);
            FluidState var5 = levelReader.getFluidState(blockPos);
            if(var5.getType() == Fluids.WATER && var4.getBlock() instanceof LiquidBlock) {
               if(!var3) {
                  return true;
               }

               boolean var6 = levelReader.isWaterAt(blockPos.west()) && levelReader.isWaterAt(blockPos.east()) && levelReader.isWaterAt(blockPos.north()) && levelReader.isWaterAt(blockPos.south());
               if(!var6) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean shouldSnow(LevelReader levelReader, BlockPos blockPos) {
      if(this.getTemperature(blockPos) >= 0.15F) {
         return false;
      } else {
         if(blockPos.getY() >= 0 && blockPos.getY() < 256 && levelReader.getBrightness(LightLayer.BLOCK, blockPos) < 10) {
            BlockState var3 = levelReader.getBlockState(blockPos);
            if(var3.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(levelReader, blockPos)) {
               return true;
            }
         }

         return false;
      }
   }

   public void addFeature(GenerationStep.Decoration generationStep$Decoration, ConfiguredFeature configuredFeature) {
      if(configuredFeature.feature == Feature.DECORATED_FLOWER) {
         this.flowerFeatures.add(configuredFeature);
      }

      ((List)this.features.get(generationStep$Decoration)).add(configuredFeature);
   }

   public void addCarver(GenerationStep.Carving generationStep$Carving, ConfiguredWorldCarver configuredWorldCarver) {
      ((List)this.carvers.computeIfAbsent(generationStep$Carving, (generationStep$Carving) -> {
         return Lists.newArrayList();
      })).add(configuredWorldCarver);
   }

   public List getCarvers(GenerationStep.Carving generationStep$Carving) {
      return (List)this.carvers.computeIfAbsent(generationStep$Carving, (generationStep$Carving) -> {
         return Lists.newArrayList();
      });
   }

   public void addStructureStart(StructureFeature structureFeature, FeatureConfiguration featureConfiguration) {
      this.validFeatureStarts.put(structureFeature, featureConfiguration);
   }

   public boolean isValidStart(StructureFeature structureFeature) {
      return this.validFeatureStarts.containsKey(structureFeature);
   }

   @Nullable
   public FeatureConfiguration getStructureConfiguration(StructureFeature structureFeature) {
      return (FeatureConfiguration)this.validFeatureStarts.get(structureFeature);
   }

   public List getFlowerFeatures() {
      return this.flowerFeatures;
   }

   public List getFeaturesForStep(GenerationStep.Decoration generationStep$Decoration) {
      return (List)this.features.get(generationStep$Decoration);
   }

   public void generate(GenerationStep.Decoration generationStep$Decoration, ChunkGenerator chunkGenerator, LevelAccessor levelAccessor, long var4, WorldgenRandom worldgenRandom, BlockPos blockPos) {
      int var8 = 0;

      for(ConfiguredFeature<?> var10 : (List)this.features.get(generationStep$Decoration)) {
         worldgenRandom.setFeatureSeed(var4, var8, generationStep$Decoration.ordinal());

         try {
            var10.place(levelAccessor, chunkGenerator, worldgenRandom, blockPos);
         } catch (Exception var13) {
            CrashReport var12 = CrashReport.forThrowable(var13, "Feature placement");
            CrashReportCategory var10000 = var12.addCategory("Feature").setDetail("Id", (Object)Registry.FEATURE.getKey(var10.feature));
            Feature var10002 = var10.feature;
            var10.feature.getClass();
            var10000.setDetail("Description", var10002::toString);
            throw new ReportedException(var12);
         }

         ++var8;
      }

   }

   public int getGrassColor(BlockPos blockPos) {
      double var2 = (double)Mth.clamp(this.getTemperature(blockPos), 0.0F, 1.0F);
      double var4 = (double)Mth.clamp(this.getDownfall(), 0.0F, 1.0F);
      return GrassColor.get(var2, var4);
   }

   public int getFoliageColor(BlockPos blockPos) {
      double var2 = (double)Mth.clamp(this.getTemperature(blockPos), 0.0F, 1.0F);
      double var4 = (double)Mth.clamp(this.getDownfall(), 0.0F, 1.0F);
      return FoliageColor.get(var2, var4);
   }

   public void buildSurfaceAt(Random random, ChunkAccess chunkAccess, int var3, int var4, int var5, double var6, BlockState var8, BlockState var9, int var10, long var11) {
      this.surfaceBuilder.initNoise(var11);
      this.surfaceBuilder.apply(random, chunkAccess, this, var3, var4, var5, var6, var8, var9, var10, var11);
   }

   public Biome.BiomeTempCategory getTemperatureCategory() {
      return this.biomeCategory == Biome.BiomeCategory.OCEAN?Biome.BiomeTempCategory.OCEAN:((double)this.getTemperature() < 0.2D?Biome.BiomeTempCategory.COLD:((double)this.getTemperature() < 1.0D?Biome.BiomeTempCategory.MEDIUM:Biome.BiomeTempCategory.WARM));
   }

   public final float getDepth() {
      return this.depth;
   }

   public final float getDownfall() {
      return this.downfall;
   }

   public Component getName() {
      return new TranslatableComponent(this.getDescriptionId(), new Object[0]);
   }

   public String getDescriptionId() {
      if(this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("biome", Registry.BIOME.getKey(this));
      }

      return this.descriptionId;
   }

   public final float getScale() {
      return this.scale;
   }

   public final float getTemperature() {
      return this.temperature;
   }

   public final int getWaterColor() {
      return this.waterColor;
   }

   public final int getWaterFogColor() {
      return this.waterFogColor;
   }

   public final Biome.BiomeCategory getBiomeCategory() {
      return this.biomeCategory;
   }

   public ConfiguredSurfaceBuilder getSurfaceBuilder() {
      return this.surfaceBuilder;
   }

   public SurfaceBuilderConfiguration getSurfaceBuilderConfig() {
      return this.surfaceBuilder.getSurfaceBuilderConfiguration();
   }

   @Nullable
   public String getParent() {
      return this.parent;
   }

   public static class BiomeBuilder {
      @Nullable
      private ConfiguredSurfaceBuilder surfaceBuilder;
      @Nullable
      private Biome.Precipitation precipitation;
      @Nullable
      private Biome.BiomeCategory biomeCategory;
      @Nullable
      private Float depth;
      @Nullable
      private Float scale;
      @Nullable
      private Float temperature;
      @Nullable
      private Float downfall;
      @Nullable
      private Integer waterColor;
      @Nullable
      private Integer waterFogColor;
      @Nullable
      private String parent;

      public Biome.BiomeBuilder surfaceBuilder(SurfaceBuilder surfaceBuilder, SurfaceBuilderConfiguration surfaceBuilderConfiguration) {
         this.surfaceBuilder = new ConfiguredSurfaceBuilder(surfaceBuilder, surfaceBuilderConfiguration);
         return this;
      }

      public Biome.BiomeBuilder surfaceBuilder(ConfiguredSurfaceBuilder surfaceBuilder) {
         this.surfaceBuilder = surfaceBuilder;
         return this;
      }

      public Biome.BiomeBuilder precipitation(Biome.Precipitation precipitation) {
         this.precipitation = precipitation;
         return this;
      }

      public Biome.BiomeBuilder biomeCategory(Biome.BiomeCategory biomeCategory) {
         this.biomeCategory = biomeCategory;
         return this;
      }

      public Biome.BiomeBuilder depth(float f) {
         this.depth = Float.valueOf(f);
         return this;
      }

      public Biome.BiomeBuilder scale(float f) {
         this.scale = Float.valueOf(f);
         return this;
      }

      public Biome.BiomeBuilder temperature(float f) {
         this.temperature = Float.valueOf(f);
         return this;
      }

      public Biome.BiomeBuilder downfall(float f) {
         this.downfall = Float.valueOf(f);
         return this;
      }

      public Biome.BiomeBuilder waterColor(int i) {
         this.waterColor = Integer.valueOf(i);
         return this;
      }

      public Biome.BiomeBuilder waterFogColor(int i) {
         this.waterFogColor = Integer.valueOf(i);
         return this;
      }

      public Biome.BiomeBuilder parent(@Nullable String parent) {
         this.parent = parent;
         return this;
      }

      public String toString() {
         return "BiomeBuilder{\nsurfaceBuilder=" + this.surfaceBuilder + ",\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.biomeCategory + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ndownfall=" + this.downfall + ",\nwaterColor=" + this.waterColor + ",\nwaterFogColor=" + this.waterFogColor + ",\nparent=\'" + this.parent + '\'' + "\n" + '}';
      }
   }

   public static enum BiomeCategory {
      NONE("none"),
      TAIGA("taiga"),
      EXTREME_HILLS("extreme_hills"),
      JUNGLE("jungle"),
      MESA("mesa"),
      PLAINS("plains"),
      SAVANNA("savanna"),
      ICY("icy"),
      THEEND("the_end"),
      BEACH("beach"),
      FOREST("forest"),
      OCEAN("ocean"),
      DESERT("desert"),
      RIVER("river"),
      SWAMP("swamp"),
      MUSHROOM("mushroom"),
      NETHER("nether");

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(Biome.BiomeCategory::getName, (biome$BiomeCategory) -> {
         return biome$BiomeCategory;
      }));
      private final String name;

      private BiomeCategory(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }
   }

   public static enum BiomeTempCategory {
      OCEAN("ocean"),
      COLD("cold"),
      MEDIUM("medium"),
      WARM("warm");

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(Biome.BiomeTempCategory::getName, (biome$BiomeTempCategory) -> {
         return biome$BiomeTempCategory;
      }));
      private final String name;

      private BiomeTempCategory(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }
   }

   public static enum Precipitation {
      NONE("none"),
      RAIN("rain"),
      SNOW("snow");

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(Biome.Precipitation::getName, (biome$Precipitation) -> {
         return biome$Precipitation;
      }));
      private final String name;

      private Precipitation(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }
   }

   public static class SpawnerData extends WeighedRandom.WeighedRandomItem {
      public final EntityType type;
      public final int minCount;
      public final int maxCount;

      public SpawnerData(EntityType type, int var2, int minCount, int maxCount) {
         super(var2);
         this.type = type;
         this.minCount = minCount;
         this.maxCount = maxCount;
      }

      public String toString() {
         return EntityType.getKey(this.type) + "*(" + this.minCount + "-" + this.maxCount + "):" + this.weight;
      }
   }
}
