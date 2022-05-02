package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class FurnaceFuelSlot extends Slot {
   private final AbstractFurnaceMenu menu;

   public FurnaceFuelSlot(AbstractFurnaceMenu menu, Container container, int var3, int var4, int var5) {
      super(container, var3, var4, var5);
      this.menu = menu;
   }

   public boolean mayPlace(ItemStack itemStack) {
      return this.menu.isFuel(itemStack) || isBucket(itemStack);
   }

   public int getMaxStackSize(ItemStack itemStack) {
      return isBucket(itemStack)?1:super.getMaxStackSize(itemStack);
   }

   public static boolean isBucket(ItemStack itemStack) {
      return itemStack.getItem() == Items.BUCKET;
   }
}
