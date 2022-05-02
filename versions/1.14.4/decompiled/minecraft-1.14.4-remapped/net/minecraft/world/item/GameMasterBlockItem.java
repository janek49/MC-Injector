package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class GameMasterBlockItem extends BlockItem {
   public GameMasterBlockItem(Block block, Item.Properties item$Properties) {
      super(block, item$Properties);
   }

   @Nullable
   protected BlockState getPlacementState(BlockPlaceContext blockPlaceContext) {
      Player var2 = blockPlaceContext.getPlayer();
      return var2 != null && !var2.canUseGameMasterBlocks()?null:super.getPlacementState(blockPlaceContext);
   }
}
