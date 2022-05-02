package net.minecraft.client.gui.screens.recipebook;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Set;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

@ClientJarOnly
public class SmeltingRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
   protected boolean getFilteringCraftable() {
      return this.book.isFurnaceFilteringCraftable();
   }

   protected void setFilteringCraftable(boolean filteringCraftable) {
      this.book.setFurnaceFilteringCraftable(filteringCraftable);
   }

   protected boolean isGuiOpen() {
      return this.book.isFurnaceGuiOpen();
   }

   protected void setGuiOpen(boolean guiOpen) {
      this.book.setFurnaceGuiOpen(guiOpen);
   }

   protected String getRecipeFilterName() {
      return "gui.recipebook.toggleRecipes.smeltable";
   }

   protected Set getFuelItems() {
      return AbstractFurnaceBlockEntity.getFuel().keySet();
   }
}
