package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.ConfiguredDecorator;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class DecoratedFeatureConfiguration implements FeatureConfiguration {
   public final ConfiguredFeature feature;
   public final ConfiguredDecorator decorator;

   public DecoratedFeatureConfiguration(ConfiguredFeature feature, ConfiguredDecorator decorator) {
      this.feature = feature;
      this.decorator = decorator;
   }

   public DecoratedFeatureConfiguration(Feature feature, FeatureConfiguration featureConfiguration, FeatureDecorator featureDecorator, DecoratorConfiguration decoratorConfiguration) {
      this(new ConfiguredFeature(feature, featureConfiguration), new ConfiguredDecorator(featureDecorator, decoratorConfiguration));
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("feature"), this.feature.serialize(dynamicOps).getValue(), dynamicOps.createString("decorator"), this.decorator.serialize(dynamicOps).getValue())));
   }

   public String toString() {
      return String.format("< %s [%s | %s] >", new Object[]{this.getClass().getSimpleName(), Registry.FEATURE.getKey(this.feature.feature), Registry.DECORATOR.getKey(this.decorator.decorator)});
   }

   public static DecoratedFeatureConfiguration deserialize(Dynamic dynamic) {
      ConfiguredFeature<?> var1 = ConfiguredFeature.deserialize(dynamic.get("feature").orElseEmptyMap());
      ConfiguredDecorator<?> var2 = ConfiguredDecorator.deserialize(dynamic.get("decorator").orElseEmptyMap());
      return new DecoratedFeatureConfiguration(var1, var2);
   }
}
