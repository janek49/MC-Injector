package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentPredicate {
   public static final EnchantmentPredicate ANY = new EnchantmentPredicate();
   private final Enchantment enchantment;
   private final MinMaxBounds.Ints level;

   public EnchantmentPredicate() {
      this.enchantment = null;
      this.level = MinMaxBounds.Ints.ANY;
   }

   public EnchantmentPredicate(@Nullable Enchantment enchantment, MinMaxBounds.Ints level) {
      this.enchantment = enchantment;
      this.level = level;
   }

   public boolean containedIn(Map map) {
      if(this.enchantment != null) {
         if(!map.containsKey(this.enchantment)) {
            return false;
         }

         int var2 = ((Integer)map.get(this.enchantment)).intValue();
         if(this.level != null && !this.level.matches(var2)) {
            return false;
         }
      } else if(this.level != null) {
         for(Integer var3 : map.values()) {
            if(this.level.matches(var3.intValue())) {
               return true;
            }
         }

         return false;
      }

      return true;
   }

   public JsonElement serializeToJson() {
      if(this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         if(this.enchantment != null) {
            var1.addProperty("enchantment", Registry.ENCHANTMENT.getKey(this.enchantment).toString());
         }

         var1.add("levels", this.level.serializeToJson());
         return var1;
      }
   }

   public static EnchantmentPredicate fromJson(@Nullable JsonElement json) {
      if(json != null && !json.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(json, "enchantment");
         Enchantment var2 = null;
         if(var1.has("enchantment")) {
            ResourceLocation var3 = new ResourceLocation(GsonHelper.getAsString(var1, "enchantment"));
            var2 = (Enchantment)Registry.ENCHANTMENT.getOptional(var3).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown enchantment \'" + var3 + "\'");
            });
         }

         MinMaxBounds.Ints var3 = MinMaxBounds.Ints.fromJson(var1.get("levels"));
         return new EnchantmentPredicate(var2, var3);
      } else {
         return ANY;
      }
   }

   public static EnchantmentPredicate[] fromJsonArray(@Nullable JsonElement jsonArray) {
      if(jsonArray != null && !jsonArray.isJsonNull()) {
         JsonArray var1 = GsonHelper.convertToJsonArray(jsonArray, "enchantments");
         EnchantmentPredicate[] vars2 = new EnchantmentPredicate[var1.size()];

         for(int var3 = 0; var3 < vars2.length; ++var3) {
            vars2[var3] = fromJson(var1.get(var3));
         }

         return vars2;
      } else {
         return new EnchantmentPredicate[0];
      }
   }
}
