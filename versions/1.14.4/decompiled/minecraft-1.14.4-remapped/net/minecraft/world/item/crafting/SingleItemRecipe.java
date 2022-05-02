package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

public abstract class SingleItemRecipe implements Recipe {
   protected final Ingredient ingredient;
   protected final ItemStack result;
   private final RecipeType type;
   private final RecipeSerializer serializer;
   protected final ResourceLocation id;
   protected final String group;

   public SingleItemRecipe(RecipeType type, RecipeSerializer serializer, ResourceLocation id, String group, Ingredient ingredient, ItemStack result) {
      this.type = type;
      this.serializer = serializer;
      this.id = id;
      this.group = group;
      this.ingredient = ingredient;
      this.result = result;
   }

   public RecipeType getType() {
      return this.type;
   }

   public RecipeSerializer getSerializer() {
      return this.serializer;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public String getGroup() {
      return this.group;
   }

   public ItemStack getResultItem() {
      return this.result;
   }

   public NonNullList getIngredients() {
      NonNullList<Ingredient> nonNullList = NonNullList.create();
      nonNullList.add(this.ingredient);
      return nonNullList;
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return true;
   }

   public ItemStack assemble(Container container) {
      return this.result.copy();
   }

   public static class Serializer implements RecipeSerializer {
      final SingleItemRecipe.Serializer.SingleItemMaker factory;

      protected Serializer(SingleItemRecipe.Serializer.SingleItemMaker factory) {
         this.factory = factory;
      }

      public SingleItemRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
         String var3 = GsonHelper.getAsString(jsonObject, "group", "");
         Ingredient var4;
         if(GsonHelper.isArrayNode(jsonObject, "ingredient")) {
            var4 = Ingredient.fromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredient"));
         } else {
            var4 = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
         }

         String var5 = GsonHelper.getAsString(jsonObject, "result");
         int var6 = GsonHelper.getAsInt(jsonObject, "count");
         ItemStack var7 = new ItemStack((ItemLike)Registry.ITEM.get(new ResourceLocation(var5)), var6);
         return this.factory.create(resourceLocation, var3, var4, var7);
      }

      public SingleItemRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
         String var3 = friendlyByteBuf.readUtf(32767);
         Ingredient var4 = Ingredient.fromNetwork(friendlyByteBuf);
         ItemStack var5 = friendlyByteBuf.readItem();
         return this.factory.create(resourceLocation, var3, var4, var5);
      }

      public void toNetwork(FriendlyByteBuf friendlyByteBuf, SingleItemRecipe singleItemRecipe) {
         friendlyByteBuf.writeUtf(singleItemRecipe.group);
         singleItemRecipe.ingredient.toNetwork(friendlyByteBuf);
         friendlyByteBuf.writeItem(singleItemRecipe.result);
      }

      // $FF: synthetic method
      public Recipe fromNetwork(ResourceLocation var1, FriendlyByteBuf var2) {
         return this.fromNetwork(var1, var2);
      }

      // $FF: synthetic method
      public Recipe fromJson(ResourceLocation var1, JsonObject var2) {
         return this.fromJson(var1, var2);
      }

      interface SingleItemMaker {
         SingleItemRecipe create(ResourceLocation var1, String var2, Ingredient var3, ItemStack var4);
      }
   }
}
