package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class ShipwreckConfiguration implements FeatureConfiguration {
   public final boolean isBeached;

   public ShipwreckConfiguration(boolean isBeached) {
      this.isBeached = isBeached;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("is_beached"), dynamicOps.createBoolean(this.isBeached))));
   }

   public static ShipwreckConfiguration deserialize(Dynamic dynamic) {
      boolean var1 = dynamic.get("is_beached").asBoolean(false);
      return new ShipwreckConfiguration(var1);
   }
}
