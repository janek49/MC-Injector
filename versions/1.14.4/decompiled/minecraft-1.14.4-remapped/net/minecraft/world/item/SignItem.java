package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignItem extends StandingAndWallBlockItem {
   public SignItem(Item.Properties item$Properties, Block var2, Block var3) {
      super(var2, var3, item$Properties);
   }

   protected boolean updateCustomBlockEntityTag(BlockPos blockPos, Level level, @Nullable Player player, ItemStack itemStack, BlockState blockState) {
      boolean var6 = super.updateCustomBlockEntityTag(blockPos, level, player, itemStack, blockState);
      if(!level.isClientSide && !var6 && player != null) {
         player.openTextEdit((SignBlockEntity)level.getBlockEntity(blockPos));
      }

      return var6;
   }
}
