package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SweepingEdgeEnchantment extends Enchantment {
   public SweepingEdgeEnchantment(Enchantment.Rarity enchantment$Rarity, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, EnchantmentCategory.WEAPON, equipmentSlots);
   }

   public int getMinCost(int i) {
      return 5 + (i - 1) * 9;
   }

   public int getMaxCost(int i) {
      return this.getMinCost(i) + 15;
   }

   public int getMaxLevel() {
      return 3;
   }

   public static float getSweepingDamageRatio(int i) {
      return 1.0F - 1.0F / (float)(i + 1);
   }
}
