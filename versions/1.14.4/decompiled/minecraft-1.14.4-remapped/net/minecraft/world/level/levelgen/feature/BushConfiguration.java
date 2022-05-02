package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.function.Function;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class BushConfiguration implements FeatureConfiguration {
   public final BlockState state;

   public BushConfiguration(BlockState state) {
      this.state = state;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.state).getValue())));
   }

   public static BushConfiguration deserialize(Dynamic dynamic) {
      BlockState var1 = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      return new BushConfiguration(var1);
   }
}
