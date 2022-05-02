package net.minecraft.world.level.block;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class PumpkinBlock extends StemGrownBlock {
   protected PumpkinBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      ItemStack var7 = player.getItemInHand(interactionHand);
      if(var7.getItem() == Items.SHEARS) {
         if(!level.isClientSide) {
            Direction var8 = blockHitResult.getDirection();
            Direction var9 = var8.getAxis() == Direction.Axis.Y?player.getDirection().getOpposite():var8;
            level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlock(blockPos, (BlockState)Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, var9), 11);
            ItemEntity var10 = new ItemEntity(level, (double)blockPos.getX() + 0.5D + (double)var9.getStepX() * 0.65D, (double)blockPos.getY() + 0.1D, (double)blockPos.getZ() + 0.5D + (double)var9.getStepZ() * 0.65D, new ItemStack(Items.PUMPKIN_SEEDS, 4));
            var10.setDeltaMovement(0.05D * (double)var9.getStepX() + level.random.nextDouble() * 0.02D, 0.05D, 0.05D * (double)var9.getStepZ() + level.random.nextDouble() * 0.02D);
            level.addFreshEntity(var10);
            var7.hurtAndBreak(1, player, (player) -> {
               player.broadcastBreakEvent(interactionHand);
            });
         }

         return true;
      } else {
         return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
      }
   }

   public StemBlock getStem() {
      return (StemBlock)Blocks.PUMPKIN_STEM;
   }

   public AttachedStemBlock getAttachedStem() {
      return (AttachedStemBlock)Blocks.ATTACHED_PUMPKIN_STEM;
   }
}
