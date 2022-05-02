package net.minecraft.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SimpleFoiledItem extends Item {
   public SimpleFoiledItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public boolean isFoil(ItemStack itemStack) {
      return true;
   }
}
