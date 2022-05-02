package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CartographyMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CartographyTableBlock extends Block {
   private static final TranslatableComponent CONTAINER_TITLE = new TranslatableComponent("container.cartography_table", new Object[0]);

   protected CartographyTableBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      player.openMenu(blockState.getMenuProvider(level, blockPos));
      player.awardStat(Stats.INTERACT_WITH_CARTOGRAPHY_TABLE);
      return true;
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos blockPos) {
      return new SimpleMenuProvider((var2, inventory, player) -> {
         return new CartographyMenu(var2, inventory, ContainerLevelAccess.create(level, blockPos));
      }, CONTAINER_TITLE);
   }
}
