package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class PillagerOutpostConfiguration implements FeatureConfiguration {
   public final double probability;

   public PillagerOutpostConfiguration(double probability) {
      this.probability = probability;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("probability"), dynamicOps.createDouble(this.probability))));
   }

   public static PillagerOutpostConfiguration deserialize(Dynamic dynamic) {
      float var1 = dynamic.get("probability").asFloat(0.0F);
      return new PillagerOutpostConfiguration((double)var1);
   }
}
