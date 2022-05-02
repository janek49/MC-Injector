package net.minecraft.world.inventory;

import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Recipe;

public abstract class RecipeBookMenu extends AbstractContainerMenu {
   public RecipeBookMenu(MenuType menuType, int var2) {
      super(menuType, var2);
   }

   public void handlePlacement(boolean var1, Recipe recipe, ServerPlayer serverPlayer) {
      (new ServerPlaceRecipe(this)).recipeClicked(serverPlayer, recipe, var1);
   }

   public abstract void fillCraftSlotsStackedContents(StackedContents var1);

   public abstract void clearCraftingContent();

   public abstract boolean recipeMatches(Recipe var1);

   public abstract int getResultSlotIndex();

   public abstract int getGridWidth();

   public abstract int getGridHeight();

   public abstract int getSize();
}
