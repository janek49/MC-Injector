package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ArrowDamageEnchantment extends Enchantment {
   public ArrowDamageEnchantment(Enchantment.Rarity enchantment$Rarity, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, EnchantmentCategory.BOW, equipmentSlots);
   }

   public int getMinCost(int i) {
      return 1 + (i - 1) * 10;
   }

   public int getMaxCost(int i) {
      return this.getMinCost(i) + 15;
   }

   public int getMaxLevel() {
      return 5;
   }
}
