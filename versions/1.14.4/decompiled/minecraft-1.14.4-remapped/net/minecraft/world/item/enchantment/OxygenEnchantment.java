package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class OxygenEnchantment extends Enchantment {
   public OxygenEnchantment(Enchantment.Rarity enchantment$Rarity, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, EnchantmentCategory.ARMOR_HEAD, equipmentSlots);
   }

   public int getMinCost(int i) {
      return 10 * i;
   }

   public int getMaxCost(int i) {
      return this.getMinCost(i) + 30;
   }

   public int getMaxLevel() {
      return 3;
   }
}
