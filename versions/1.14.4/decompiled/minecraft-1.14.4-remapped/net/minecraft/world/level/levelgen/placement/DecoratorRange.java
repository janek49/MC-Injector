package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorRange implements DecoratorConfiguration {
   public final int min;
   public final int max;

   public DecoratorRange(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("min"), dynamicOps.createInt(this.min), dynamicOps.createString("max"), dynamicOps.createInt(this.max))));
   }

   public static DecoratorRange deserialize(Dynamic dynamic) {
      int var1 = dynamic.get("min").asInt(0);
      int var2 = dynamic.get("max").asInt(0);
      return new DecoratorRange(var1, var2);
   }
}
