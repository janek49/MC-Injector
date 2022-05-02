package net.minecraft.client.gui.screens.recipebook;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Set;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

@ClientJarOnly
public class BlastingRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
   protected boolean getFilteringCraftable() {
      return this.book.isBlastingFurnaceFilteringCraftable();
   }

   protected void setFilteringCraftable(boolean filteringCraftable) {
      this.book.setBlastingFurnaceFilteringCraftable(filteringCraftable);
   }

   protected boolean isGuiOpen() {
      return this.book.isBlastingFurnaceGuiOpen();
   }

   protected void setGuiOpen(boolean guiOpen) {
      this.book.setBlastingFurnaceGuiOpen(guiOpen);
   }

   protected String getRecipeFilterName() {
      return "gui.recipebook.toggleRecipes.blastable";
   }

   protected Set getFuelItems() {
      return AbstractFurnaceBlockEntity.getFuel().keySet();
   }
}
