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

public class SimpleRandomFeatureConfig implements FeatureConfiguration {
   public final List features;

   public SimpleRandomFeatureConfig(List features) {
      this.features = features;
   }

   public SimpleRandomFeatureConfig(Feature[] features, FeatureConfiguration[] featureConfigurations) {
      this((List)IntStream.range(0, features.length).mapToObj((var2) -> {
         return getConfiguredFeature(features[var2], featureConfigurations[var2]);
      }).collect(Collectors.toList()));
   }

   private static ConfiguredFeature getConfiguredFeature(Feature feature, FeatureConfiguration featureConfiguration) {
      return new ConfiguredFeature(feature, featureConfiguration);
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("features"), dynamicOps.createList(this.features.stream().map((configuredFeature) -> {
         return configuredFeature.serialize(dynamicOps).getValue();
      })))));
   }

   public static SimpleRandomFeatureConfig deserialize(Dynamic dynamic) {
      List<ConfiguredFeature<?>> var1 = dynamic.get("features").asList(ConfiguredFeature::deserialize);
      return new SimpleRandomFeatureConfig(var1);
   }
}
