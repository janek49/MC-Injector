package net.minecraft.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BookItem extends Item {
   public BookItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public boolean isEnchantable(ItemStack itemStack) {
      return itemStack.getCount() == 1;
   }

   public int getEnchantmentValue() {
      return 1;
   }
}
