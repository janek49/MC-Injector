package net.minecraft.world.item;

import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ComplexItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;

public class EmptyMapItem extends ComplexItem {
   public EmptyMapItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = MapItem.create(level, Mth.floor(player.x), Mth.floor(player.z), (byte)0, true, false);
      ItemStack var5 = player.getItemInHand(interactionHand);
      if(!player.abilities.instabuild) {
         var5.shrink(1);
      }

      if(var5.isEmpty()) {
         return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
      } else {
         if(!player.inventory.add(var4.copy())) {
            player.drop(var4, false);
         }

         player.awardStat(Stats.ITEM_USED.get(this));
         return new InteractionResultHolder(InteractionResult.SUCCESS, var5);
      }
   }
}
