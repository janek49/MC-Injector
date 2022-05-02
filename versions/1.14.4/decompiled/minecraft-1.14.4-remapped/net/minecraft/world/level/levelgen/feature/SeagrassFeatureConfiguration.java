package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class SeagrassFeatureConfiguration implements FeatureConfiguration {
   public final int count;
   public final double tallSeagrassProbability;

   public SeagrassFeatureConfiguration(int count, double tallSeagrassProbability) {
      this.count = count;
      this.tallSeagrassProbability = tallSeagrassProbability;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count), dynamicOps.createString("tall_seagrass_probability"), dynamicOps.createDouble(this.tallSeagrassProbability))));
   }

   public static SeagrassFeatureConfiguration deserialize(Dynamic dynamic) {
      int var1 = dynamic.get("count").asInt(0);
      double var2 = dynamic.get("tall_seagrass_probability").asDouble(0.0D);
      return new SeagrassFeatureConfiguration(var1, var2);
   }
}
