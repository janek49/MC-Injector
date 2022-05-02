package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class AirItem extends Item {
   private final Block block;

   public AirItem(Block block, Item.Properties item$Properties) {
      super(item$Properties);
      this.block = block;
   }

   public String getDescriptionId() {
      return this.block.getDescriptionId();
   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level level, List list, TooltipFlag tooltipFlag) {
      super.appendHoverText(itemStack, level, list, tooltipFlag);
      this.block.appendHoverText(itemStack, level, list, tooltipFlag);
   }
}
