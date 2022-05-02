package net.minecraft.world.item.enchantment;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DamageEnchantment extends Enchantment {
   private static final String[] NAMES = new String[]{"all", "undead", "arthropods"};
   private static final int[] MIN_COST = new int[]{1, 5, 5};
   private static final int[] LEVEL_COST = new int[]{11, 8, 8};
   private static final int[] LEVEL_COST_SPAN = new int[]{20, 20, 20};
   public final int type;

   public DamageEnchantment(Enchantment.Rarity enchantment$Rarity, int type, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, EnchantmentCategory.WEAPON, equipmentSlots);
      this.type = type;
   }

   public int getMinCost(int i) {
      return MIN_COST[this.type] + (i - 1) * LEVEL_COST[this.type];
   }

   public int getMaxCost(int i) {
      return this.getMinCost(i) + LEVEL_COST_SPAN[this.type];
   }

   public int getMaxLevel() {
      return 5;
   }

   public float getDamageBonus(int var1, MobType mobType) {
      return this.type == 0?1.0F + (float)Math.max(0, var1 - 1) * 0.5F:(this.type == 1 && mobType == MobType.UNDEAD?(float)var1 * 2.5F:(this.type == 2 && mobType == MobType.ARTHROPOD?(float)var1 * 2.5F:0.0F));
   }

   public boolean checkCompatibility(Enchantment enchantment) {
      return !(enchantment instanceof DamageEnchantment);
   }

   public boolean canEnchant(ItemStack itemStack) {
      return itemStack.getItem() instanceof AxeItem?true:super.canEnchant(itemStack);
   }

   public void doPostAttack(LivingEntity livingEntity, Entity entity, int var3) {
      if(entity instanceof LivingEntity) {
         LivingEntity livingEntity = (LivingEntity)entity;
         if(this.type == 2 && livingEntity.getMobType() == MobType.ARTHROPOD) {
            int var5 = 20 + livingEntity.getRandom().nextInt(10 * var3);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, var5, 3));
         }
      }

   }
}
