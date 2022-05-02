package net.minecraft.world.item;

import java.util.function.Predicate;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public abstract class ProjectileWeaponItem extends Item {
   public static final Predicate ARROW_ONLY = (itemStack) -> {
      return itemStack.getItem().is(ItemTags.ARROWS);
   };
   public static final Predicate ARROW_OR_FIREWORK = ARROW_ONLY.or((itemStack) -> {
      return itemStack.getItem() == Items.FIREWORK_ROCKET;
   });

   public ProjectileWeaponItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public Predicate getSupportedHeldProjectiles() {
      return this.getAllSupportedProjectiles();
   }

   public abstract Predicate getAllSupportedProjectiles();

   public static ItemStack getHeldProjectile(LivingEntity livingEntity, Predicate predicate) {
      return predicate.test(livingEntity.getItemInHand(InteractionHand.OFF_HAND))?livingEntity.getItemInHand(InteractionHand.OFF_HAND):(predicate.test(livingEntity.getItemInHand(InteractionHand.MAIN_HAND))?livingEntity.getItemInHand(InteractionHand.MAIN_HAND):ItemStack.EMPTY);
   }

   public int getEnchantmentValue() {
      return 1;
   }
}
