package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class UntouchingEnchantment extends Enchantment {
   protected UntouchingEnchantment(Enchantment.Rarity enchantment$Rarity, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, EnchantmentCategory.DIGGER, equipmentSlots);
   }

   public int getMinCost(int i) {
      return 15;
   }

   public int getMaxCost(int i) {
      return super.getMinCost(i) + 50;
   }

   public int getMaxLevel() {
      return 1;
   }

   public boolean checkCompatibility(Enchantment enchantment) {
      return super.checkCompatibility(enchantment) && enchantment != Enchantments.BLOCK_FORTUNE;
   }
}
