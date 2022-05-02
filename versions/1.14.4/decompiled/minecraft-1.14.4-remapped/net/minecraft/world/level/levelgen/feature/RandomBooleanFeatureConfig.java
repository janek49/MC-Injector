package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class RandomBooleanFeatureConfig implements FeatureConfiguration {
   public final ConfiguredFeature featureTrue;
   public final ConfiguredFeature featureFalse;

   public RandomBooleanFeatureConfig(ConfiguredFeature featureTrue, ConfiguredFeature featureFalse) {
      this.featureTrue = featureTrue;
      this.featureFalse = featureFalse;
   }

   public RandomBooleanFeatureConfig(Feature var1, FeatureConfiguration var2, Feature var3, FeatureConfiguration var4) {
      this(getFeature(var1, var2), getFeature(var3, var4));
   }

   private static ConfiguredFeature getFeature(Feature feature, FeatureConfiguration featureConfiguration) {
      return new ConfiguredFeature(feature, featureConfiguration);
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("feature_true"), this.featureTrue.serialize(dynamicOps).getValue(), dynamicOps.createString("feature_false"), this.featureFalse.serialize(dynamicOps).getValue())));
   }

   public static RandomBooleanFeatureConfig deserialize(Dynamic dynamic) {
      ConfiguredFeature<?> var1 = ConfiguredFeature.deserialize(dynamic.get("feature_true").orElseEmptyMap());
      ConfiguredFeature<?> var2 = ConfiguredFeature.deserialize(dynamic.get("feature_false").orElseEmptyMap());
      return new RandomBooleanFeatureConfig(var1, var2);
   }
}
