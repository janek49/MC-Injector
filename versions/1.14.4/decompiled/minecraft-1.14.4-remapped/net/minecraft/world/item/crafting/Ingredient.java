package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class Ingredient implements Predicate {
   private static final Predicate NON_ALL_EMPTY = (ingredient$Value) -> {
      return !ingredient$Value.getItems().stream().allMatch(ItemStack::isEmpty);
   };
   public static final Ingredient EMPTY = new Ingredient(Stream.empty());
   private final Ingredient.Value[] values;
   private ItemStack[] itemStacks;
   private IntList stackingIds;

   private Ingredient(Stream stream) {
      this.values = (Ingredient.Value[])stream.filter(NON_ALL_EMPTY).toArray((i) -> {
         return new Ingredient.Value[i];
      });
   }

   public ItemStack[] getItems() {
      this.dissolve();
      return this.itemStacks;
   }

   private void dissolve() {
      if(this.itemStacks == null) {
         this.itemStacks = (ItemStack[])Arrays.stream(this.values).flatMap((ingredient$Value) -> {
            return ingredient$Value.getItems().stream();
         }).distinct().toArray((i) -> {
            return new ItemStack[i];
         });
      }

   }

   public boolean test(@Nullable ItemStack itemStack) {
      if(itemStack == null) {
         return false;
      } else if(this.values.length == 0) {
         return itemStack.isEmpty();
      } else {
         this.dissolve();

         for(ItemStack var5 : this.itemStacks) {
            if(var5.getItem() == itemStack.getItem()) {
               return true;
            }
         }

         return false;
      }
   }

   public IntList getStackingIds() {
      if(this.stackingIds == null) {
         this.dissolve();
         this.stackingIds = new IntArrayList(this.itemStacks.length);

         for(ItemStack var4 : this.itemStacks) {
            this.stackingIds.add(StackedContents.getStackingIndex(var4));
         }

         this.stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
      }

      return this.stackingIds;
   }

   public void toNetwork(FriendlyByteBuf friendlyByteBuf) {
      this.dissolve();
      friendlyByteBuf.writeVarInt(this.itemStacks.length);

      for(int var2 = 0; var2 < this.itemStacks.length; ++var2) {
         friendlyByteBuf.writeItem(this.itemStacks[var2]);
      }

   }

   public JsonElement toJson() {
      if(this.values.length == 1) {
         return this.values[0].serialize();
      } else {
         JsonArray var1 = new JsonArray();

         for(Ingredient.Value var5 : this.values) {
            var1.add(var5.serialize());
         }

         return var1;
      }
   }

   public boolean isEmpty() {
      return this.values.length == 0 && (this.itemStacks == null || this.itemStacks.length == 0) && (this.stackingIds == null || this.stackingIds.isEmpty());
   }

   private static Ingredient fromValues(Stream values) {
      Ingredient ingredient = new Ingredient(values);
      return ingredient.values.length == 0?EMPTY:ingredient;
   }

   public static Ingredient of(ItemLike... itemLikes) {
      return fromValues(Arrays.stream(itemLikes).map((itemLike) -> {
         return new Ingredient.ItemValue(new ItemStack(itemLike));
      }));
   }

   public static Ingredient of(ItemStack... itemStacks) {
      return fromValues(Arrays.stream(itemStacks).map((itemStack) -> {
         return new Ingredient.ItemValue(itemStack);
      }));
   }

   public static Ingredient of(Tag tag) {
      return fromValues(Stream.of(new Ingredient.TagValue(tag)));
   }

   public static Ingredient fromNetwork(FriendlyByteBuf network) {
      int var1 = network.readVarInt();
      return fromValues(Stream.generate(() -> {
         return new Ingredient.ItemValue(network.readItem());
      }).limit((long)var1));
   }

   public static Ingredient fromJson(@Nullable JsonElement json) {
      if(json != null && !json.isJsonNull()) {
         if(json.isJsonObject()) {
            return fromValues(Stream.of(valueFromJson(json.getAsJsonObject())));
         } else if(json.isJsonArray()) {
            JsonArray var1 = json.getAsJsonArray();
            if(var1.size() == 0) {
               throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            } else {
               return fromValues(StreamSupport.stream(var1.spliterator(), false).map((jsonElement) -> {
                  return valueFromJson(GsonHelper.convertToJsonObject(jsonElement, "item"));
               }));
            }
         } else {
            throw new JsonSyntaxException("Expected item to be object or array of objects");
         }
      } else {
         throw new JsonSyntaxException("Item cannot be null");
      }
   }

   public static Ingredient.Value valueFromJson(JsonObject jsonObject) {
      if(jsonObject.has("item") && jsonObject.has("tag")) {
         throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
      } else if(jsonObject.has("item")) {
         ResourceLocation var1 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "item"));
         Item var2 = (Item)Registry.ITEM.getOptional(var1).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown item \'" + var1 + "\'");
         });
         return new Ingredient.ItemValue(new ItemStack(var2));
      } else if(jsonObject.has("tag")) {
         ResourceLocation var1 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "tag"));
         Tag<Item> var2 = ItemTags.getAllTags().getTag(var1);
         if(var2 == null) {
            throw new JsonSyntaxException("Unknown item tag \'" + var1 + "\'");
         } else {
            return new Ingredient.TagValue(var2);
         }
      } else {
         throw new JsonParseException("An ingredient entry needs either a tag or an item");
      }
   }

   // $FF: synthetic method
   public boolean test(@Nullable Object var1) {
      return this.test((ItemStack)var1);
   }

   static class ItemValue implements Ingredient.Value {
      private final ItemStack item;

      private ItemValue(ItemStack item) {
         this.item = item;
      }

      public Collection getItems() {
         return Collections.singleton(this.item);
      }

      public JsonObject serialize() {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("item", Registry.ITEM.getKey(this.item.getItem()).toString());
         return jsonObject;
      }
   }

   static class TagValue implements Ingredient.Value {
      private final Tag tag;

      private TagValue(Tag tag) {
         this.tag = tag;
      }

      public Collection getItems() {
         List<ItemStack> var1 = Lists.newArrayList();

         for(Item var3 : this.tag.getValues()) {
            var1.add(new ItemStack(var3));
         }

         return var1;
      }

      public JsonObject serialize() {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("tag", this.tag.getId().toString());
         return jsonObject;
      }
   }

   interface Value {
      Collection getItems();

      JsonObject serialize();
   }
}
