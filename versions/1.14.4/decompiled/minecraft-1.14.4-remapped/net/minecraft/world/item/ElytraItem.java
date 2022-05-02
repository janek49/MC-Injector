package net.minecraft.world.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemPropertyFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class ElytraItem extends Item {
   public ElytraItem(Item.Properties item$Properties) {
      super(item$Properties);
      this.addProperty(new ResourceLocation("broken"), (itemStack, level, livingEntity) -> {
         return isFlyEnabled(itemStack)?0.0F:1.0F;
      });
      DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
   }

   public static boolean isFlyEnabled(ItemStack itemStack) {
      return itemStack.getDamageValue() < itemStack.getMaxDamage() - 1;
   }

   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return var2.getItem() == Items.PHANTOM_MEMBRANE;
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      EquipmentSlot var5 = Mob.getEquipmentSlotForItem(var4);
      ItemStack var6 = player.getItemBySlot(var5);
      if(var6.isEmpty()) {
         player.setItemSlot(var5, var4.copy());
         var4.setCount(0);
         return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
      } else {
         return new InteractionResultHolder(InteractionResult.FAIL, var4);
      }
   }
}
