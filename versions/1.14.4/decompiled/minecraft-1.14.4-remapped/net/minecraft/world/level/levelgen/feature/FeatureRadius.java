package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class FeatureRadius implements FeatureConfiguration {
   public final int radius;

   public FeatureRadius(int radius) {
      this.radius = radius;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("radius"), dynamicOps.createInt(this.radius))));
   }

   public static FeatureRadius deserialize(Dynamic dynamic) {
      int var1 = dynamic.get("radius").asInt(0);
      return new FeatureRadius(var1);
   }
}
