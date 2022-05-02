package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CraftingTableBlock extends Block {
   private static final Component CONTAINER_TITLE = new TranslatableComponent("container.crafting", new Object[0]);

   protected CraftingTableBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      player.openMenu(blockState.getMenuProvider(level, blockPos));
      player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
      return true;
   }

   public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos blockPos) {
      return new SimpleMenuProvider((var2, inventory, player) -> {
         return new CraftingMenu(var2, inventory, ContainerLevelAccess.create(level, blockPos));
      }, CONTAINER_TITLE);
   }
}
