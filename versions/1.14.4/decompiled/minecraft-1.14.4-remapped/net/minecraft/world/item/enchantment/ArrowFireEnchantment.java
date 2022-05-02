package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ArrowFireEnchantment extends Enchantment {
   public ArrowFireEnchantment(Enchantment.Rarity enchantment$Rarity, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, EnchantmentCategory.BOW, equipmentSlots);
   }

   public int getMinCost(int i) {
      return 20;
   }

   public int getMaxCost(int i) {
      return 50;
   }

   public int getMaxLevel() {
      return 1;
   }
}
