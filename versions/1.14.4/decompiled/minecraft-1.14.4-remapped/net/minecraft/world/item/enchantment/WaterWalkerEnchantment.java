package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class WaterWalkerEnchantment extends Enchantment {
   public WaterWalkerEnchantment(Enchantment.Rarity enchantment$Rarity, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, EnchantmentCategory.ARMOR_FEET, equipmentSlots);
   }

   public int getMinCost(int i) {
      return i * 10;
   }

   public int getMaxCost(int i) {
      return this.getMinCost(i) + 15;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean checkCompatibility(Enchantment enchantment) {
      return super.checkCompatibility(enchantment) && enchantment != Enchantments.FROST_WALKER;
   }
}
