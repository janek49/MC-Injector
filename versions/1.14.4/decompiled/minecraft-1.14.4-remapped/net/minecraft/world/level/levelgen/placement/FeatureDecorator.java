package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DecoratorChanceRange;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.DecoratorCountRange;
import net.minecraft.world.level.levelgen.feature.DecoratorNoiseDependant;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.CarvingMaskDecorator;
import net.minecraft.world.level.levelgen.placement.ChanceHeightmapDecorator;
import net.minecraft.world.level.levelgen.placement.ChanceHeightmapDoubleDecorator;
import net.minecraft.world.level.levelgen.placement.ChancePassthroughDecorator;
import net.minecraft.world.level.levelgen.placement.ChanceTopSolidHeightmapDecorator;
import net.minecraft.world.level.levelgen.placement.ChorusPlantPlacementDecorator;
import net.minecraft.world.level.levelgen.placement.CountBiasedRangeDecorator;
import net.minecraft.world.level.levelgen.placement.CountChanceHeightmapDecorator;
import net.minecraft.world.level.levelgen.placement.CountChanceHeightmapDoubleDecorator;
import net.minecraft.world.level.levelgen.placement.CountDepthAverageDecorator;
import net.minecraft.world.level.levelgen.placement.CountHeighmapDoubleDecorator;
import net.minecraft.world.level.levelgen.placement.CountHeight64Decorator;
import net.minecraft.world.level.levelgen.placement.CountHeightmap32Decorator;
import net.minecraft.world.level.levelgen.placement.CountHeightmapDecorator;
import net.minecraft.world.level.levelgen.placement.CountTopSolidDecorator;
import net.minecraft.world.level.levelgen.placement.CountVeryBiasedRangeDecorator;
import net.minecraft.world.level.levelgen.placement.CountWithExtraChanceHeightmapDecorator;
import net.minecraft.world.level.levelgen.placement.DarkOakTreePlacementDecorator;
import net.minecraft.world.level.levelgen.placement.DecoratorCarvingMaskConfig;
import net.minecraft.world.level.levelgen.placement.DecoratorChance;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequency;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequencyChance;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequencyWithExtraChance;
import net.minecraft.world.level.levelgen.placement.DecoratorNoiseCountFactor;
import net.minecraft.world.level.levelgen.placement.DecoratorRange;
import net.minecraft.world.level.levelgen.placement.DepthAverageConfigation;
import net.minecraft.world.level.levelgen.placement.EmeraldPlacementDecorator;
import net.minecraft.world.level.levelgen.placement.EndGatewayPlacementDecorator;
import net.minecraft.world.level.levelgen.placement.EndIslandPlacementDecorator;
import net.minecraft.world.level.levelgen.placement.ForestRockPlacementDecorator;
import net.minecraft.world.level.levelgen.placement.IcebergPlacementDecorator;
import net.minecraft.world.level.levelgen.placement.LakeChanceDecoratorConfig;
import net.minecraft.world.level.levelgen.placement.LakeLavaPlacementDecorator;
import net.minecraft.world.level.levelgen.placement.LakeWaterPlacementDecorator;
import net.minecraft.world.level.levelgen.placement.MonsterRoomPlacementConfiguration;
import net.minecraft.world.level.levelgen.placement.MonsterRoomPlacementDecorator;
import net.minecraft.world.level.levelgen.placement.NoiseHeightmap32Decorator;
import net.minecraft.world.level.levelgen.placement.NoiseHeightmapDoubleDecorator;
import net.minecraft.world.level.levelgen.placement.NopePlacementDecorator;
import net.minecraft.world.level.levelgen.placement.TopSolidHeightMapDecorator;
import net.minecraft.world.level.levelgen.placement.TopSolidHeightMapNoiseBasedDecorator;
import net.minecraft.world.level.levelgen.placement.TopSolidHeightMapRangeDecorator;
import net.minecraft.world.level.levelgen.placement.nether.ChanceRangeDecorator;
import net.minecraft.world.level.levelgen.placement.nether.CountRangeDecorator;
import net.minecraft.world.level.levelgen.placement.nether.HellFireDecorator;
import net.minecraft.world.level.levelgen.placement.nether.LightGemChanceDecorator;
import net.minecraft.world.level.levelgen.placement.nether.MagmaDecorator;
import net.minecraft.world.level.levelgen.placement.nether.RandomCountRangeDecorator;

