package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

public class TippedArrowItem extends ArrowItem {
   public TippedArrowItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public ItemStack getDefaultInstance() {
      return PotionUtils.setPotion(super.getDefaultInstance(), Potions.POISON);
   }

   public void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList nonNullList) {
      if(this.allowdedIn(creativeModeTab)) {
         for(Potion var4 : Registry.POTION) {
            if(!var4.getEffects().isEmpty()) {
               nonNullList.add(PotionUtils.setPotion(new ItemStack(this), var4));
            }
         }
      }

   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level level, List list, TooltipFlag tooltipFlag) {
      PotionUtils.addPotionTooltip(itemStack, list, 0.125F);
   }

   public String getDescriptionId(ItemStack itemStack) {
      return PotionUtils.getPotion(itemStack).getName(this.getDescriptionId() + ".effect.");
   }
}
