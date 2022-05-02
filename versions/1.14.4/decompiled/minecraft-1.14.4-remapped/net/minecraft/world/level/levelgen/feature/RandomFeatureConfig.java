package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.WeightedConfiguredFeature;

public class RandomFeatureConfig implements FeatureConfiguration {
   public final List features;
   public final ConfiguredFeature defaultFeature;

   public RandomFeatureConfig(List features, ConfiguredFeature defaultFeature) {
      this.features = features;
      this.defaultFeature = defaultFeature;
   }

   public RandomFeatureConfig(Feature[] features, FeatureConfiguration[] featureConfigurations, float[] floats, Feature var4, FeatureConfiguration var5) {
      this((List)IntStream.range(0, features.length).mapToObj((var3) -> {
         return getWeightedConfiguredFeature(features[var3], featureConfigurations[var3], floats[var3]);
      }).collect(Collectors.toList()), getDefaultFeature(var4, var5));
   }

   private static WeightedConfiguredFeature getWeightedConfiguredFeature(Feature feature, FeatureConfiguration featureConfiguration, float var2) {
      return new WeightedConfiguredFeature(feature, featureConfiguration, Float.valueOf(var2));
   }

   private static ConfiguredFeature getDefaultFeature(Feature feature, FeatureConfiguration featureConfiguration) {
      return new ConfiguredFeature(feature, featureConfiguration);
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      T var2 = dynamicOps.createList(this.features.stream().map((weightedConfiguredFeature) -> {
         return weightedConfiguredFeature.serialize(dynamicOps).getValue();
      }));
      T var3 = this.defaultFeature.serialize(dynamicOps).getValue();
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("features"), var2, dynamicOps.createString("default"), var3)));
   }

   public static RandomFeatureConfig deserialize(Dynamic dynamic) {
      List<WeightedConfiguredFeature<?>> var1 = dynamic.get("features").asList(WeightedConfiguredFeature::deserialize);
      ConfiguredFeature<?> var2 = ConfiguredFeature.deserialize(dynamic.get("default").orElseEmptyMap());
      return new RandomFeatureConfig(var1, var2);
   }
}
