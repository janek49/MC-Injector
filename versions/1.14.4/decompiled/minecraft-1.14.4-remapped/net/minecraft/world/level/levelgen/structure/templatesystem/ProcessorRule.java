package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Deserializer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public class ProcessorRule {
   private final RuleTest inputPredicate;
   private final RuleTest locPredicate;
   private final BlockState outputState;
   @Nullable
   private final CompoundTag outputTag;

   public ProcessorRule(RuleTest var1, RuleTest var2, BlockState blockState) {
      this(var1, var2, blockState, (CompoundTag)null);
   }

   public ProcessorRule(RuleTest inputPredicate, RuleTest locPredicate, BlockState outputState, @Nullable CompoundTag outputTag) {
      this.inputPredicate = inputPredicate;
      this.locPredicate = locPredicate;
      this.outputState = outputState;
      this.outputTag = outputTag;
   }

   public boolean test(BlockState var1, BlockState var2, Random random) {
      return this.inputPredicate.test(var1, random) && this.locPredicate.test(var2, random);
   }

   public BlockState getOutputState() {
      return this.outputState;
   }

   @Nullable
   public CompoundTag getOutputTag() {
      return this.outputTag;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      T var2 = dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("input_predicate"), this.inputPredicate.serialize(dynamicOps).getValue(), dynamicOps.createString("location_predicate"), this.locPredicate.serialize(dynamicOps).getValue(), dynamicOps.createString("output_state"), BlockState.serialize(dynamicOps, this.outputState).getValue()));
      return this.outputTag == null?new Dynamic(dynamicOps, var2):new Dynamic(dynamicOps, dynamicOps.mergeInto(var2, dynamicOps.createString("output_nbt"), (new Dynamic(NbtOps.INSTANCE, this.outputTag)).convert(dynamicOps).getValue()));
   }

   public static ProcessorRule deserialize(Dynamic dynamic) {
      Dynamic<T> dynamic = dynamic.get("input_predicate").orElseEmptyMap();
      Dynamic<T> var2 = dynamic.get("location_predicate").orElseEmptyMap();
      RuleTest var3 = (RuleTest)Deserializer.deserialize(dynamic, Registry.RULE_TEST, "predicate_type", AlwaysTrueTest.INSTANCE);
      RuleTest var4 = (RuleTest)Deserializer.deserialize(var2, Registry.RULE_TEST, "predicate_type", AlwaysTrueTest.INSTANCE);
      BlockState var5 = BlockState.deserialize(dynamic.get("output_state").orElseEmptyMap());
      CompoundTag var6 = (CompoundTag)dynamic.get("output_nbt").map((dynamic) -> {
         return (Tag)dynamic.convert(NbtOps.INSTANCE).getValue();
      }).orElse((Object)null);
      return new ProcessorRule(var3, var4, var5, var6);
   }
}
