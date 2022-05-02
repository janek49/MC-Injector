package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SimpleRecipeSerializer implements RecipeSerializer {
   private final Function constructor;

   public SimpleRecipeSerializer(Function constructor) {
      this.constructor = constructor;
   }

   public Recipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
      return (Recipe)this.constructor.apply(resourceLocation);
   }

   public Recipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
      return (Recipe)this.constructor.apply(resourceLocation);
   }

   public void toNetwork(FriendlyByteBuf friendlyByteBuf, Recipe recipe) {
   }
}
