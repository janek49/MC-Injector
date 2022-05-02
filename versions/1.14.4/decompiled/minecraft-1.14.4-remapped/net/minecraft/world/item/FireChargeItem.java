package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FireChargeItem extends Item {
   public FireChargeItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      Level var2 = useOnContext.getLevel();
      if(var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         BlockPos var3 = useOnContext.getClickedPos();
         BlockState var4 = var2.getBlockState(var3);
         if(var4.getBlock() == Blocks.CAMPFIRE) {
            if(!((Boolean)var4.getValue(CampfireBlock.LIT)).booleanValue() && !((Boolean)var4.getValue(CampfireBlock.WATERLOGGED)).booleanValue()) {
               this.playSound(var2, var3);
               var2.setBlockAndUpdate(var3, (BlockState)var4.setValue(CampfireBlock.LIT, Boolean.valueOf(true)));
            }
         } else {
            var3 = var3.relative(useOnContext.getClickedFace());
            if(var2.getBlockState(var3).isAir()) {
               this.playSound(var2, var3);
               var2.setBlockAndUpdate(var3, ((FireBlock)Blocks.FIRE).getStateForPlacement(var2, var3));
            }
         }

         useOnContext.getItemInHand().shrink(1);
         return InteractionResult.SUCCESS;
      }
   }

   private void playSound(Level level, BlockPos blockPos) {
      level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
   }
}
