package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class WetSpongeBlock extends Block {
   protected WetSpongeBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      Direction var5 = Direction.getRandomFace(random);
      if(var5 != Direction.UP) {
         BlockPos var6 = blockPos.relative(var5);
         BlockState var7 = level.getBlockState(var6);
         if(!blockState.canOcclude() || !var7.isFaceSturdy(level, var6, var5.getOpposite())) {
            double var8 = (double)blockPos.getX();
            double var10 = (double)blockPos.getY();
            double var12 = (double)blockPos.getZ();
            if(var5 == Direction.DOWN) {
               var10 = var10 - 0.05D;
               var8 += random.nextDouble();
               var12 += random.nextDouble();
            } else {
               var10 = var10 + random.nextDouble() * 0.8D;
               if(var5.getAxis() == Direction.Axis.X) {
                  var12 += random.nextDouble();
                  if(var5 == Direction.EAST) {
                     ++var8;
                  } else {
                     var8 += 0.05D;
                  }
               } else {
                  var8 += random.nextDouble();
                  if(var5 == Direction.SOUTH) {
                     ++var12;
                  } else {
                     var12 += 0.05D;
                  }
               }
            }

            level.addParticle(ParticleTypes.DRIPPING_WATER, var8, var10, var12, 0.0D, 0.0D, 0.0D);
         }
      }
   }
}
