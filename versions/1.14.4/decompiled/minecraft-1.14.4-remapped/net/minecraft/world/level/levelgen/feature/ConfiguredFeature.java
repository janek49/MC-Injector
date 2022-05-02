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

public class ConfiguredFeature {
   public final Feature feature;
   public final FeatureConfiguration config;

   public ConfiguredFeature(Feature feature, FeatureConfiguration config) {
      this.feature = feature;
      this.config = config;
   }

   public ConfiguredFeature(Feature feature, Dynamic dynamic) {
      this(feature, feature.createSettings(dynamic));
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("name"), dynamicOps.createString(Registry.FEATURE.getKey(this.feature).toString()), dynamicOps.createString("config"), this.config.serialize(dynamicOps).getValue())));
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos) {
      return this.feature.place(levelAccessor, chunkGenerator, random, blockPos, this.config);
   }

   public static ConfiguredFeature deserialize(Dynamic dynamic) {
      Feature<? extends FeatureConfiguration> var1 = (Feature)Registry.FEATURE.get(new ResourceLocation(dynamic.get("name").asString("")));
      return new ConfiguredFeature(var1, dynamic.get("config").orElseEmptyMap());
   }
}
