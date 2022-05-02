package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class SpringConfiguration implements FeatureConfiguration {
   public final FluidState state;

   public SpringConfiguration(FluidState state) {
      this.state = state;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("state"), FluidState.serialize(dynamicOps, this.state).getValue())));
   }

   public static SpringConfiguration deserialize(Dynamic dynamic) {
      FluidState var1 = (FluidState)dynamic.get("state").map(FluidState::deserialize).orElse(Fluids.EMPTY.defaultFluidState());
      return new SpringConfiguration(var1);
   }
}
