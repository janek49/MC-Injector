package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorChanceRange implements DecoratorConfiguration {
   public final float chance;
   public final int bottomOffset;
   public final int topOffset;
   public final int top;

   public DecoratorChanceRange(float chance, int bottomOffset, int topOffset, int top) {
      this.chance = chance;
      this.bottomOffset = bottomOffset;
      this.topOffset = topOffset;
      this.top = top;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("chance"), dynamicOps.createFloat(this.chance), dynamicOps.createString("bottom_offset"), dynamicOps.createInt(this.bottomOffset), dynamicOps.createString("top_offset"), dynamicOps.createInt(this.topOffset), dynamicOps.createString("top"), dynamicOps.createInt(this.top))));
   }

   public static DecoratorChanceRange deserialize(Dynamic dynamic) {
      float var1 = dynamic.get("chance").asFloat(0.0F);
      int var2 = dynamic.get("bottom_offset").asInt(0);
      int var3 = dynamic.get("top_offset").asInt(0);
      int var4 = dynamic.get("top").asInt(0);
      return new DecoratorChanceRange(var1, var2, var3, var4);
   }
}
