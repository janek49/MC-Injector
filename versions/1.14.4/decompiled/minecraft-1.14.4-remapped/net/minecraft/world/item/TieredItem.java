package net.minecraft.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

public class TieredItem extends Item {
   private final Tier tier;

   public TieredItem(Tier tier, Item.Properties item$Properties) {
      super(item$Properties.defaultDurability(tier.getUses()));
      this.tier = tier;
   }

   public Tier getTier() {
      return this.tier;
   }

   public int getEnchantmentValue() {
      return this.tier.getEnchantmentValue();
   }

   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return this.tier.getRepairIngredient().test(var2) || super.isValidRepairItem(var1, var2);
   }
}
