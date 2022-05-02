package net.minecraft.world.inventory;

import net.minecraft.recipebook.ServerPlaceSmeltingRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FurnaceFuelSlot;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public abstract class AbstractFurnaceMenu extends RecipeBookMenu {
   private final Container container;
   private final ContainerData data;
   protected final Level level;
   private final RecipeType recipeType;

   protected AbstractFurnaceMenu(MenuType menuType, RecipeType recipeType, int var3, Inventory inventory) {
      this(menuType, recipeType, var3, inventory, new SimpleContainer(3), new SimpleContainerData(4));
   }

   protected AbstractFurnaceMenu(MenuType menuType, RecipeType recipeType, int var3, Inventory inventory, Container container, ContainerData data) {
      super(menuType, var3);
      this.recipeType = recipeType;
      checkContainerSize(container, 3);
      checkContainerDataCount(data, 4);
      this.container = container;
      this.data = data;
      this.level = inventory.player.level;
      this.addSlot(new Slot(container, 0, 56, 17));
      this.addSlot(new FurnaceFuelSlot(this, container, 1, 56, 53));
      this.addSlot(new FurnaceResultSlot(inventory.player, container, 2, 116, 35));

      for(int var7 = 0; var7 < 3; ++var7) {
         for(int var8 = 0; var8 < 9; ++var8) {
            this.addSlot(new Slot(inventory, var8 + var7 * 9 + 9, 8 + var8 * 18, 84 + var7 * 18));
         }
      }

      for(int var7 = 0; var7 < 9; ++var7) {
         this.addSlot(new Slot(inventory, var7, 8 + var7 * 18, 142));
      }

      this.addDataSlots(data);
   }

   public void fillCraftSlotsStackedContents(StackedContents stackedContents) {
      if(this.container instanceof StackedContentsCompatible) {
         ((StackedContentsCompatible)this.container).fillStackedContents(stackedContents);
      }

   }

   public void clearCraftingContent() {
      this.container.clearContent();
   }

   public void handlePlacement(boolean var1, Recipe recipe, ServerPlayer serverPlayer) {
      (new ServerPlaceSmeltingRecipe(this)).recipeClicked(serverPlayer, recipe, var1);
   }

   public boolean recipeMatches(Recipe recipe) {
      return recipe.matches(this.container, this.level);
   }

   public int getResultSlotIndex() {
      return 2;
   }

   public int getGridWidth() {
      return 1;
   }

   public int getGridHeight() {
      return 1;
   }

   public int getSize() {
      return 3;
   }

   public boolean stillValid(Player player) {
      return this.container.stillValid(player);
   }

   public ItemStack quickMoveStack(Player player, int var2) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if(var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         itemStack = var5.copy();
         if(var2 == 2) {
            if(!this.moveItemStackTo(var5, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, itemStack);
         } else if(var2 != 1 && var2 != 0) {
            if(this.canSmelt(var5)) {
               if(!this.moveItemStackTo(var5, 0, 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if(this.isFuel(var5)) {
               if(!this.moveItemStackTo(var5, 1, 2, false)) {
                  return ItemStack.EMPTY;
               }
            } else if(var2 >= 3 && var2 < 30) {
               if(!this.moveItemStackTo(var5, 30, 39, false)) {
                  return ItemStack.EMPTY;
               }
            } else if(var2 >= 30 && var2 < 39 && !this.moveItemStackTo(var5, 3, 30, false)) {
               return ItemStack.EMPTY;
            }
         } else if(!this.moveItemStackTo(var5, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if(var5.isEmpty()) {
            var4.set(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }

         if(var5.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(player, var5);
      }

      return itemStack;
   }

   protected boolean canSmelt(ItemStack itemStack) {
      return this.level.getRecipeManager().getRecipeFor(this.recipeType, new SimpleContainer(new ItemStack[]{itemStack}), this.level).isPresent();
   }

   protected boolean isFuel(ItemStack itemStack) {
      return AbstractFurnaceBlockEntity.isFuel(itemStack);
   }

   public int getBurnProgress() {
      int var1 = this.data.get(2);
      int var2 = this.data.get(3);
      return var2 != 0 && var1 != 0?var1 * 24 / var2:0;
   }

   public int getLitProgress() {
      int var1 = this.data.get(1);
      if(var1 == 0) {
         var1 = 200;
      }

      return this.data.get(0) * 13 / var1;
   }

   public boolean isLit() {
      return this.data.get(0) > 0;
   }
}
