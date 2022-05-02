package net.minecraft.world.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArrowItem extends Item {
   public ArrowItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public AbstractArrow createArrow(Level level, ItemStack itemStack, LivingEntity livingEntity) {
      Arrow var4 = new Arrow(level, livingEntity);
      var4.setEffectsFromItem(itemStack);
      return var4;
   }
}
