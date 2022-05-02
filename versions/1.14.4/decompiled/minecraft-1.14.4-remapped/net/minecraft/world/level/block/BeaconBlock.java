package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BeaconBlock extends BaseEntityBlock implements BeaconBeamBlock {
   public BeaconBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public DyeColor getColor() {
      return DyeColor.WHITE;
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new BeaconBlockEntity();
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(level.isClientSide) {
         return true;
      } else {
         BlockEntity var7 = level.getBlockEntity(blockPos);
         if(var7 instanceof BeaconBlockEntity) {
            player.openMenu((BeaconBlockEntity)var7);
            player.awardStat(Stats.INTERACT_WITH_BEACON);
         }

         return true;
      }
   }

   public boolean isRedstoneConductor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return false;
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.MODEL;
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
      if(itemStack.hasCustomHoverName()) {
         BlockEntity var6 = level.getBlockEntity(blockPos);
         if(var6 instanceof BeaconBlockEntity) {
            ((BeaconBlockEntity)var6).setCustomName(itemStack.getHoverName());
         }
      }

   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }
}
