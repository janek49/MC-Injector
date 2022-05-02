package net.minecraft.world.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ShapedRecipe implements CraftingRecipe {
   private final int width;
   private final int height;
   private final NonNullList recipeItems;
   private final ItemStack result;
   private final ResourceLocation id;
   private final String group;

   public ShapedRecipe(ResourceLocation id, String group, int width, int height, NonNullList recipeItems, ItemStack result) {
      this.id = id;
      this.group = group;
      this.width = width;
      this.height = height;
      this.recipeItems = recipeItems;
      this.result = result;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.SHAPED_RECIPE;
   }

   public String getGroup() {
      return this.group;
   }

   public ItemStack getResultItem() {
      return this.result;
   }

   public NonNullList getIngredients() {
      return this.recipeItems;
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 >= this.width && var2 >= this.height;
   }

   public boolean matches(CraftingContainer craftingContainer, Level level) {
      for(int var3 = 0; var3 <= craftingContainer.getWidth() - this.width; ++var3) {
         for(int var4 = 0; var4 <= craftingContainer.getHeight() - this.height; ++var4) {
            if(this.matches(craftingContainer, var3, var4, true)) {
               return true;
            }

            if(this.matches(craftingContainer, var3, var4, false)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean matches(CraftingContainer craftingContainer, int var2, int var3, boolean var4) {
      for(int var5 = 0; var5 < craftingContainer.getWidth(); ++var5) {
         for(int var6 = 0; var6 < craftingContainer.getHeight(); ++var6) {
            int var7 = var5 - var2;
            int var8 = var6 - var3;
            Ingredient var9 = Ingredient.EMPTY;
            if(var7 >= 0 && var8 >= 0 && var7 < this.width && var8 < this.height) {
               if(var4) {
                  var9 = (Ingredient)this.recipeItems.get(this.width - var7 - 1 + var8 * this.width);
               } else {
                  var9 = (Ingredient)this.recipeItems.get(var7 + var8 * this.width);
               }
            }

            if(!var9.test(craftingContainer.getItem(var5 + var6 * craftingContainer.getWidth()))) {
               return false;
            }
         }
      }

      return true;
   }

   public ItemStack assemble(CraftingContainer craftingContainer) {
      return this.getResultItem().copy();
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   private static NonNullList dissolvePattern(String[] strings, Map map, int var2, int var3) {
      NonNullList<Ingredient> nonNullList = NonNullList.withSize(var2 * var3, Ingredient.EMPTY);
      Set<String> var5 = Sets.newHashSet(map.keySet());
      var5.remove(" ");

      for(int var6 = 0; var6 < strings.length; ++var6) {
         for(int var7 = 0; var7 < strings[var6].length(); ++var7) {
            String var8 = strings[var6].substring(var7, var7 + 1);
            Ingredient var9 = (Ingredient)map.get(var8);
            if(var9 == null) {
               throw new JsonSyntaxException("Pattern references symbol \'" + var8 + "\' but it\'s not defined in the key");
            }

            var5.remove(var8);
            nonNullList.set(var7 + var2 * var6, var9);
         }
      }

      if(!var5.isEmpty()) {
         throw new JsonSyntaxException("Key defines symbols that aren\'t used in pattern: " + var5);
      } else {
         return nonNullList;
      }
   }

   @VisibleForTesting
   static String[] shrink(String... strings) {
      int var1 = Integer.MAX_VALUE;
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;

      for(int var5 = 0; var5 < strings.length; ++var5) {
         String var6 = strings[var5];
         var1 = Math.min(var1, firstNonSpace(var6));
         int var7 = lastNonSpace(var6);
         var2 = Math.max(var2, var7);
         if(var7 < 0) {
            if(var3 == var5) {
               ++var3;
            }

            ++var4;
         } else {
            var4 = 0;
         }
      }

      if(strings.length == var4) {
         return new String[0];
      } else {
         String[] vars5 = new String[strings.length - var4 - var3];

         for(int var6 = 0; var6 < vars5.length; ++var6) {
            vars5[var6] = strings[var6 + var3].substring(var1, var2 + 1);
         }

         return vars5;
      }
   }

   private static int firstNonSpace(String string) {
      int var1;
      for(var1 = 0; var1 < string.length() && string.charAt(var1) == 32; ++var1) {
         ;
      }

      return var1;
   }

   private static int lastNonSpace(String string) {
      int var1;
      for(var1 = string.length() - 1; var1 >= 0 && string.charAt(var1) == 32; --var1) {
         ;
      }

      return var1;
   }

   private static String[] patternFromJson(JsonArray jsonArray) {
      String[] strings = new String[jsonArray.size()];
      if(strings.length > 3) {
         throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
      } else if(strings.length == 0) {
         throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
      } else {
         for(int var2 = 0; var2 < strings.length; ++var2) {
            String var3 = GsonHelper.convertToString(jsonArray.get(var2), "pattern[" + var2 + "]");
            if(var3.length() > 3) {
               throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
            }

            if(var2 > 0 && strings[0].length() != var3.length()) {
               throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }

            strings[var2] = var3;
         }

         return strings;
      }
   }

   private static Map keyFromJson(JsonObject jsonObject) {
      Map<String, Ingredient> map = Maps.newHashMap();

      for(Entry<String, JsonElement> var3 : jsonObject.entrySet()) {
         if(((String)var3.getKey()).length() != 1) {
            throw new JsonSyntaxException("Invalid key entry: \'" + (String)var3.getKey() + "\' is an invalid symbol (must be 1 character only).");
         }

         if(" ".equals(var3.getKey())) {
            throw new JsonSyntaxException("Invalid key entry: \' \' is a reserved symbol.");
         }

         map.put(var3.getKey(), Ingredient.fromJson((JsonElement)var3.getValue()));
      }

      map.put(" ", Ingredient.EMPTY);
      return map;
   }

   public static ItemStack itemFromJson(JsonObject jsonObject) {
      String var1 = GsonHelper.getAsString(jsonObject, "item");
      Item var2 = (Item)Registry.ITEM.getOptional(new ResourceLocation(var1)).orElseThrow(() -> {
         return new JsonSyntaxException("Unknown item \'" + var1 + "\'");
      });
      if(jsonObject.has("data")) {
         throw new JsonParseException("Disallowed data tag found");
      } else {
         int var3 = GsonHelper.getAsInt(jsonObject, "count", 1);
         return new ItemStack(var2, var3);
      }
   }

   public static class Serializer implements RecipeSerializer {
      public ShapedRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
         String var3 = GsonHelper.getAsString(jsonObject, "group", "");
         Map<String, Ingredient> var4 = ShapedRecipe.keyFromJson(GsonHelper.getAsJsonObject(jsonObject, "key"));
         String[] vars5 = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(GsonHelper.getAsJsonArray(jsonObject, "pattern")));
         int var6 = vars5[0].length();
         int var7 = vars5.length;
         NonNullList<Ingredient> var8 = ShapedRecipe.dissolvePattern(vars5, var4, var6, var7);
         ItemStack var9 = ShapedRecipe.itemFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
         return new ShapedRecipe(resourceLocation, var3, var6, var7, var8, var9);
      }

      public ShapedRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
         int var3 = friendlyByteBuf.readVarInt();
         int var4 = friendlyByteBuf.readVarInt();
         String var5 = friendlyByteBuf.readUtf(32767);
         NonNullList<Ingredient> var6 = NonNullList.withSize(var3 * var4, Ingredient.EMPTY);

         for(int var7 = 0; var7 < var6.size(); ++var7) {
            var6.set(var7, Ingredient.fromNetwork(friendlyByteBuf));
         }

         ItemStack var7 = friendlyByteBuf.readItem();
         return new ShapedRecipe(resourceLocation, var5, var3, var4, var6, var7);
      }

      public void toNetwork(FriendlyByteBuf friendlyByteBuf, ShapedRecipe shapedRecipe) {
         friendlyByteBuf.writeVarInt(shapedRecipe.width);
         friendlyByteBuf.writeVarInt(shapedRecipe.height);
         friendlyByteBuf.writeUtf(shapedRecipe.group);

         for(Ingredient var4 : shapedRecipe.recipeItems) {
            var4.toNetwork(friendlyByteBuf);
         }

         friendlyByteBuf.writeItem(shapedRecipe.result);
      }

      // $FF: synthetic method
      public Recipe fromNetwork(ResourceLocation var1, FriendlyByteBuf var2) {
         return this.fromNetwork(var1, var2);
      }

      // $FF: synthetic method
      public Recipe fromJson(ResourceLocation var1, JsonObject var2) {
         return this.fromJson(var1, var2);
      }
   }
}
