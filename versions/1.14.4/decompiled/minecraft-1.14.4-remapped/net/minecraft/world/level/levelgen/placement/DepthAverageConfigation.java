package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DepthAverageConfigation implements DecoratorConfiguration {
   public final int count;
   public final int baseline;
   public final int spread;

   public DepthAverageConfigation(int count, int baseline, int spread) {
      this.count = count;
      this.baseline = baseline;
      this.spread = spread;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count), dynamicOps.createString("baseline"), dynamicOps.createInt(this.baseline), dynamicOps.createString("spread"), dynamicOps.createInt(this.spread))));
   }

   public static DepthAverageConfigation deserialize(Dynamic dynamic) {
      int var1 = dynamic.get("count").asInt(0);
      int var2 = dynamic.get("baseline").asInt(0);
      int var3 = dynamic.get("spread").asInt(0);
      return new DepthAverageConfigation(var1, var2, var3);
   }
}
