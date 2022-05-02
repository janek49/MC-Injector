package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.function.Function;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class SimpleBlockConfiguration implements FeatureConfiguration {
   protected final BlockState toPlace;
   protected final List placeOn;
   protected final List placeIn;
   protected final List placeUnder;

   public SimpleBlockConfiguration(BlockState toPlace, List placeOn, List placeIn, List placeUnder) {
      this.toPlace = toPlace;
      this.placeOn = placeOn;
      this.placeIn = placeIn;
      this.placeUnder = placeUnder;
   }

   public SimpleBlockConfiguration(BlockState var1, BlockState[] vars2, BlockState[] vars3, BlockState[] vars4) {
      this(var1, (List)Lists.newArrayList(vars2), (List)Lists.newArrayList(vars3), (List)Lists.newArrayList(vars4));
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      T var2 = BlockState.serialize(dynamicOps, this.toPlace).getValue();
      T var3 = dynamicOps.createList(this.placeOn.stream().map((blockState) -> {
         return BlockState.serialize(dynamicOps, blockState).getValue();
      }));
      T var4 = dynamicOps.createList(this.placeIn.stream().map((blockState) -> {
         return BlockState.serialize(dynamicOps, blockState).getValue();
      }));
      T var5 = dynamicOps.createList(this.placeUnder.stream().map((blockState) -> {
         return BlockState.serialize(dynamicOps, blockState).getValue();
      }));
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("to_place"), var2, dynamicOps.createString("place_on"), var3, dynamicOps.createString("place_in"), var4, dynamicOps.createString("place_under"), var5)));
   }

   public static SimpleBlockConfiguration deserialize(Dynamic dynamic) {
      BlockState var1 = (BlockState)dynamic.get("to_place").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      List<BlockState> var2 = dynamic.get("place_on").asList(BlockState::deserialize);
      List<BlockState> var3 = dynamic.get("place_in").asList(BlockState::deserialize);
      List<BlockState> var4 = dynamic.get("place_under").asList(BlockState::deserialize);
      return new SimpleBlockConfiguration(var1, var2, var3, var4);
   }
}
