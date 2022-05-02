package net.minecraft.world.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class BowlFoodItem extends Item {
   public BowlFoodItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public ItemStack finishUsingItem(ItemStack var1, Level level, LivingEntity livingEntity) {
      super.finishUsingItem(var1, level, livingEntity);
      return new ItemStack(Items.BOWL);
   }
}
