package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class WeightedConfiguredFeature {
   public final Feature feature;
   public final FeatureConfiguration config;
   public final Float chance;

   public WeightedConfiguredFeature(Feature feature, FeatureConfiguration config, Float chance) {
      this.feature = feature;
      this.config = config;
      this.chance = chance;
   }

   public WeightedConfiguredFeature(Feature feature, Dynamic dynamic, float var3) {
      this(feature, feature.createSettings(dynamic), Float.valueOf(var3));
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("name"), dynamicOps.createString(Registry.FEATURE.getKey(this.feature).toString()), dynamicOps.createString("config"), this.config.serialize(dynamicOps).getValue(), dynamicOps.createString("chance"), dynamicOps.createFloat(this.chance.floatValue()))));
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos) {
      return this.feature.place(levelAccessor, chunkGenerator, random, blockPos, this.config);
   }

   public static WeightedConfiguredFeature deserialize(Dynamic dynamic) {
      Feature<? extends FeatureConfiguration> var1 = (Feature)Registry.FEATURE.get(new ResourceLocation(dynamic.get("name").asString("")));
      return new WeightedConfiguredFeature(var1, dynamic.get("config").orElseEmptyMap(), dynamic.get("chance").asFloat(0.0F));
   }
}
