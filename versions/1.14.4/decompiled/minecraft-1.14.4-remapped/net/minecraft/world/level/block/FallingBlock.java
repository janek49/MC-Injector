package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class FallingBlock extends Block {
   public FallingBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      level.getBlockTicks().scheduleTick(blockPos, this, this.getTickDelay(level));
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      levelAccessor.getBlockTicks().scheduleTick(var5, this, this.getTickDelay(levelAccessor));
      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!level.isClientSide) {
         this.checkSlide(level, blockPos);
      }

   }

   private void checkSlide(Level level, BlockPos blockPos) {
      if(isFree(level.getBlockState(blockPos.below())) && blockPos.getY() >= 0) {
         if(!level.isClientSide) {
            FallingBlockEntity var3 = new FallingBlockEntity(level, (double)blockPos.getX() + 0.5D, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5D, level.getBlockState(blockPos));
            this.falling(var3);
            level.addFreshEntity(var3);
         }

      }
   }

   protected void falling(FallingBlockEntity fallingBlockEntity) {
   }

   public int getTickDelay(LevelReader levelReader) {
      return 2;
   }

   public static boolean isFree(BlockState blockState) {
      Block var1 = blockState.getBlock();
      Material var2 = blockState.getMaterial();
      return blockState.isAir() || var1 == Blocks.FIRE || var2.isLiquid() || var2.isReplaceable();
   }

   public void onLand(Level level, BlockPos blockPos, BlockState var3, BlockState var4) {
   }

   public void onBroken(Level level, BlockPos blockPos) {
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(random.nextInt(16) == 0) {
         BlockPos blockPos = blockPos.below();
         if(isFree(level.getBlockState(blockPos))) {
            double var6 = (double)((float)blockPos.getX() + random.nextFloat());
            double var8 = (double)blockPos.getY() - 0.05D;
            double var10 = (double)((float)blockPos.getZ() + random.nextFloat());
            level.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, blockState), var6, var8, var10, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   public int getDustColor(BlockState blockState) {
      return -16777216;
   }
}
