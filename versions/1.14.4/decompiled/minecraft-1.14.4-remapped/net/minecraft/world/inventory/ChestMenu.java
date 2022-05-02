package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ChestMenu extends AbstractContainerMenu {
   private final Container container;
   private final int containerRows;

   private ChestMenu(MenuType menuType, int var2, Inventory inventory, int var4) {
      this(menuType, var2, inventory, new SimpleContainer(9 * var4), var4);
   }

   public static ChestMenu oneRow(int var0, Inventory inventory) {
      return new ChestMenu(MenuType.GENERIC_9x1, var0, inventory, 1);
   }

   public static ChestMenu twoRows(int var0, Inventory inventory) {
      return new ChestMenu(MenuType.GENERIC_9x2, var0, inventory, 2);
   }

   public static ChestMenu threeRows(int var0, Inventory inventory) {
      return new ChestMenu(MenuType.GENERIC_9x3, var0, inventory, 3);
   }

   public static ChestMenu fourRows(int var0, Inventory inventory) {
      return new ChestMenu(MenuType.GENERIC_9x4, var0, inventory, 4);
   }

   public static ChestMenu fiveRows(int var0, Inventory inventory) {
      return new ChestMenu(MenuType.GENERIC_9x5, var0, inventory, 5);
   }

   public static ChestMenu sixRows(int var0, Inventory inventory) {
      return new ChestMenu(MenuType.GENERIC_9x6, var0, inventory, 6);
   }

   public static ChestMenu threeRows(int var0, Inventory inventory, Container container) {
      return new ChestMenu(MenuType.GENERIC_9x3, var0, inventory, container, 3);
   }

   public static ChestMenu sixRows(int var0, Inventory inventory, Container container) {
      return new ChestMenu(MenuType.GENERIC_9x6, var0, inventory, container, 6);
   }

   public ChestMenu(MenuType menuType, int var2, Inventory inventory, Container container, int containerRows) {
      super(menuType, var2);
      checkContainerSize(container, containerRows * 9);
      this.container = container;
      this.containerRows = containerRows;
      container.startOpen(inventory.player);
      int var6 = (this.containerRows - 4) * 18;

      for(int var7 = 0; var7 < this.containerRows; ++var7) {
         for(int var8 = 0; var8 < 9; ++var8) {
            this.addSlot(new Slot(container, var8 + var7 * 9, 8 + var8 * 18, 18 + var7 * 18));
         }
      }

      for(int var7 = 0; var7 < 3; ++var7) {
         for(int var8 = 0; var8 < 9; ++var8) {
            this.addSlot(new Slot(inventory, var8 + var7 * 9 + 9, 8 + var8 * 18, 103 + var7 * 18 + var6));
         }
      }

      for(int var7 = 0; var7 < 9; ++var7) {
         this.addSlot(new Slot(inventory, var7, 8 + var7 * 18, 161 + var6));
      }

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
         if(var2 < this.containerRows * 9) {
            if(!this.moveItemStackTo(var5, this.containerRows * 9, this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if(!this.moveItemStackTo(var5, 0, this.containerRows * 9, false)) {
            return ItemStack.EMPTY;
         }

         if(var5.isEmpty()) {
            var4.set(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }
      }

      return itemStack;
   }

   public void removed(Player player) {
      super.removed(player);
      this.container.stopOpen(player);
   }

   public Container getContainer() {
      return this.container;
   }

   public int getRowCount() {
      return this.containerRows;
   }
}
