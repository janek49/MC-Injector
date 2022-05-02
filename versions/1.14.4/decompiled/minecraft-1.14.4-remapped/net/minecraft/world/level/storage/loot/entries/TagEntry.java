package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class TagEntry extends LootPoolSingletonContainer {
   private final Tag tag;
   private final boolean expand;

   private TagEntry(Tag tag, boolean expand, int var3, int var4, LootItemCondition[] lootItemConditions, LootItemFunction[] lootItemFunctions) {
      super(var3, var4, lootItemConditions, lootItemFunctions);
      this.tag = tag;
      this.expand = expand;
   }

   public void createItemStack(Consumer consumer, LootContext lootContext) {
      this.tag.getValues().forEach((item) -> {
         consumer.accept(new ItemStack(item));
      });
   }

   private boolean expandTag(LootContext lootContext, Consumer consumer) {
      if(!this.canRun(lootContext)) {
         return false;
      } else {
         for(final Item var4 : this.tag.getValues()) {
            consumer.accept(new LootPoolSingletonContainer.EntryBase() {
               public void createItemStack(Consumer consumer, LootContext lootContext) {
                  consumer.accept(new ItemStack(var4));
               }
            });
         }

         return true;
      }
   }

   public boolean expand(LootContext lootContext, Consumer consumer) {
      return this.expand?this.expandTag(lootContext, consumer):super.expand(lootContext, consumer);
   }

   public static LootPoolSingletonContainer.Builder expandTag(Tag tag) {
      return simpleBuilder((var1, var2, lootItemConditions, lootItemFunctions) -> {
         return new TagEntry(tag, true, var1, var2, lootItemConditions, lootItemFunctions);
      });
   }

   public static class Serializer extends LootPoolSingletonContainer.Serializer {
      public Serializer() {
         super(new ResourceLocation("tag"), TagEntry.class);
      }

      public void serialize(JsonObject jsonObject, TagEntry tagEntry, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootPoolSingletonContainer)tagEntry, jsonSerializationContext);
         jsonObject.addProperty("name", tagEntry.tag.getId().toString());
         jsonObject.addProperty("expand", Boolean.valueOf(tagEntry.expand));
      }

      protected TagEntry deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int var3, int var4, LootItemCondition[] lootItemConditions, LootItemFunction[] lootItemFunctions) {
         ResourceLocation var7 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "name"));
         Tag<Item> var8 = ItemTags.getAllTags().getTag(var7);
         if(var8 == null) {
            throw new JsonParseException("Can\'t find tag: " + var7);
         } else {
            boolean var9 = GsonHelper.getAsBoolean(jsonObject, "expand");
            return new TagEntry(var8, var9, var3, var4, lootItemConditions, lootItemFunctions);
         }
      }

      // $FF: synthetic method
      protected LootPoolSingletonContainer deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6) {
         return this.deserialize(var1, var2, var3, var4, var5, var6);
      }
   }
}
