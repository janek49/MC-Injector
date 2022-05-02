package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DiggingEnchantment extends Enchantment {
   protected DiggingEnchantment(Enchantment.Rarity enchantment$Rarity, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, EnchantmentCategory.DIGGER, equipmentSlots);
   }

   public int getMinCost(int i) {
      return 1 + 10 * (i - 1);
   }

   public int getMaxCost(int i) {
      return super.getMinCost(i) + 50;
   }

   public int getMaxLevel() {
      return 5;
   }

   public boolean canEnchant(ItemStack itemStack) {
      return itemStack.getItem() == Items.SHEARS?true:super.canEnchant(itemStack);
   }
}
