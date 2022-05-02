package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class LoomBlock extends HorizontalDirectionalBlock {
   private static final TranslatableComponent CONTAINER_TITLE = new TranslatableComponent("container.loom", new Object[0]);

   protected LoomBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(level.isClientSide) {
         return true;
      } else {
         player.openMenu(blockState.getMenuProvider(level, blockPos));
         player.awardStat(Stats.INTERACT_WITH_LOOM);
         return true;
      }
   }

   public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos blockPos) {
      return new SimpleMenuProvider((var2, inventory, player) -> {
         return new LoomMenu(var2, inventory, ContainerLevelAccess.create(level, blockPos));
      }, CONTAINER_TITLE);
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING});
   }
}
