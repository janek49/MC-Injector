package net.minecraft.client.gui.screens.recipebook;

import com.fox2code.repacker.ClientJarOnly;
import java.util.List;

@ClientJarOnly
public interface RecipeShownListener {
   void recipesShown(List var1);
}
