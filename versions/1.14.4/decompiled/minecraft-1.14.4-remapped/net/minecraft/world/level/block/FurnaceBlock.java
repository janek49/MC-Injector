package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FurnaceBlock extends AbstractFurnaceBlock {
   protected FurnaceBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new FurnaceBlockEntity();
   }

   protected void openContainer(Level level, BlockPos blockPos, Player player) {
      BlockEntity var4 = level.getBlockEntity(blockPos);
      if(var4 instanceof FurnaceBlockEntity) {
         player.openMenu((MenuProvider)var4);
         player.awardStat(Stats.INTERACT_WITH_FURNACE);
      }

   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(((Boolean)blockState.getValue(LIT)).booleanValue()) {
         double var5 = (double)blockPos.getX() + 0.5D;
         double var7 = (double)blockPos.getY();
         double var9 = (double)blockPos.getZ() + 0.5D;
         if(random.nextDouble() < 0.1D) {
            level.playLocalSound(var5, var7, var9, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         Direction var11 = (Direction)blockState.getValue(FACING);
         Direction.Axis var12 = var11.getAxis();
         double var13 = 0.52D;
         double var15 = random.nextDouble() * 0.6D - 0.3D;
         double var17 = var12 == Direction.Axis.X?(double)var11.getStepX() * 0.52D:var15;
         double var19 = random.nextDouble() * 6.0D / 16.0D;
         double var21 = var12 == Direction.Axis.Z?(double)var11.getStepZ() * 0.52D:var15;
         level.addParticle(ParticleTypes.SMOKE, var5 + var17, var7 + var19, var9 + var21, 0.0D, 0.0D, 0.0D);
         level.addParticle(ParticleTypes.FLAME, var5 + var17, var7 + var19, var9 + var21, 0.0D, 0.0D, 0.0D);
      }
   }
}
