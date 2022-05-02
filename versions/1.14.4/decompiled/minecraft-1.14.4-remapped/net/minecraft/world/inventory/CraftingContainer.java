package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;

public class CraftingContainer implements Container, StackedContentsCompatible {
   private final NonNullList items;
   private final int width;
   private final int height;
   private final AbstractContainerMenu menu;

   public CraftingContainer(AbstractContainerMenu menu, int width, int height) {
      this.items = NonNullList.withSize(width * height, ItemStack.EMPTY);
      this.menu = menu;
      this.width = width;
      this.height = height;
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public boolean isEmpty() {
      for(ItemStack var2 : this.items) {
         if(!var2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int i) {
      return i >= this.getContainerSize()?ItemStack.EMPTY:(ItemStack)this.items.get(i);
   }

   public ItemStack removeItemNoUpdate(int i) {
      return ContainerHelper.takeItem(this.items, i);
   }

   public ItemStack removeItem(int var1, int var2) {
      ItemStack itemStack = ContainerHelper.removeItem(this.items, var1, var2);
      if(!itemStack.isEmpty()) {
         this.menu.slotsChanged(this);
      }

      return itemStack;
   }

   public void setItem(int var1, ItemStack itemStack) {
      this.items.set(var1, itemStack);
      this.menu.slotsChanged(this);
   }

   public void setChanged() {
   }

   public boolean stillValid(Player player) {
      return true;
   }

   public void clearContent() {
      this.items.clear();
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public void fillStackedContents(StackedContents stackedContents) {
      for(ItemStack var3 : this.items) {
         stackedContents.accountSimpleStack(var3);
      }

   }
}
