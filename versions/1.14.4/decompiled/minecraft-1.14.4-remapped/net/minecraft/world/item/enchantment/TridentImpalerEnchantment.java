package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class TridentImpalerEnchantment extends Enchantment {
   public TridentImpalerEnchantment(Enchantment.Rarity enchantment$Rarity, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, EnchantmentCategory.TRIDENT, equipmentSlots);
   }

   public int getMinCost(int i) {
      return 1 + (i - 1) * 8;
   }

   public int getMaxCost(int i) {
      return this.getMinCost(i) + 20;
   }

   public int getMaxLevel() {
      return 5;
   }

   public float getDamageBonus(int var1, MobType mobType) {
      return mobType == MobType.WATER?(float)var1 * 2.5F:0.0F;
   }
}
