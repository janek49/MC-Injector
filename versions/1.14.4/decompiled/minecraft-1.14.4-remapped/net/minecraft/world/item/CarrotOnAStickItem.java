package net.minecraft.world.item;

import java.util.function.Consumer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class CarrotOnAStickItem extends Item {
   public CarrotOnAStickItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      if(level.isClientSide) {
         return new InteractionResultHolder(InteractionResult.PASS, var4);
      } else {
         if(player.isPassenger() && player.getVehicle() instanceof Pig) {
            Pig var5 = (Pig)player.getVehicle();
            if(var4.getMaxDamage() - var4.getDamageValue() >= 7 && var5.boost()) {
               var4.hurtAndBreak(7, player, (player) -> {
                  player.broadcastBreakEvent(interactionHand);
               });
               if(var4.isEmpty()) {
                  ItemStack var6 = new ItemStack(Items.FISHING_ROD);
                  var6.setTag(var4.getTag());
                  return new InteractionResultHolder(InteractionResult.SUCCESS, var6);
               }

               return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
            }
         }

         player.awardStat(Stats.ITEM_USED.get(this));
         return new InteractionResultHolder(InteractionResult.PASS, var4);
      }
   }
}
