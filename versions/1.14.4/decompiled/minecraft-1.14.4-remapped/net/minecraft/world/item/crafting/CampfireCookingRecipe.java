package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;

public class CampfireCookingRecipe extends AbstractCookingRecipe {
   public CampfireCookingRecipe(ResourceLocation resourceLocation, String string, Ingredient ingredient, ItemStack itemStack, float var5, int var6) {
      super(RecipeType.CAMPFIRE_COOKING, resourceLocation, string, ingredient, itemStack, var5, var6);
   }

   public ItemStack getToastSymbol() {
      return new ItemStack(Blocks.CAMPFIRE);
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.CAMPFIRE_COOKING_RECIPE;
   }
}
