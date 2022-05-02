package net.minecraft.world.item.crafting;

import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public interface RecipeType {
   RecipeType CRAFTING = register("crafting");
   RecipeType SMELTING = register("smelting");
   RecipeType BLASTING = register("blasting");
   RecipeType SMOKING = register("smoking");
   RecipeType CAMPFIRE_COOKING = register("campfire_cooking");
   RecipeType STONECUTTING = register("stonecutting");

   static default RecipeType register(final String string) {
      return (RecipeType)Registry.register(Registry.RECIPE_TYPE, (ResourceLocation)(new ResourceLocation(string)), new RecipeType() {
         public String toString() {
            return string;
         }
      });
   }

   default Optional tryMatch(Recipe recipe, Level level, Container container) {
      return recipe.matches(container, level)?Optional.of(recipe):Optional.empty();
   }
}
