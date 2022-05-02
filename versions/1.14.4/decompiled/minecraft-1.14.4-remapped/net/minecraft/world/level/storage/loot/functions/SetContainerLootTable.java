package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerLootTable extends LootItemConditionalFunction {
   private final ResourceLocation name;
   private final long seed;

   private SetContainerLootTable(LootItemCondition[] lootItemConditions, ResourceLocation name, long seed) {
      super(lootItemConditions);
      this.name = name;
      this.seed = seed;
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      if(var1.isEmpty()) {
         return var1;
      } else {
         CompoundTag var3 = new CompoundTag();
         var3.putString("LootTable", this.name.toString());
         if(this.seed != 0L) {
            var3.putLong("LootTableSeed", this.seed);
         }

         var1.getOrCreateTag().put("BlockEntityTag", var3);
         return var1;
      }
   }

   public void validate(LootTableProblemCollector lootTableProblemCollector, Function function, Set set, LootContextParamSet lootContextParamSet) {
      if(set.contains(this.name)) {
         lootTableProblemCollector.reportProblem("Table " + this.name + " is recursively called");
      } else {
         super.validate(lootTableProblemCollector, function, set, lootContextParamSet);
         LootTable var5 = (LootTable)function.apply(this.name);
         if(var5 == null) {
            lootTableProblemCollector.reportProblem("Unknown loot table called " + this.name);
         } else {
            Set<ResourceLocation> var6 = ImmutableSet.builder().addAll(set).add(this.name).build();
            var5.validate(lootTableProblemCollector.forChild("->{" + this.name + "}"), function, var6, lootContextParamSet);
         }

      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      protected Serializer() {
         super(new ResourceLocation("set_loot_table"), SetContainerLootTable.class);
      }

      public void serialize(JsonObject jsonObject, SetContainerLootTable setContainerLootTable, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)setContainerLootTable, jsonSerializationContext);
         jsonObject.addProperty("name", setContainerLootTable.name.toString());
         if(setContainerLootTable.seed != 0L) {
            jsonObject.addProperty("seed", Long.valueOf(setContainerLootTable.seed));
         }

      }

      public SetContainerLootTable deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "name"));
         long var5 = GsonHelper.getAsLong(jsonObject, "seed", 0L);
         return new SetContainerLootTable(lootItemConditions, var4, var5);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
