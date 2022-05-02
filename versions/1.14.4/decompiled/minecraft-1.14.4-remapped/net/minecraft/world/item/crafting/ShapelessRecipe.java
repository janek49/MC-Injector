package net.minecraft.world.item.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class ShapelessRecipe implements CraftingRecipe {
   private final ResourceLocation id;
   private final String group;
   private final ItemStack result;
   private final NonNullList ingredients;

   public ShapelessRecipe(ResourceLocation id, String group, ItemStack result, NonNullList ingredients) {
      this.id = id;
      this.group = group;
      this.result = result;
      this.ingredients = ingredients;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.SHAPELESS_RECIPE;
   }

   public String getGroup() {
      return this.group;
   }

   public ItemStack getResultItem() {
      return this.result;
   }

   public NonNullList getIngredients() {
      return this.ingredients;
   }

   public boolean matches(CraftingContainer craftingContainer, Level level) {
      StackedContents var3 = new StackedContents();
      int var4 = 0;

      for(int var5 = 0; var5 < craftingContainer.getContainerSize(); ++var5) {
         ItemStack var6 = craftingContainer.getItem(var5);
         if(!var6.isEmpty()) {
            ++var4;
            var3.accountStack(var6, 1);
         }
      }

      return var4 == this.ingredients.size() && var3.canCraft(this, (IntList)null);
   }

   public ItemStack assemble(CraftingContainer craftingContainer) {
      return this.result.copy();
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= this.ingredients.size();
   }

   public static class Serializer implements RecipeSerializer {
      public ShapelessRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
         String var3 = GsonHelper.getAsString(jsonObject, "group", "");
         NonNullList<Ingredient> var4 = itemsFromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredients"));
         if(var4.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
         } else if(var4.size() > 9) {
            throw new JsonParseException("Too many ingredients for shapeless recipe");
         } else {
            ItemStack var5 = ShapedRecipe.itemFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
            return new ShapelessRecipe(resourceLocation, var3, var5, var4);
         }
      }

      private static NonNullList itemsFromJson(JsonArray jsonArray) {
         NonNullList<Ingredient> nonNullList = NonNullList.create();

         for(int var2 = 0; var2 < jsonArray.size(); ++var2) {
            Ingredient var3 = Ingredient.fromJson(jsonArray.get(var2));
            if(!var3.isEmpty()) {
               nonNullList.add(var3);
            }
         }

         return nonNullList;
      }

      public ShapelessRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
         String var3 = friendlyByteBuf.readUtf(32767);
         int var4 = friendlyByteBuf.readVarInt();
         NonNullList<Ingredient> var5 = NonNullList.withSize(var4, Ingredient.EMPTY);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            var5.set(var6, Ingredient.fromNetwork(friendlyByteBuf));
         }

         ItemStack var6 = friendlyByteBuf.readItem();
         return new ShapelessRecipe(resourceLocation, var3, var6, var5);
      }

      public void toNetwork(FriendlyByteBuf friendlyByteBuf, ShapelessRecipe shapelessRecipe) {
         friendlyByteBuf.writeUtf(shapelessRecipe.group);
         friendlyByteBuf.writeVarInt(shapelessRecipe.ingredients.size());

         for(Ingredient var4 : shapelessRecipe.ingredients) {
            var4.toNetwork(friendlyByteBuf);
         }

         friendlyByteBuf.writeItem(shapelessRecipe.result);
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
