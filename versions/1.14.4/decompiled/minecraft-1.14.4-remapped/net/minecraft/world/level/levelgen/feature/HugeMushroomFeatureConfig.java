package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class HugeMushroomFeatureConfig implements FeatureConfiguration {
   public final boolean planted;

   public HugeMushroomFeatureConfig(boolean planted) {
      this.planted = planted;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("planted"), dynamicOps.createBoolean(this.planted))));
   }

   public static HugeMushroomFeatureConfig deserialize(Dynamic dynamic) {
      boolean var1 = dynamic.get("planted").asBoolean(false);
      return new HugeMushroomFeatureConfig(var1);
   }
}
