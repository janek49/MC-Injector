package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorCountRange implements DecoratorConfiguration {
   public final int count;
   public final int bottomOffset;
   public final int topOffset;
   public final int maximum;

   public DecoratorCountRange(int count, int bottomOffset, int topOffset, int maximum) {
      this.count = count;
      this.bottomOffset = bottomOffset;
      this.topOffset = topOffset;
      this.maximum = maximum;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count), dynamicOps.createString("bottom_offset"), dynamicOps.createInt(this.bottomOffset), dynamicOps.createString("top_offset"), dynamicOps.createInt(this.topOffset), dynamicOps.createString("maximum"), dynamicOps.createInt(this.maximum))));
   }

   public static DecoratorCountRange deserialize(Dynamic dynamic) {
      int var1 = dynamic.get("count").asInt(0);
      int var2 = dynamic.get("bottom_offset").asInt(0);
      int var3 = dynamic.get("top_offset").asInt(0);
      int var4 = dynamic.get("maximum").asInt(0);
      return new DecoratorCountRange(var1, var2, var3, var4);
   }
}
