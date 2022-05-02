package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;

public class ItemPredicate {
   public static final ItemPredicate ANY = new ItemPredicate();
   @Nullable
   private final Tag tag;
   @Nullable
   private final Item item;
   private final MinMaxBounds.Ints count;
   private final MinMaxBounds.Ints durability;
   private final EnchantmentPredicate[] enchantments;
   @Nullable
   private final Potion potion;
   private final NbtPredicate nbt;

   public ItemPredicate() {
      this.tag = null;
      this.item = null;
      this.potion = null;
      this.count = MinMaxBounds.Ints.ANY;
      this.durability = MinMaxBounds.Ints.ANY;
      this.enchantments = new EnchantmentPredicate[0];
      this.nbt = NbtPredicate.ANY;
   }

   public ItemPredicate(@Nullable Tag tag, @Nullable Item item, MinMaxBounds.Ints count, MinMaxBounds.Ints durability, EnchantmentPredicate[] enchantments, @Nullable Potion potion, NbtPredicate nbt) {
      this.tag = tag;
      this.item = item;
      this.count = count;
      this.durability = durability;
      this.enchantments = enchantments;
      this.potion = potion;
      this.nbt = nbt;
   }

   public boolean matches(ItemStack itemStack) {
      if(this == ANY) {
         return true;
      } else if(this.tag != null && !this.tag.contains(itemStack.getItem())) {
         return false;
      } else if(this.item != null && itemStack.getItem() != this.item) {
         return false;
      } else if(!this.count.matches(itemStack.getCount())) {
         return false;
      } else if(!this.durability.isAny() && !itemStack.isDamageableItem()) {
         return false;
      } else if(!this.durability.matches(itemStack.getMaxDamage() - itemStack.getDamageValue())) {
         return false;
      } else if(!this.nbt.matches(itemStack)) {
         return false;
      } else {
         Map<Enchantment, Integer> var2 = EnchantmentHelper.getEnchantments(itemStack);

         for(int var3 = 0; var3 < this.enchantments.length; ++var3) {
            if(!this.enchantments[var3].containedIn(var2)) {
               return false;
            }
         }

         Potion var3 = PotionUtils.getPotion(itemStack);
         if(this.potion != null && this.potion != var3) {
            return false;
         } else {
            return true;
         }
      }
   }

   public static ItemPredicate fromJson(@Nullable JsonElement json) {
      if(json != null && !json.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(json, "item");
         MinMaxBounds.Ints var2 = MinMaxBounds.Ints.fromJson(var1.get("count"));
         MinMaxBounds.Ints var3 = MinMaxBounds.Ints.fromJson(var1.get("durability"));
         if(var1.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
         } else {
            NbtPredicate var4 = NbtPredicate.fromJson(var1.get("nbt"));
            Item var5 = null;
            if(var1.has("item")) {
               ResourceLocation var6 = new ResourceLocation(GsonHelper.getAsString(var1, "item"));
               var5 = (Item)Registry.ITEM.getOptional(var6).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown item id \'" + var6 + "\'");
               });
            }

            Tag<Item> var6 = null;
            if(var1.has("tag")) {
               ResourceLocation var7 = new ResourceLocation(GsonHelper.getAsString(var1, "tag"));
               var6 = ItemTags.getAllTags().getTag(var7);
               if(var6 == null) {
                  throw new JsonSyntaxException("Unknown item tag \'" + var7 + "\'");
               }
            }

            EnchantmentPredicate[] vars7 = EnchantmentPredicate.fromJsonArray(var1.get("enchantments"));
            Potion var8 = null;
            if(var1.has("potion")) {
               ResourceLocation var9 = new ResourceLocation(GsonHelper.getAsString(var1, "potion"));
               var8 = (Potion)Registry.POTION.getOptional(var9).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown potion \'" + var9 + "\'");
               });
            }

            return new ItemPredicate(var6, var5, var2, var3, vars7, var8, var4);
         }
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if(this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         if(this.item != null) {
            var1.addProperty("item", Registry.ITEM.getKey(this.item).toString());
         }

         if(this.tag != null) {
            var1.addProperty("tag", this.tag.getId().toString());
         }

         var1.add("count", this.count.serializeToJson());
         var1.add("durability", this.durability.serializeToJson());
         var1.add("nbt", this.nbt.serializeToJson());
         if(this.enchantments.length > 0) {
            JsonArray var2 = new JsonArray();

            for(EnchantmentPredicate var6 : this.enchantments) {
               var2.add(var6.serializeToJson());
            }

            var1.add("enchantments", var2);
         }

         if(this.potion != null) {
            var1.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
         }

         return var1;
      }
   }

   public static ItemPredicate[] fromJsonArray(@Nullable JsonElement jsonArray) {
      if(jsonArray != null && !jsonArray.isJsonNull()) {
         JsonArray var1 = GsonHelper.convertToJsonArray(jsonArray, "items");
         ItemPredicate[] vars2 = new ItemPredicate[var1.size()];

         for(int var3 = 0; var3 < vars2.length; ++var3) {
            vars2[var3] = fromJson(var1.get(var3));
         }

         return vars2;
      } else {
         return new ItemPredicate[0];
      }
   }

   public static class Builder {
      private final List enchantments = Lists.newArrayList();
      @Nullable
      private Item item;
      @Nullable
      private Tag tag;
      private MinMaxBounds.Ints count = MinMaxBounds.Ints.ANY;
      private MinMaxBounds.Ints durability = MinMaxBounds.Ints.ANY;
      @Nullable
      private Potion potion;
      private NbtPredicate nbt = NbtPredicate.ANY;

      public static ItemPredicate.Builder item() {
         return new ItemPredicate.Builder();
      }

      public ItemPredicate.Builder of(ItemLike itemLike) {
         this.item = itemLike.asItem();
         return this;
      }

      public ItemPredicate.Builder of(Tag tag) {
         this.tag = tag;
         return this;
      }

      public ItemPredicate.Builder withCount(MinMaxBounds.Ints count) {
         this.count = count;
         return this;
      }

      public ItemPredicate.Builder hasNbt(CompoundTag compoundTag) {
         this.nbt = new NbtPredicate(compoundTag);
         return this;
      }

      public ItemPredicate.Builder hasEnchantment(EnchantmentPredicate enchantmentPredicate) {
         this.enchantments.add(enchantmentPredicate);
         return this;
      }

      public ItemPredicate build() {
         return new ItemPredicate(this.tag, this.item, this.count, this.durability, (EnchantmentPredicate[])this.enchantments.toArray(new EnchantmentPredicate[0]), this.potion, this.nbt);
      }
   }
}
