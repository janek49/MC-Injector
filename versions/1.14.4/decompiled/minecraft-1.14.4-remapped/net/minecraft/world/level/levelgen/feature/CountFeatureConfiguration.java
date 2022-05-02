package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class CountFeatureConfiguration implements FeatureConfiguration {
   public final int count;

   public CountFeatureConfiguration(int count) {
      this.count = count;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count))));
   }

   public static CountFeatureConfiguration deserialize(Dynamic dynamic) {
      int var1 = dynamic.get("count").asInt(0);
      return new CountFeatureConfiguration(var1);
   }
}
