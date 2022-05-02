package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.function.Function;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class GrassConfiguration implements FeatureConfiguration {
   public final BlockState state;

   public GrassConfiguration(BlockState state) {
      this.state = state;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.state).getValue())));
   }

   public static GrassConfiguration deserialize(Dynamic dynamic) {
      BlockState var1 = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      return new GrassConfiguration(var1);
   }
}
