package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ShulkerBoxMenu extends AbstractContainerMenu {
   private final Container container;

   public ShulkerBoxMenu(int var1, Inventory inventory) {
      this(var1, inventory, new SimpleContainer(27));
   }

   public ShulkerBoxMenu(int var1, Inventory inventory, Container container) {
      super(MenuType.SHULKER_BOX, var1);
      checkContainerSize(container, 27);
      this.container = container;
      container.startOpen(inventory.player);
      int var4 = 3;
      int var5 = 9;

      for(int var6 = 0; var6 < 3; ++var6) {
         for(int var7 = 0; var7 < 9; ++var7) {
            this.addSlot(new ShulkerBoxSlot(container, var7 + var6 * 9, 8 + var7 * 18, 18 + var6 * 18));
         }
      }

      for(int var6 = 0; var6 < 3; ++var6) {
         for(int var7 = 0; var7 < 9; ++var7) {
            this.addSlot(new Slot(inventory, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
         }
      }

      for(int var6 = 0; var6 < 9; ++var6) {
         this.addSlot(new Slot(inventory, var6, 8 + var6 * 18, 142));
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
         if(var2 < this.container.getContainerSize()) {
            if(!this.moveItemStackTo(var5, this.container.getContainerSize(), this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if(!this.moveItemStackTo(var5, 0, this.container.getContainerSize(), false)) {
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
}
