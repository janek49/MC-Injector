package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DyeItem extends Item {
   private static final Map ITEM_BY_COLOR = Maps.newEnumMap(DyeColor.class);
   private final DyeColor dyeColor;

   public DyeItem(DyeColor dyeColor, Item.Properties item$Properties) {
      super(item$Properties);
      this.dyeColor = dyeColor;
      ITEM_BY_COLOR.put(dyeColor, this);
   }

   public boolean interactEnemy(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
      if(livingEntity instanceof Sheep) {
         Sheep var5 = (Sheep)livingEntity;
         if(var5.isAlive() && !var5.isSheared() && var5.getColor() != this.dyeColor) {
            var5.setColor(this.dyeColor);
            itemStack.shrink(1);
         }

         return true;
      } else {
         return false;
      }
   }

   public DyeColor getDyeColor() {
      return this.dyeColor;
   }

   public static DyeItem byColor(DyeColor color) {
      return (DyeItem)ITEM_BY_COLOR.get(color);
   }
}
