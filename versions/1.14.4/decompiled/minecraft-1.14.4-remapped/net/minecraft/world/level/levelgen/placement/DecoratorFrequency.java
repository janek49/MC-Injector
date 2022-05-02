package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorFrequency implements DecoratorConfiguration {
   public final int count;

   public DecoratorFrequency(int count) {
      this.count = count;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count))));
   }

   public static DecoratorFrequency deserialize(Dynamic dynamic) {
      int var1 = dynamic.get("count").asInt(0);
      return new DecoratorFrequency(var1);
   }
}
