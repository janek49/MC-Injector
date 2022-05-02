package net.minecraft.world.item;

import java.util.function.Consumer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FlintAndSteelItem extends Item {
   public FlintAndSteelItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      Player var2 = useOnContext.getPlayer();
      LevelAccessor var3 = useOnContext.getLevel();
      BlockPos var4 = useOnContext.getClickedPos();
      BlockPos var5 = var4.relative(useOnContext.getClickedFace());
      if(canUse(var3.getBlockState(var5), var3, var5)) {
         var3.playSound(var2, var5, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
         BlockState var6 = ((FireBlock)Blocks.FIRE).getStateForPlacement(var3, var5);
         var3.setBlock(var5, var6, 11);
         ItemStack var7 = useOnContext.getItemInHand();
         if(var2 instanceof ServerPlayer) {
            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)var2, var5, var7);
            var7.hurtAndBreak(1, var2, (player) -> {
               player.broadcastBreakEvent(useOnContext.getHand());
            });
         }

         return InteractionResult.SUCCESS;
      } else {
         BlockState var6 = var3.getBlockState(var4);
         if(canLightCampFire(var6)) {
            var3.playSound(var2, var4, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            var3.setBlock(var4, (BlockState)var6.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
            if(var2 != null) {
               useOnContext.getItemInHand().hurtAndBreak(1, var2, (player) -> {
                  player.broadcastBreakEvent(useOnContext.getHand());
               });
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.FAIL;
         }
      }
   }

   public static boolean canLightCampFire(BlockState blockState) {
      return blockState.getBlock() == Blocks.CAMPFIRE && !((Boolean)blockState.getValue(BlockStateProperties.WATERLOGGED)).booleanValue() && !((Boolean)blockState.getValue(BlockStateProperties.LIT)).booleanValue();
   }

   public static boolean canUse(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos) {
      BlockState blockState = ((FireBlock)Blocks.FIRE).getStateForPlacement(levelAccessor, blockPos);
      boolean var4 = false;

      for(Direction var6 : Direction.Plane.HORIZONTAL) {
         if(levelAccessor.getBlockState(blockPos.relative(var6)).getBlock() == Blocks.OBSIDIAN && ((NetherPortalBlock)Blocks.NETHER_PORTAL).isPortal(levelAccessor, blockPos) != null) {
            var4 = true;
         }
      }

      return blockState.isAir() && (blockState.canSurvive(levelAccessor, blockPos) || var4);
   }
}
