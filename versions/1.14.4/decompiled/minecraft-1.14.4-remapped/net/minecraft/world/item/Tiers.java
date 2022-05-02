package net.minecraft.world.item;

import java.util.function.Supplier;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public enum Tiers implements Tier {
   WOOD(0, 59, 2.0F, 0.0F, 15, () -> {
      return Ingredient.of(ItemTags.PLANKS);
   }),
   STONE(1, 131, 4.0F, 1.0F, 5, () -> {
      return Ingredient.of(new ItemLike[]{Blocks.COBBLESTONE});
   }),
   IRON(2, 250, 6.0F, 2.0F, 14, () -> {
      return Ingredient.of(new ItemLike[]{Items.IRON_INGOT});
   }),
   DIAMOND(3, 1561, 8.0F, 3.0F, 10, () -> {
      return Ingredient.of(new ItemLike[]{Items.DIAMOND});
   }),
   GOLD(0, 32, 12.0F, 0.0F, 22, () -> {
      return Ingredient.of(new ItemLike[]{Items.GOLD_INGOT});
   });

   private final int level;
   private final int uses;
   private final float speed;
   private final float damage;
   private final int enchantmentValue;
   private final LazyLoadedValue repairIngredient;

   private Tiers(int level, int uses, float speed, float damage, int enchantmentValue, Supplier supplier) {
      this.level = level;
      this.uses = uses;
      this.speed = speed;
      this.damage = damage;
      this.enchantmentValue = enchantmentValue;
      this.repairIngredient = new LazyLoadedValue(supplier);
   }

   public int getUses() {
      return this.uses;
   }

   public float getSpeed() {
      return this.speed;
   }

   public float getAttackDamageBonus() {
      return this.damage;
   }

   public int getLevel() {
      return this.level;
   }

   public int getEnchantmentValue() {
      return this.enchantmentValue;
   }

   public Ingredient getRepairIngredient() {
      return (Ingredient)this.repairIngredient.get();
   }
}
