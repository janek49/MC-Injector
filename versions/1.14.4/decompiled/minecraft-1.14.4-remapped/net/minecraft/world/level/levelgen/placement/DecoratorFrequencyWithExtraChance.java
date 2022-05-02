package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorFrequencyWithExtraChance implements DecoratorConfiguration {
   public final int count;
   public final float extraChance;
   public final int extraCount;

   public DecoratorFrequencyWithExtraChance(int count, float extraChance, int extraCount) {
      this.count = count;
      this.extraChance = extraChance;
      this.extraCount = extraCount;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count), dynamicOps.createString("extra_chance"), dynamicOps.createFloat(this.extraChance), dynamicOps.createString("extra_count"), dynamicOps.createInt(this.extraCount))));
   }

   public static DecoratorFrequencyWithExtraChance deserialize(Dynamic dynamic) {
      int var1 = dynamic.get("count").asInt(0);
      float var2 = dynamic.get("extra_chance").asFloat(0.0F);
      int var3 = dynamic.get("extra_count").asInt(0);
      return new DecoratorFrequencyWithExtraChance(var1, var2, var3);
   }
}
