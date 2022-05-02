package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class DynamicLoot extends LootPoolSingletonContainer {
   public static final ResourceLocation TYPE = new ResourceLocation("dynamic");
   private final ResourceLocation name;

   private DynamicLoot(ResourceLocation name, int var2, int var3, LootItemCondition[] lootItemConditions, LootItemFunction[] lootItemFunctions) {
      super(var2, var3, lootItemConditions, lootItemFunctions);
      this.name = name;
   }

   public void createItemStack(Consumer consumer, LootContext lootContext) {
      lootContext.addDynamicDrops(this.name, consumer);
   }

   public static LootPoolSingletonContainer.Builder dynamicEntry(ResourceLocation resourceLocation) {
      return simpleBuilder((var1, var2, lootItemConditions, lootItemFunctions) -> {
         return new DynamicLoot(resourceLocation, var1, var2, lootItemConditions, lootItemFunctions);
      });
   }

   public static class Serializer extends LootPoolSingletonContainer.Serializer {
      public Serializer() {
         super(new ResourceLocation("dynamic"), DynamicLoot.class);
      }

      public void serialize(JsonObject jsonObject, DynamicLoot dynamicLoot, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootPoolSingletonContainer)dynamicLoot, jsonSerializationContext);
         jsonObject.addProperty("name", dynamicLoot.name.toString());
      }

      protected DynamicLoot deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int var3, int var4, LootItemCondition[] lootItemConditions, LootItemFunction[] lootItemFunctions) {
         ResourceLocation var7 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "name"));
         return new DynamicLoot(var7, var3, var4, lootItemConditions, lootItemFunctions);
      }

      // $FF: synthetic method
      protected LootPoolSingletonContainer deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6) {
         return this.deserialize(var1, var2, var3, var4, var5, var6);
      }
   }
}
