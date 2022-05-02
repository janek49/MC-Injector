package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class MendingEnchantment extends Enchantment {
   public MendingEnchantment(Enchantment.Rarity enchantment$Rarity, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, EnchantmentCategory.BREAKABLE, equipmentSlots);
   }

   public int getMinCost(int i) {
      return i * 25;
   }

   public int getMaxCost(int i) {
      return this.getMinCost(i) + 50;
   }

   public boolean isTreasureOnly() {
      return true;
   }

   public int getMaxLevel() {
      return 1;
   }
}
