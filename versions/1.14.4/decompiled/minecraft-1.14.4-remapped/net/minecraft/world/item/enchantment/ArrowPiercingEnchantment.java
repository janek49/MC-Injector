package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class ArrowPiercingEnchantment extends Enchantment {
   public ArrowPiercingEnchantment(Enchantment.Rarity enchantment$Rarity, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, EnchantmentCategory.CROSSBOW, equipmentSlots);
   }

   public int getMinCost(int i) {
      return 1 + (i - 1) * 10;
   }

   public int getMaxCost(int i) {
      return 50;
   }

   public int getMaxLevel() {
      return 4;
   }

   public boolean checkCompatibility(Enchantment enchantment) {
      return super.checkCompatibility(enchantment) && enchantment != Enchantments.MULTISHOT;
   }
}
