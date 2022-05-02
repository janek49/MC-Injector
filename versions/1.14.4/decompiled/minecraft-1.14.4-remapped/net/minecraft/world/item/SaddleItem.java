package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SaddleItem extends Item {
   public SaddleItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public boolean interactEnemy(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
      if(livingEntity instanceof Pig) {
         Pig var5 = (Pig)livingEntity;
         if(var5.isAlive() && !var5.hasSaddle() && !var5.isBaby()) {
            var5.setSaddle(true);
            var5.level.playSound(player, var5.x, var5.y, var5.z, SoundEvents.PIG_SADDLE, SoundSource.NEUTRAL, 0.5F, 1.0F);
            itemStack.shrink(1);
         }

         return true;
      } else {
         return false;
      }
   }
}
