package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EmptyLootItem extends LootPoolSingletonContainer {
   private EmptyLootItem(int var1, int var2, LootItemCondition[] lootItemConditions, LootItemFunction[] lootItemFunctions) {
      super(var1, var2, lootItemConditions, lootItemFunctions);
   }

   public void createItemStack(Consumer consumer, LootContext lootContext) {
   }

   public static LootPoolSingletonContainer.Builder emptyItem() {
      return simpleBuilder(EmptyLootItem::<init>);
   }

   public static class Serializer extends LootPoolSingletonContainer.Serializer {
      public Serializer() {
         super(new ResourceLocation("empty"), EmptyLootItem.class);
      }

      protected EmptyLootItem deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int var3, int var4, LootItemCondition[] lootItemConditions, LootItemFunction[] lootItemFunctions) {
         return new EmptyLootItem(var3, var4, lootItemConditions, lootItemFunctions);
      }

      // $FF: synthetic method
      protected LootPoolSingletonContainer deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6) {
         return this.deserialize(var1, var2, var3, var4, var5, var6);
      }
   }
}
