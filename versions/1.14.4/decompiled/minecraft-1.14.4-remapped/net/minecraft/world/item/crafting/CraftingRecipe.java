package net.minecraft.world.item.crafting;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface CraftingRecipe extends Recipe {
   default RecipeType getType() {
      return RecipeType.CRAFTING;
   }
}
