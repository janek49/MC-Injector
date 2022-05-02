package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorNoiseDependant implements DecoratorConfiguration {
   public final double noiseLevel;
   public final int belowNoise;
   public final int aboveNoise;

   public DecoratorNoiseDependant(double noiseLevel, int belowNoise, int aboveNoise) {
      this.noiseLevel = noiseLevel;
      this.belowNoise = belowNoise;
      this.aboveNoise = aboveNoise;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("noise_level"), dynamicOps.createDouble(this.noiseLevel), dynamicOps.createString("below_noise"), dynamicOps.createInt(this.belowNoise), dynamicOps.createString("above_noise"), dynamicOps.createInt(this.aboveNoise))));
   }

   public static DecoratorNoiseDependant deserialize(Dynamic dynamic) {
      double var1 = dynamic.get("noise_level").asDouble(0.0D);
      int var3 = dynamic.get("below_noise").asInt(0);
      int var4 = dynamic.get("above_noise").asInt(0);
      return new DecoratorNoiseDependant(var1, var3, var4);
   }
}
