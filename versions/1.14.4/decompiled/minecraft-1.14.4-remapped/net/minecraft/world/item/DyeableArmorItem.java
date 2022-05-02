package net.minecraft.world.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;

public class DyeableArmorItem extends ArmorItem implements DyeableLeatherItem {
   public DyeableArmorItem(ArmorMaterial armorMaterial, EquipmentSlot equipmentSlot, Item.Properties item$Properties) {
      super(armorMaterial, equipmentSlot, item$Properties);
   }
}
