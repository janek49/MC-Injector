package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class HellSpringConfiguration implements FeatureConfiguration {
   public final boolean insideRock;

   public HellSpringConfiguration(boolean insideRock) {
      this.insideRock = insideRock;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("inside_rock"), dynamicOps.createBoolean(this.insideRock))));
   }

   public static HellSpringConfiguration deserialize(Dynamic dynamic) {
      boolean var1 = dynamic.get("inside_rock").asBoolean(false);
      return new HellSpringConfiguration(var1);
   }
}
