package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class NoneFeatureConfiguration implements FeatureConfiguration {
   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.emptyMap());
   }

   public static NoneFeatureConfiguration deserialize(Dynamic dynamic) {
      return FeatureConfiguration.NONE;
   }
}
