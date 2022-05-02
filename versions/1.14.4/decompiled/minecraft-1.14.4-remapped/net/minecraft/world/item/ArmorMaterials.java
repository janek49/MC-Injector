package net.minecraft.world.item;

import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public enum ArmorMaterials implements ArmorMaterial {
   LEATHER("leather", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, () -> {
      return Ingredient.of(new ItemLike[]{Items.LEATHER});
   }),
   CHAIN("chainmail", 15, new int[]{1, 4, 5, 2}, 12, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, () -> {
      return Ingredient.of(new ItemLike[]{Items.IRON_INGOT});
   }),
   IRON("iron", 15, new int[]{2, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> {
      return Ingredient.of(new ItemLike[]{Items.IRON_INGOT});
   }),
   GOLD("gold", 7, new int[]{1, 3, 5, 2}, 25, SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, () -> {
      return Ingredient.of(new ItemLike[]{Items.GOLD_INGOT});
   }),
   DIAMOND("diamond", 33, new int[]{3, 6, 8, 3}, 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, () -> {
      return Ingredient.of(new ItemLike[]{Items.DIAMOND});
   }),
   TURTLE("turtle", 25, new int[]{2, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_TURTLE, 0.0F, () -> {
      return Ingredient.of(new ItemLike[]{Items.SCUTE});
   });

   private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
   private final String name;
   private final int durabilityMultiplier;
   private final int[] slotProtections;
   private final int enchantmentValue;
   private final SoundEvent sound;
   private final float toughness;
   private final LazyLoadedValue repairIngredient;

   private ArmorMaterials(String name, int durabilityMultiplier, int[] slotProtections, int enchantmentValue, SoundEvent sound, float toughness, Supplier supplier) {
      this.name = name;
      this.durabilityMultiplier = durabilityMultiplier;
      this.slotProtections = slotProtections;
      this.enchantmentValue = enchantmentValue;
      this.sound = sound;
      this.toughness = toughness;
      this.repairIngredient = new LazyLoadedValue(supplier);
   }

   public int getDurabilityForSlot(EquipmentSlot equipmentSlot) {
      return HEALTH_PER_SLOT[equipmentSlot.getIndex()] * this.durabilityMultiplier;
   }

   public int getDefenseForSlot(EquipmentSlot equipmentSlot) {
      return this.slotProtections[equipmentSlot.getIndex()];
   }

   public int getEnchantmentValue() {
      return this.enchantmentValue;
   }

   public SoundEvent getEquipSound() {
      return this.sound;
   }

   public Ingredient getRepairIngredient() {
      return (Ingredient)this.repairIngredient.get();
   }

   public String getName() {
      return this.name;
   }

   public float getToughness() {
      return this.toughness;
   }
}
