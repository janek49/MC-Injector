package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItem extends LootPoolSingletonContainer {
   private final Item item;

   private LootItem(Item item, int var2, int var3, LootItemCondition[] lootItemConditions, LootItemFunction[] lootItemFunctions) {
      super(var2, var3, lootItemConditions, lootItemFunctions);
      this.item = item;
   }

   public void createItemStack(Consumer consumer, LootContext lootContext) {
      consumer.accept(new ItemStack(this.item));
   }

   public static LootPoolSingletonContainer.Builder lootTableItem(ItemLike itemLike) {
      return simpleBuilder((var1, var2, lootItemConditions, lootItemFunctions) -> {
         return new LootItem(itemLike.asItem(), var1, var2, lootItemConditions, lootItemFunctions);
      });
   }

   public static class Serializer extends LootPoolSingletonContainer.Serializer {
      public Serializer() {
         super(new ResourceLocation("item"), LootItem.class);
      }

      public void serialize(JsonObject jsonObject, LootItem lootItem, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootPoolSingletonContainer)lootItem, jsonSerializationContext);
         ResourceLocation var4 = Registry.ITEM.getKey(lootItem.item);
         if(var4 == null) {
            throw new IllegalArgumentException("Can\'t serialize unknown item " + lootItem.item);
         } else {
            jsonObject.addProperty("name", var4.toString());
         }
      }

      protected LootItem deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int var3, int var4, LootItemCondition[] lootItemConditions, LootItemFunction[] lootItemFunctions) {
         Item var7 = GsonHelper.getAsItem(jsonObject, "name");
         return new LootItem(var7, var3, var4, lootItemConditions, lootItemFunctions);
      }

      // $FF: synthetic method
      protected LootPoolSingletonContainer deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6) {
         return this.deserialize(var1, var2, var3, var4, var5, var6);
      }
   }
}
