package net.minecraft.world.item.enchantment;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public abstract class Enchantment {
   private final EquipmentSlot[] slots;
   private final Enchantment.Rarity rarity;
   @Nullable
   public EnchantmentCategory category;
   @Nullable
   protected String descriptionId;

   @Nullable
   public static Enchantment byId(int id) {
      return (Enchantment)Registry.ENCHANTMENT.byId(id);
   }

   protected Enchantment(Enchantment.Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
      this.rarity = rarity;
      this.category = category;
      this.slots = slots;
   }

   public Map getSlotItems(LivingEntity livingEntity) {
      Map<EquipmentSlot, ItemStack> map = Maps.newEnumMap(EquipmentSlot.class);

      for(EquipmentSlot var6 : this.slots) {
         ItemStack var7 = livingEntity.getItemBySlot(var6);
         if(!var7.isEmpty()) {
            map.put(var6, var7);
         }
      }

      return map;
   }

   public Enchantment.Rarity getRarity() {
      return this.rarity;
   }

   public int getMinLevel() {
      return 1;
   }

   public int getMaxLevel() {
      return 1;
   }

   public int getMinCost(int i) {
      return 1 + i * 10;
   }

   public int getMaxCost(int i) {
      return this.getMinCost(i) + 5;
   }

   public int getDamageProtection(int var1, DamageSource damageSource) {
      return 0;
   }

   public float getDamageBonus(int var1, MobType mobType) {
      return 0.0F;
   }

   public final boolean isCompatibleWith(Enchantment enchantment) {
      return this.checkCompatibility(enchantment) && enchantment.checkCompatibility(this);
   }

   protected boolean checkCompatibility(Enchantment enchantment) {
      return this != enchantment;
   }

   protected String getOrCreateDescriptionId() {
      if(this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("enchantment", Registry.ENCHANTMENT.getKey(this));
      }

      return this.descriptionId;
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }

   public Component getFullname(int i) {
      Component component = new TranslatableComponent(this.getDescriptionId(), new Object[0]);
      if(this.isCurse()) {
         component.withStyle(ChatFormatting.RED);
      } else {
         component.withStyle(ChatFormatting.GRAY);
      }

      if(i != 1 || this.getMaxLevel() != 1) {
         component.append(" ").append((Component)(new TranslatableComponent("enchantment.level." + i, new Object[0])));
      }

      return component;
   }

   public boolean canEnchant(ItemStack itemStack) {
      return this.category.canEnchant(itemStack.getItem());
   }

   public void doPostAttack(LivingEntity livingEntity, Entity entity, int var3) {
   }

   public void doPostHurt(LivingEntity livingEntity, Entity entity, int var3) {
   }

   public boolean isTreasureOnly() {
      return false;
   }

   public boolean isCurse() {
      return false;
   }

   public static enum Rarity {
      COMMON(10),
      UNCOMMON(5),
      RARE(2),
      VERY_RARE(1);

      private final int weight;

      private Rarity(int weight) {
         this.weight = weight;
      }

      public int getWeight() {
         return this.weight;
      }
   }
}
