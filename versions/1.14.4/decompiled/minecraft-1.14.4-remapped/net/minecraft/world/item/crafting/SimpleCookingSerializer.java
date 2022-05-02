package net.minecraft.world.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class SimpleCookingSerializer implements RecipeSerializer {
   private final int defaultCookingTime;
   private final SimpleCookingSerializer.CookieBaker factory;

   public SimpleCookingSerializer(SimpleCookingSerializer.CookieBaker factory, int defaultCookingTime) {
      this.defaultCookingTime = defaultCookingTime;
      this.factory = factory;
   }

   public AbstractCookingRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
      String var3 = GsonHelper.getAsString(jsonObject, "group", "");
      JsonElement var4 = (JsonElement)(GsonHelper.isArrayNode(jsonObject, "ingredient")?GsonHelper.getAsJsonArray(jsonObject, "ingredient"):GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
      Ingredient var5 = Ingredient.fromJson(var4);
      String var6 = GsonHelper.getAsString(jsonObject, "result");
      ResourceLocation var7 = new ResourceLocation(var6);
      ItemStack var8 = new ItemStack((ItemLike)Registry.ITEM.getOptional(var7).orElseThrow(() -> {
         return new IllegalStateException("Item: " + var6 + " does not exist");
      }));
      float var9 = GsonHelper.getAsFloat(jsonObject, "experience", 0.0F);
      int var10 = GsonHelper.getAsInt(jsonObject, "cookingtime", this.defaultCookingTime);
      return this.factory.create(resourceLocation, var3, var5, var8, var9, var10);
   }

   public AbstractCookingRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
      String var3 = friendlyByteBuf.readUtf(32767);
      Ingredient var4 = Ingredient.fromNetwork(friendlyByteBuf);
      ItemStack var5 = friendlyByteBuf.readItem();
      float var6 = friendlyByteBuf.readFloat();
      int var7 = friendlyByteBuf.readVarInt();
      return this.factory.create(resourceLocation, var3, var4, var5, var6, var7);
   }

   public void toNetwork(FriendlyByteBuf friendlyByteBuf, AbstractCookingRecipe abstractCookingRecipe) {
      friendlyByteBuf.writeUtf(abstractCookingRecipe.group);
      abstractCookingRecipe.ingredient.toNetwork(friendlyByteBuf);
      friendlyByteBuf.writeItem(abstractCookingRecipe.result);
      friendlyByteBuf.writeFloat(abstractCookingRecipe.experience);
      friendlyByteBuf.writeVarInt(abstractCookingRecipe.cookingTime);
   }

   // $FF: synthetic method
   public Recipe fromNetwork(ResourceLocation var1, FriendlyByteBuf var2) {
      return this.fromNetwork(var1, var2);
   }

   // $FF: synthetic method
   public Recipe fromJson(ResourceLocation var1, JsonObject var2) {
      return this.fromJson(var1, var2);
   }

   interface CookieBaker {
      AbstractCookingRecipe create(ResourceLocation var1, String var2, Ingredient var3, ItemStack var4, float var5, int var6);
   }
}
