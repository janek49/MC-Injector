package net.minecraft.client.gui.screens.recipebook;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Set;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

@ClientJarOnly
public class SmokingRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
   protected boolean getFilteringCraftable() {
      return this.book.isSmokerFilteringCraftable();
   }

   protected void setFilteringCraftable(boolean filteringCraftable) {
      this.book.setSmokerFilteringCraftable(filteringCraftable);
   }

   protected boolean isGuiOpen() {
      return this.book.isSmokerGuiOpen();
   }

   protected void setGuiOpen(boolean guiOpen) {
      this.book.setSmokerGuiOpen(guiOpen);
   }

   protected String getRecipeFilterName() {
      return "gui.recipebook.toggleRecipes.smokable";
   }

   protected Set getFuelItems() {
      return AbstractFurnaceBlockEntity.getFuel().keySet();
   }
}
