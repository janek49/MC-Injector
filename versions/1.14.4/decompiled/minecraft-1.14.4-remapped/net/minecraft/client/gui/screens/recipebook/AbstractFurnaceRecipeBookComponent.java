package net.minecraft.client.gui.screens.recipebook;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

@ClientJarOnly
public abstract class AbstractFurnaceRecipeBookComponent extends RecipeBookComponent {
   private Iterator iterator;
   private Set fuels;
   private Slot fuelSlot;
   private Item fuel;
   private float time;

   protected boolean updateFiltering() {
      boolean var1 = !this.getFilteringCraftable();
      this.setFilteringCraftable(var1);
      return var1;
   }

   protected abstract boolean getFilteringCraftable();

   protected abstract void setFilteringCraftable(boolean var1);

   public boolean isVisible() {
      return this.isGuiOpen();
   }

   protected abstract boolean isGuiOpen();

   protected void setVisible(boolean visible) {
      this.setGuiOpen(visible);
      if(!visible) {
         this.recipeBookPage.setInvisible();
      }

      this.sendUpdateSettings();
   }

   protected abstract void setGuiOpen(boolean var1);

   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(152, 182, 28, 18, RECIPE_BOOK_LOCATION);
   }

   protected String getFilterButtonTooltip() {
      return I18n.get(this.filterButton.isStateTriggered()?this.getRecipeFilterName():"gui.recipebook.toggleRecipes.all", new Object[0]);
   }

   protected abstract String getRecipeFilterName();

   public void slotClicked(@Nullable Slot slot) {
      super.slotClicked(slot);
      if(slot != null && slot.index < this.menu.getSize()) {
         this.fuelSlot = null;
      }

   }

   public void setupGhostRecipe(Recipe recipe, List list) {
      ItemStack var3 = recipe.getResultItem();
      this.ghostRecipe.setRecipe(recipe);
      this.ghostRecipe.addIngredient(Ingredient.of(new ItemStack[]{var3}), ((Slot)list.get(2)).x, ((Slot)list.get(2)).y);
      NonNullList<Ingredient> var4 = recipe.getIngredients();
      this.fuelSlot = (Slot)list.get(1);
      if(this.fuels == null) {
         this.fuels = this.getFuelItems();
      }

      this.iterator = this.fuels.iterator();
      this.fuel = null;
      Iterator<Ingredient> var5 = var4.iterator();

      for(int var6 = 0; var6 < 2; ++var6) {
         if(!var5.hasNext()) {
            return;
         }

         Ingredient var7 = (Ingredient)var5.next();
         if(!var7.isEmpty()) {
            Slot var8 = (Slot)list.get(var6);
            this.ghostRecipe.addIngredient(var7, var8.x, var8.y);
         }
      }

   }

   protected abstract Set getFuelItems();

   public void renderGhostRecipe(int var1, int var2, boolean var3, float var4) {
      super.renderGhostRecipe(var1, var2, var3, var4);
      if(this.fuelSlot != null) {
         if(!Screen.hasControlDown()) {
            this.time += var4;
         }

         Lighting.turnOnGui();
         GlStateManager.disableLighting();
         int var5 = this.fuelSlot.x + var1;
         int var6 = this.fuelSlot.y + var2;
         GuiComponent.fill(var5, var6, var5 + 16, var6 + 16, 822018048);
         this.minecraft.getItemRenderer().renderAndDecorateItem(this.minecraft.player, this.getFuel().getDefaultInstance(), var5, var6);
         GlStateManager.depthFunc(516);
         GuiComponent.fill(var5, var6, var5 + 16, var6 + 16, 822083583);
         GlStateManager.depthFunc(515);
         GlStateManager.enableLighting();
         Lighting.turnOff();
      }
   }

   private Item getFuel() {
      if(this.fuel == null || this.time > 30.0F) {
         this.time = 0.0F;
         if(this.iterator == null || !this.iterator.hasNext()) {
            if(this.fuels == null) {
               this.fuels = this.getFuelItems();
            }

            this.iterator = this.fuels.iterator();
         }

         this.fuel = (Item)this.iterator.next();
      }

      return this.fuel;
   }
}
