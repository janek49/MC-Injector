package net.minecraft.world.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ItemNameBlockItem extends BlockItem {
   public ItemNameBlockItem(Block block, Item.Properties item$Properties) {
      super(block, item$Properties);
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }
}
