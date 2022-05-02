package net.minecraft.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class EnchantedGoldenAppleItem extends Item {
   public EnchantedGoldenAppleItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public boolean isFoil(ItemStack itemStack) {
      return true;
   }
}
