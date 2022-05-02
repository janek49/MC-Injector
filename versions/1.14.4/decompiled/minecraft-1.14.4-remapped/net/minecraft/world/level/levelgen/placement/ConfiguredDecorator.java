package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class ConfiguredDecorator {
   public final FeatureDecorator decorator;
   public final DecoratorConfiguration config;

   public ConfiguredDecorator(FeatureDecorator featureDecorator, Dynamic dynamic) {
      this(featureDecorator, featureDecorator.createSettings(dynamic));
   }

   public ConfiguredDecorator(FeatureDecorator decorator, DecoratorConfiguration config) {
      this.decorator = decorator;
      this.config = config;
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, ConfiguredFeature configuredFeature) {
      return this.decorator.placeFeature(levelAccessor, chunkGenerator, random, blockPos, this.config, configuredFeature);
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("name"), dynamicOps.createString(Registry.DECORATOR.getKey(this.decorator).toString()), dynamicOps.createString("config"), this.config.serialize(dynamicOps).getValue())));
   }

   public static ConfiguredDecorator deserialize(Dynamic dynamic) {
      FeatureDecorator<? extends DecoratorConfiguration> var1 = (FeatureDecorator)Registry.DECORATOR.get(new ResourceLocation(dynamic.get("name").asString("")));
      return new ConfiguredDecorator(var1, dynamic.get("config").orElseEmptyMap());
   }
}
