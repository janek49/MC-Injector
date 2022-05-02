package net.minecraft.world.item.enchantment;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class ProtectionEnchantment extends Enchantment {
   public final ProtectionEnchantment.Type type;

   public ProtectionEnchantment(Enchantment.Rarity enchantment$Rarity, ProtectionEnchantment.Type type, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, EnchantmentCategory.ARMOR, equipmentSlots);
      this.type = type;
      if(type == ProtectionEnchantment.Type.FALL) {
         this.category = EnchantmentCategory.ARMOR_FEET;
      }

   }

   public int getMinCost(int i) {
      return this.type.getMinCost() + (i - 1) * this.type.getLevelCost();
   }

   public int getMaxCost(int i) {
      return this.getMinCost(i) + this.type.getLevelCost();
   }

   public int getMaxLevel() {
      return 4;
   }

   public int getDamageProtection(int var1, DamageSource damageSource) {
      return damageSource.isBypassInvul()?0:(this.type == ProtectionEnchantment.Type.ALL?var1:(this.type == ProtectionEnchantment.Type.FIRE && damageSource.isFire()?var1 * 2:(this.type == ProtectionEnchantment.Type.FALL && damageSource == DamageSource.FALL?var1 * 3:(this.type == ProtectionEnchantment.Type.EXPLOSION && damageSource.isExplosion()?var1 * 2:(this.type == ProtectionEnchantment.Type.PROJECTILE && damageSource.isProjectile()?var1 * 2:0)))));
   }

   public boolean checkCompatibility(Enchantment enchantment) {
      if(enchantment instanceof ProtectionEnchantment) {
         ProtectionEnchantment var2 = (ProtectionEnchantment)enchantment;
         return this.type == var2.type?false:this.type == ProtectionEnchantment.Type.FALL || var2.type == ProtectionEnchantment.Type.FALL;
      } else {
         return super.checkCompatibility(enchantment);
      }
   }

   public static int getFireAfterDampener(LivingEntity livingEntity, int var1) {
      int var2 = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, livingEntity);
      if(var2 > 0) {
         var1 -= Mth.floor((float)var1 * (float)var2 * 0.15F);
      }

      return var1;
   }

   public static double getExplosionKnockbackAfterDampener(LivingEntity livingEntity, double var1) {
      int var3 = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, livingEntity);
      if(var3 > 0) {
         var1 -= (double)Mth.floor(var1 * (double)((float)var3 * 0.15F));
      }

      return var1;
   }

   public static enum Type {
      ALL("all", 1, 11),
      FIRE("fire", 10, 8),
      FALL("fall", 5, 6),
      EXPLOSION("explosion", 5, 8),
      PROJECTILE("projectile", 3, 6);

      private final String name;
      private final int minCost;
      private final int levelCost;

      private Type(String name, int minCost, int levelCost) {
         this.name = name;
         this.minCost = minCost;
         this.levelCost = levelCost;
      }

      public int getMinCost() {
         return this.minCost;
      }

      public int getLevelCost() {
         return this.levelCost;
      }
   }
}