public abstract class FeatureDecorator {
   public static final FeatureDecorator COUNT_HEIGHTMAP = register("count_heightmap", new CountHeightmapDecorator(DecoratorFrequency::deserialize));
   public static final FeatureDecorator COUNT_TOP_SOLID = register("count_top_solid", new CountTopSolidDecorator(DecoratorFrequency::deserialize));
   public static final FeatureDecorator COUNT_HEIGHTMAP_32 = register("count_heightmap_32", new CountHeightmap32Decorator(DecoratorFrequency::deserialize));
   public static final FeatureDecorator COUNT_HEIGHTMAP_DOUBLE = register("count_heightmap_double", new CountHeighmapDoubleDecorator(DecoratorFrequency::deserialize));
   public static final FeatureDecorator COUNT_HEIGHT_64 = register("count_height_64", new CountHeight64Decorator(DecoratorFrequency::deserialize));
   public static final FeatureDecorator NOISE_HEIGHTMAP_32 = register("noise_heightmap_32", new NoiseHeightmap32Decorator(DecoratorNoiseDependant::deserialize));
   public static final FeatureDecorator NOISE_HEIGHTMAP_DOUBLE = register("noise_heightmap_double", new NoiseHeightmapDoubleDecorator(DecoratorNoiseDependant::deserialize));
   public static final FeatureDecorator NOPE = register("nope", new NopePlacementDecorator(NoneDecoratorConfiguration::deserialize));
   public static final FeatureDecorator CHANCE_HEIGHTMAP = register("chance_heightmap", new ChanceHeightmapDecorator(DecoratorChance::deserialize));
   public static final FeatureDecorator CHANCE_HEIGHTMAP_DOUBLE = register("chance_heightmap_double", new ChanceHeightmapDoubleDecorator(DecoratorChance::deserialize));
   public static final FeatureDecorator CHANCE_PASSTHROUGH = register("chance_passthrough", new ChancePassthroughDecorator(DecoratorChance::deserialize));
   public static final FeatureDecorator CHANCE_TOP_SOLID_HEIGHTMAP = register("chance_top_solid_heightmap", new ChanceTopSolidHeightmapDecorator(DecoratorChance::deserialize));
   public static final FeatureDecorator COUNT_EXTRA_HEIGHTMAP = register("count_extra_heightmap", new CountWithExtraChanceHeightmapDecorator(DecoratorFrequencyWithExtraChance::deserialize));
   public static final FeatureDecorator COUNT_RANGE = register("count_range", new CountRangeDecorator(DecoratorCountRange::deserialize));
   public static final FeatureDecorator COUNT_BIASED_RANGE = register("count_biased_range", new CountBiasedRangeDecorator(DecoratorCountRange::deserialize));
   public static final FeatureDecorator COUNT_VERY_BIASED_RANGE = register("count_very_biased_range", new CountVeryBiasedRangeDecorator(DecoratorCountRange::deserialize));
   public static final FeatureDecorator RANDOM_COUNT_RANGE = register("random_count_range", new RandomCountRangeDecorator(DecoratorCountRange::deserialize));
   public static final FeatureDecorator CHANCE_RANGE = register("chance_range", new ChanceRangeDecorator(DecoratorChanceRange::deserialize));
   public static final FeatureDecorator COUNT_CHANCE_HEIGHTMAP = register("count_chance_heightmap", new CountChanceHeightmapDecorator(DecoratorFrequencyChance::deserialize));
   public static final FeatureDecorator COUNT_CHANCE_HEIGHTMAP_DOUBLE = register("count_chance_heightmap_double", new CountChanceHeightmapDoubleDecorator(DecoratorFrequencyChance::deserialize));
   public static final FeatureDecorator COUNT_DEPTH_AVERAGE = register("count_depth_average", new CountDepthAverageDecorator(DepthAverageConfigation::deserialize));
   public static final FeatureDecorator TOP_SOLID_HEIGHTMAP = register("top_solid_heightmap", new TopSolidHeightMapDecorator(NoneDecoratorConfiguration::deserialize));
   public static final FeatureDecorator TOP_SOLID_HEIGHTMAP_RANGE = register("top_solid_heightmap_range", new TopSolidHeightMapRangeDecorator(DecoratorRange::deserialize));
   public static final FeatureDecorator TOP_SOLID_HEIGHTMAP_NOISE_BIASED = register("top_solid_heightmap_noise_biased", new TopSolidHeightMapNoiseBasedDecorator(DecoratorNoiseCountFactor::deserialize));
   public static final FeatureDecorator CARVING_MASK = register("carving_mask", new CarvingMaskDecorator(DecoratorCarvingMaskConfig::deserialize));
   public static final FeatureDecorator FOREST_ROCK = register("forest_rock", new ForestRockPlacementDecorator(DecoratorFrequency::deserialize));
   public static final FeatureDecorator HELL_FIRE = register("hell_fire", new HellFireDecorator(DecoratorFrequency::deserialize));
   public static final FeatureDecorator MAGMA = register("magma", new MagmaDecorator(DecoratorFrequency::deserialize));
   public static final FeatureDecorator EMERALD_ORE = register("emerald_ore", new EmeraldPlacementDecorator(NoneDecoratorConfiguration::deserialize));
   public static final FeatureDecorator LAVA_LAKE = register("lava_lake", new LakeLavaPlacementDecorator(LakeChanceDecoratorConfig::deserialize));
   public static final FeatureDecorator WATER_LAKE = register("water_lake", new LakeWaterPlacementDecorator(LakeChanceDecoratorConfig::deserialize));
   public static final FeatureDecorator DUNGEONS = register("dungeons", new MonsterRoomPlacementDecorator(MonsterRoomPlacementConfiguration::deserialize));
   public static final FeatureDecorator DARK_OAK_TREE = register("dark_oak_tree", new DarkOakTreePlacementDecorator(NoneDecoratorConfiguration::deserialize));
   public static final FeatureDecorator ICEBERG = register("iceberg", new IcebergPlacementDecorator(DecoratorChance::deserialize));
   public static final FeatureDecorator LIGHT_GEM_CHANCE = register("light_gem_chance", new LightGemChanceDecorator(DecoratorFrequency::deserialize));
   public static final FeatureDecorator END_ISLAND = register("end_island", new EndIslandPlacementDecorator(NoneDecoratorConfiguration::deserialize));
   public static final FeatureDecorator CHORUS_PLANT = register("chorus_plant", new ChorusPlantPlacementDecorator(NoneDecoratorConfiguration::deserialize));
   public static final FeatureDecorator END_GATEWAY = register("end_gateway", new EndGatewayPlacementDecorator(NoneDecoratorConfiguration::deserialize));
   private final Function configurationFactory;

   private static FeatureDecorator register(String string, FeatureDecorator var1) {
      return (FeatureDecorator)Registry.register(Registry.DECORATOR, (String)string, var1);
   }

   public FeatureDecorator(Function configurationFactory) {
      this.configurationFactory = configurationFactory;
   }

   public DecoratorConfiguration createSettings(Dynamic dynamic) {
      return (DecoratorConfiguration)this.configurationFactory.apply(dynamic);
   }

   protected boolean placeFeature(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DecoratorConfiguration decoratorConfiguration, ConfiguredFeature configuredFeature) {
      AtomicBoolean var7 = new AtomicBoolean(false);
      this.getPositions(levelAccessor, chunkGenerator, random, decoratorConfiguration, blockPos).forEach((blockPos) -> {
         boolean var6 = configuredFeature.place(levelAccessor, chunkGenerator, random, blockPos);
         var7.set(var7.get() || var6);
      });
      return var7.get();
   }

   public abstract Stream getPositions(LevelAccessor var1, ChunkGenerator var2, Random var3, DecoratorConfiguration var4, BlockPos var5);

   public String toString() {
      return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
   }
}
