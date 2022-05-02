package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class FishingSpeedEnchantment extends Enchantment {
   protected FishingSpeedEnchantment(Enchantment.Rarity enchantment$Rarity, EnchantmentCategory enchantmentCategory, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, enchantmentCategory, equipmentSlots);
   }

   public int getMinCost(int i) {
      return 15 + (i - 1) * 9;
   }

   public int getMaxCost(int i) {
      return super.getMinCost(i) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }
}
