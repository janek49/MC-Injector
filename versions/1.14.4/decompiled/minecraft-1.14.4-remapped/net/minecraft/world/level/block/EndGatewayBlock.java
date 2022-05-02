package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EndGatewayBlock extends BaseEntityBlock {
   protected EndGatewayBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new TheEndGatewayBlockEntity();
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      BlockEntity var5 = level.getBlockEntity(blockPos);
      if(var5 instanceof TheEndGatewayBlockEntity) {
         int var6 = ((TheEndGatewayBlockEntity)var5).getParticleAmount();

         for(int var7 = 0; var7 < var6; ++var7) {
            double var8 = (double)((float)blockPos.getX() + random.nextFloat());
            double var10 = (double)((float)blockPos.getY() + random.nextFloat());
            double var12 = (double)((float)blockPos.getZ() + random.nextFloat());
            double var14 = ((double)random.nextFloat() - 0.5D) * 0.5D;
            double var16 = ((double)random.nextFloat() - 0.5D) * 0.5D;
            double var18 = ((double)random.nextFloat() - 0.5D) * 0.5D;
            int var20 = random.nextInt(2) * 2 - 1;
            if(random.nextBoolean()) {
               var12 = (double)blockPos.getZ() + 0.5D + 0.25D * (double)var20;
               var18 = (double)(random.nextFloat() * 2.0F * (float)var20);
            } else {
               var8 = (double)blockPos.getX() + 0.5D + 0.25D * (double)var20;
               var14 = (double)(random.nextFloat() * 2.0F * (float)var20);
            }

            level.addParticle(ParticleTypes.PORTAL, var8, var10, var12, var14, var16, var18);
         }

      }
   }

   public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      return ItemStack.EMPTY;
   }
}
