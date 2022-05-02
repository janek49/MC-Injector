package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HorseInventoryMenu extends AbstractContainerMenu {
   private final Container horseContainer;
   private final AbstractHorse horse;

   public HorseInventoryMenu(int var1, Inventory inventory, final Container horseContainer, final AbstractHorse horse) {
      super((MenuType)null, var1);
      this.horseContainer = horseContainer;
      this.horse = horse;
      int var5 = 3;
      horseContainer.startOpen(inventory.player);
      int var6 = -18;
      this.addSlot(new Slot(horseContainer, 0, 8, 18) {
         public boolean mayPlace(ItemStack itemStack) {
            return itemStack.getItem() == Items.SADDLE && !this.hasItem() && horse.canBeSaddled();
         }

         public boolean isActive() {
            return horse.canBeSaddled();
         }
      });
      this.addSlot(new Slot(horseContainer, 1, 8, 36) {
         public boolean mayPlace(ItemStack itemStack) {
            return horse.isArmor(itemStack);
         }

         public boolean isActive() {
            return horse.wearsArmor();
         }

         public int getMaxStackSize() {
            return 1;
         }
      });
      if(horse instanceof AbstractChestedHorse && ((AbstractChestedHorse)horse).hasChest()) {
         for(int var7 = 0; var7 < 3; ++var7) {
            for(int var8 = 0; var8 < ((AbstractChestedHorse)horse).getInventoryColumns(); ++var8) {
               this.addSlot(new Slot(horseContainer, 2 + var8 + var7 * ((AbstractChestedHorse)horse).getInventoryColumns(), 80 + var8 * 18, 18 + var7 * 18));
            }
         }
      }

      for(int var7 = 0; var7 < 3; ++var7) {
         for(int var8 = 0; var8 < 9; ++var8) {
            this.addSlot(new Slot(inventory, var8 + var7 * 9 + 9, 8 + var8 * 18, 102 + var7 * 18 + -18));
         }
      }

      for(int var7 = 0; var7 < 9; ++var7) {
         this.addSlot(new Slot(inventory, var7, 8 + var7 * 18, 142));
      }

   }

   public boolean stillValid(Player player) {
      return this.horseContainer.stillValid(player) && this.horse.isAlive() && this.horse.distanceTo(player) < 8.0F;
   }

   public ItemStack quickMoveStack(Player player, int var2) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if(var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         itemStack = var5.copy();
         if(var2 < this.horseContainer.getContainerSize()) {
            if(!this.moveItemStackTo(var5, this.horseContainer.getContainerSize(), this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if(this.getSlot(1).mayPlace(var5) && !this.getSlot(1).hasItem()) {
            if(!this.moveItemStackTo(var5, 1, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if(this.getSlot(0).mayPlace(var5)) {
            if(!this.moveItemStackTo(var5, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if(this.horseContainer.getContainerSize() <= 2 || !this.moveItemStackTo(var5, 2, this.horseContainer.getContainerSize(), false)) {
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
      this.horseContainer.stopOpen(player);
   }
}
