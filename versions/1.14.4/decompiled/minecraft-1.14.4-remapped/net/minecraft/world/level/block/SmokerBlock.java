package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SmokerBlock extends AbstractFurnaceBlock {
   protected SmokerBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new SmokerBlockEntity();
   }

   protected void openContainer(Level level, BlockPos blockPos, Player player) {
      BlockEntity var4 = level.getBlockEntity(blockPos);
      if(var4 instanceof SmokerBlockEntity) {
         player.openMenu((MenuProvider)var4);
         player.awardStat(Stats.INTERACT_WITH_SMOKER);
      }

   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(((Boolean)blockState.getValue(LIT)).booleanValue()) {
         double var5 = (double)blockPos.getX() + 0.5D;
         double var7 = (double)blockPos.getY();
         double var9 = (double)blockPos.getZ() + 0.5D;
         if(random.nextDouble() < 0.1D) {
            level.playLocalSound(var5, var7, var9, SoundEvents.SMOKER_SMOKE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         level.addParticle(ParticleTypes.SMOKE, var5, var7 + 1.1D, var9, 0.0D, 0.0D, 0.0D);
      }
   }
}
