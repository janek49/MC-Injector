package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LecternMenu extends AbstractContainerMenu {
   private final Container lectern;
   private final ContainerData lecternData;

   public LecternMenu(int i) {
      this(i, new SimpleContainer(1), new SimpleContainerData(1));
   }

   public LecternMenu(int var1, final Container lectern, ContainerData lecternData) {
      super(MenuType.LECTERN, var1);
      checkContainerSize(lectern, 1);
      checkContainerDataCount(lecternData, 1);
      this.lectern = lectern;
      this.lecternData = lecternData;
      this.addSlot(new Slot(lectern, 0, 0, 0) {
         public void setChanged() {
            super.setChanged();
            LecternMenu.this.slotsChanged(this.container);
         }
      });
      this.addDataSlots(lecternData);
   }

   public boolean clickMenuButton(Player player, int var2) {
      if(var2 >= 100) {
         int var3 = var2 - 100;
         this.setData(0, var3);
         return true;
      } else {
         switch(var2) {
         case 1:
            int var3 = this.lecternData.get(0);
            this.setData(0, var3 - 1);
            return true;
         case 2:
            int var3 = this.lecternData.get(0);
            this.setData(0, var3 + 1);
            return true;
         case 3:
            if(!player.mayBuild()) {
               return false;
            }

            ItemStack var3 = this.lectern.removeItemNoUpdate(0);
            this.lectern.setChanged();
            if(!player.inventory.add(var3)) {
               player.drop(var3, false);
            }

            return true;
         default:
            return false;
         }
      }
   }

   public void setData(int var1, int var2) {
      super.setData(var1, var2);
      this.broadcastChanges();
   }

   public boolean stillValid(Player player) {
      return this.lectern.stillValid(player);
   }

   public ItemStack getBook() {
      return this.lectern.getItem(0);
   }

   public int getPage() {
      return this.lecternData.get(0);
   }
}
