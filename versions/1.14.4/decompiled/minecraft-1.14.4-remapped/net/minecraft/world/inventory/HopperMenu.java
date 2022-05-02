package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class HopperMenu extends AbstractContainerMenu {
   private final Container hopper;

   public HopperMenu(int var1, Inventory inventory) {
      this(var1, inventory, new SimpleContainer(5));
   }

   public HopperMenu(int var1, Inventory inventory, Container hopper) {
      super(MenuType.HOPPER, var1);
      this.hopper = hopper;
      checkContainerSize(hopper, 5);
      hopper.startOpen(inventory.player);
      int var4 = 51;

      for(int var5 = 0; var5 < 5; ++var5) {
         this.addSlot(new Slot(hopper, var5, 44 + var5 * 18, 20));
      }

      for(int var5 = 0; var5 < 3; ++var5) {
         for(int var6 = 0; var6 < 9; ++var6) {
            this.addSlot(new Slot(inventory, var6 + var5 * 9 + 9, 8 + var6 * 18, var5 * 18 + 51));
         }
      }

      for(int var5 = 0; var5 < 9; ++var5) {
         this.addSlot(new Slot(inventory, var5, 8 + var5 * 18, 109));
      }

   }

   public boolean stillValid(Player player) {
      return this.hopper.stillValid(player);
   }

   public ItemStack quickMoveStack(Player player, int var2) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if(var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         itemStack = var5.copy();
         if(var2 < this.hopper.getContainerSize()) {
            if(!this.moveItemStackTo(var5, this.hopper.getContainerSize(), this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if(!this.moveItemStackTo(var5, 0, this.hopper.getContainerSize(), false)) {
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
      this.hopper.stopOpen(player);
   }
}
