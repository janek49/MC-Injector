package net.minecraft.world.item.enchantment;

import java.util.Random;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DigDurabilityEnchantment extends Enchantment {
   protected DigDurabilityEnchantment(Enchantment.Rarity enchantment$Rarity, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, EnchantmentCategory.BREAKABLE, equipmentSlots);
   }

   public int getMinCost(int i) {
      return 5 + (i - 1) * 8;
   }

   public int getMaxCost(int i) {
      return super.getMinCost(i) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canEnchant(ItemStack itemStack) {
      return itemStack.isDamageableItem()?true:super.canEnchant(itemStack);
   }

   public static boolean shouldIgnoreDurabilityDrop(ItemStack itemStack, int var1, Random random) {
      return itemStack.getItem() instanceof ArmorItem && random.nextFloat() < 0.6F?false:random.nextInt(var1 + 1) > 0;
   }
}
