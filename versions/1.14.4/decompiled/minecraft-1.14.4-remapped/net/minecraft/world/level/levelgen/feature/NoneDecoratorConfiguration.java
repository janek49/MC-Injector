package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class NoneDecoratorConfiguration implements DecoratorConfiguration {
   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.emptyMap());
   }

   public static NoneDecoratorConfiguration deserialize(Dynamic dynamic) {
      return new NoneDecoratorConfiguration();
   }
}
