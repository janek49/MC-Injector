package net.minecraft.client.gui.screens.recipebook;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;

@ClientJarOnly
public interface RecipeUpdateListener {
   void recipesUpdated();

   RecipeBookComponent getRecipeBookComponent();
}
