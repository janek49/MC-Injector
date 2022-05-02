package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class WaterWorkerEnchantment extends Enchantment {
   public WaterWorkerEnchantment(Enchantment.Rarity enchantment$Rarity, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, EnchantmentCategory.ARMOR_HEAD, equipmentSlots);
   }

   public int getMinCost(int i) {
      return 1;
   }

   public int getMaxCost(int i) {
      return this.getMinCost(i) + 40;
   }

   public int getMaxLevel() {
      return 1;
   }
}
