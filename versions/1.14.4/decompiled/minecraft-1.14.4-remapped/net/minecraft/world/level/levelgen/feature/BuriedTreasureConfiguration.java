package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class BuriedTreasureConfiguration implements FeatureConfiguration {
   public final float probability;

   public BuriedTreasureConfiguration(float probability) {
      this.probability = probability;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("probability"), dynamicOps.createFloat(this.probability))));
   }

   public static BuriedTreasureConfiguration deserialize(Dynamic dynamic) {
      float var1 = dynamic.get("probability").asFloat(0.0F);
      return new BuriedTreasureConfiguration(var1);
   }
}
