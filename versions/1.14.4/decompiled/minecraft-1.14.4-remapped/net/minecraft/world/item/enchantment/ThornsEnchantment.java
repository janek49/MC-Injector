package net.minecraft.world.item.enchantment;

import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class ThornsEnchantment extends Enchantment {
   public ThornsEnchantment(Enchantment.Rarity enchantment$Rarity, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, EnchantmentCategory.ARMOR_CHEST, equipmentSlots);
   }

   public int getMinCost(int i) {
      return 10 + 20 * (i - 1);
   }

   public int getMaxCost(int i) {
      return super.getMinCost(i) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canEnchant(ItemStack itemStack) {
      return itemStack.getItem() instanceof ArmorItem?true:super.canEnchant(itemStack);
   }

   public void doPostHurt(LivingEntity livingEntity, Entity entity, int var3) {
      Random var4 = livingEntity.getRandom();
      Entry<EquipmentSlot, ItemStack> var5 = EnchantmentHelper.getRandomItemWith(Enchantments.THORNS, livingEntity);
      if(shouldHit(var3, var4)) {
         if(entity != null) {
            entity.hurt(DamageSource.thorns(livingEntity), (float)getDamage(var3, var4));
         }

         if(var5 != null) {
            ((ItemStack)var5.getValue()).hurtAndBreak(3, livingEntity, (livingEntity) -> {
               livingEntity.broadcastBreakEvent((EquipmentSlot)var5.getKey());
            });
         }
      } else if(var5 != null) {
         ((ItemStack)var5.getValue()).hurtAndBreak(1, livingEntity, (livingEntity) -> {
            livingEntity.broadcastBreakEvent((EquipmentSlot)var5.getKey());
         });
      }

   }

   public static boolean shouldHit(int var0, Random random) {
      return var0 <= 0?false:random.nextFloat() < 0.15F * (float)var0;
   }

   public static int getDamage(int var0, Random random) {
      return var0 > 10?var0 - 10:1 + random.nextInt(4);
   }
}
