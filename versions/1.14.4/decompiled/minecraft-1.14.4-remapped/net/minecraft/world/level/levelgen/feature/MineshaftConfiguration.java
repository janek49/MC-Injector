package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;

public class MineshaftConfiguration implements FeatureConfiguration {
   public final double probability;
   public final MineshaftFeature.Type type;

   public MineshaftConfiguration(double probability, MineshaftFeature.Type type) {
      this.probability = probability;
      this.type = type;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("probability"), dynamicOps.createDouble(this.probability), dynamicOps.createString("type"), dynamicOps.createString(this.type.getName()))));
   }

   public static MineshaftConfiguration deserialize(Dynamic dynamic) {
      float var1 = dynamic.get("probability").asFloat(0.0F);
      MineshaftFeature.Type var2 = MineshaftFeature.Type.byName(dynamic.get("type").asString(""));
      return new MineshaftConfiguration((double)var1, var2);
   }
}
