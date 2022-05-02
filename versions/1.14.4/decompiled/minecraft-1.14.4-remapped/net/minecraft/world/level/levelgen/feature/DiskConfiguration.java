package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.function.Function;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class DiskConfiguration implements FeatureConfiguration {
   public final BlockState state;
   public final int radius;
   public final int ySize;
   public final List targets;

   public DiskConfiguration(BlockState state, int radius, int ySize, List targets) {
      this.state = state;
      this.radius = radius;
      this.ySize = ySize;
      this.targets = targets;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.state).getValue(), dynamicOps.createString("radius"), dynamicOps.createInt(this.radius), dynamicOps.createString("y_size"), dynamicOps.createInt(this.ySize), dynamicOps.createString("targets"), dynamicOps.createList(this.targets.stream().map((blockState) -> {
         return BlockState.serialize(dynamicOps, blockState).getValue();
      })))));
   }

   public static DiskConfiguration deserialize(Dynamic dynamic) {
      BlockState var1 = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      int var2 = dynamic.get("radius").asInt(0);
      int var3 = dynamic.get("y_size").asInt(0);
      List<BlockState> var4 = dynamic.get("targets").asList(BlockState::deserialize);
      return new DiskConfiguration(var1, var2, var3, var4);
   }
}
