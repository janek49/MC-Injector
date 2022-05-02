package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorCarvingMaskConfig implements DecoratorConfiguration {
   protected final GenerationStep.Carving step;
   protected final float probability;

   public DecoratorCarvingMaskConfig(GenerationStep.Carving step, float probability) {
      this.step = step;
      this.probability = probability;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("step"), dynamicOps.createString(this.step.toString()), dynamicOps.createString("probability"), dynamicOps.createFloat(this.probability))));
   }

   public static DecoratorCarvingMaskConfig deserialize(Dynamic dynamic) {
      GenerationStep.Carving var1 = GenerationStep.Carving.valueOf(dynamic.get("step").asString(""));
      float var2 = dynamic.get("probability").asFloat(0.0F);
      return new DecoratorCarvingMaskConfig(var1, var2);
   }
}
