package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class ShulkerBoxSlot extends Slot {
   public ShulkerBoxSlot(Container container, int var2, int var3, int var4) {
      super(container, var2, var3, var4);
   }

   public boolean mayPlace(ItemStack itemStack) {
      return !(Block.byItem(itemStack.getItem()) instanceof ShulkerBoxBlock);
   }
}
