package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DispenserMenu extends AbstractContainerMenu {
   private final Container dispenser;

   public DispenserMenu(int var1, Inventory inventory) {
      this(var1, inventory, new SimpleContainer(9));
   }

   public DispenserMenu(int var1, Inventory inventory, Container dispenser) {
      super(MenuType.GENERIC_3x3, var1);
      checkContainerSize(dispenser, 9);
      this.dispenser = dispenser;
      dispenser.startOpen(inventory.player);

      for(int var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 3; ++var5) {
            this.addSlot(new Slot(dispenser, var5 + var4 * 3, 62 + var5 * 18, 17 + var4 * 18));
         }
      }

      for(int var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new Slot(inventory, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(int var4 = 0; var4 < 9; ++var4) {
         this.addSlot(new Slot(inventory, var4, 8 + var4 * 18, 142));
      }

   }

   public boolean stillValid(Player player) {
      return this.dispenser.stillValid(player);
   }

   public ItemStack quickMoveStack(Player player, int var2) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if(var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         itemStack = var5.copy();
         if(var2 < 9) {
            if(!this.moveItemStackTo(var5, 9, 45, true)) {
               return ItemStack.EMPTY;
            }
         } else if(!this.moveItemStackTo(var5, 0, 9, false)) {
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

   public void removed(Player player) {
      super.removed(player);
      this.dispenser.stopOpen(player);
   }
}
