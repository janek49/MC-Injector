package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class OreConfiguration implements FeatureConfiguration {
   public final OreConfiguration.Predicates target;
   public final int size;
   public final BlockState state;

   public OreConfiguration(OreConfiguration.Predicates target, BlockState state, int size) {
      this.size = size;
      this.state = state;
      this.target = target;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("size"), dynamicOps.createInt(this.size), dynamicOps.createString("target"), dynamicOps.createString(this.target.getName()), dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.state).getValue())));
   }

   public static OreConfiguration deserialize(Dynamic dynamic) {
      int var1 = dynamic.get("size").asInt(0);
      OreConfiguration.Predicates var2 = OreConfiguration.Predicates.byName(dynamic.get("target").asString(""));
      BlockState var3 = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      return new OreConfiguration(var2, var3, var1);
   }

   public static enum Predicates {
      NATURAL_STONE("natural_stone", (blockState) -> {
         if(blockState == null) {
            return false;
         } else {
            Block var1 = blockState.getBlock();
            return var1 == Blocks.STONE || var1 == Blocks.GRANITE || var1 == Blocks.DIORITE || var1 == Blocks.ANDESITE;
         }
      }),
      NETHERRACK("netherrack", new BlockPredicate(Blocks.NETHERRACK));

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(OreConfiguration.Predicates::getName, (oreConfiguration$Predicates) -> {
         return oreConfiguration$Predicates;
      }));
      private final String name;
      private final Predicate predicate;

      private Predicates(String name, Predicate predicate) {
         this.name = name;
         this.predicate = predicate;
      }

      public String getName() {
         return this.name;
      }

      public static OreConfiguration.Predicates byName(String name) {
         return (OreConfiguration.Predicates)BY_NAME.get(name);
      }

      public Predicate getPredicate() {
         return this.predicate;
      }
   }
}
