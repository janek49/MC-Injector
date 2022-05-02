package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorFrequencyChance implements DecoratorConfiguration {
   public final int count;
   public final float chance;

   public DecoratorFrequencyChance(int count, float chance) {
      this.count = count;
      this.chance = chance;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count), dynamicOps.createString("chance"), dynamicOps.createFloat(this.chance))));
   }

   public static DecoratorFrequencyChance deserialize(Dynamic dynamic) {
      int var1 = dynamic.get("count").asInt(0);
      float var2 = dynamic.get("chance").asFloat(0.0F);
      return new DecoratorFrequencyChance(var1, var2);
   }
}
