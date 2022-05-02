package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootTableReference extends LootPoolSingletonContainer {
   private final ResourceLocation name;

   private LootTableReference(ResourceLocation name, int var2, int var3, LootItemCondition[] lootItemConditions, LootItemFunction[] lootItemFunctions) {
      super(var2, var3, lootItemConditions, lootItemFunctions);
      this.name = name;
   }

   public void createItemStack(Consumer consumer, LootContext lootContext) {
      LootTable var3 = lootContext.getLootTables().get(this.name);
      var3.getRandomItemsRaw(lootContext, consumer);
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

   public static LootPoolSingletonContainer.Builder lootTableReference(ResourceLocation resourceLocation) {
      return simpleBuilder((var1, var2, lootItemConditions, lootItemFunctions) -> {
         return new LootTableReference(resourceLocation, var1, var2, lootItemConditions, lootItemFunctions);
      });
   }

   public static class Serializer extends LootPoolSingletonContainer.Serializer {
      public Serializer() {
         super(new ResourceLocation("loot_table"), LootTableReference.class);
      }

      public void serialize(JsonObject jsonObject, LootTableReference lootTableReference, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootPoolSingletonContainer)lootTableReference, jsonSerializationContext);
         jsonObject.addProperty("name", lootTableReference.name.toString());
      }

      protected LootTableReference deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int var3, int var4, LootItemCondition[] lootItemConditions, LootItemFunction[] lootItemFunctions) {
         ResourceLocation var7 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "name"));
         return new LootTableReference(var7, var3, var4, lootItemConditions, lootItemFunctions);
      }

      // $FF: synthetic method
      protected LootPoolSingletonContainer deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6) {
         return this.deserialize(var1, var2, var3, var4, var5, var6);
      }
   }
}
