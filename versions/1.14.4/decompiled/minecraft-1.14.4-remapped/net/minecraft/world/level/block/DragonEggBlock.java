package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DragonEggBlock extends FallingBlock {
   protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   public DragonEggBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      this.teleport(blockState, level, blockPos);
      return true;
   }

   public void attack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
      this.teleport(blockState, level, blockPos);
   }

   private void teleport(BlockState blockState, Level level, BlockPos blockPos) {
      for(int var4 = 0; var4 < 1000; ++var4) {
         BlockPos var5 = blockPos.offset(level.random.nextInt(16) - level.random.nextInt(16), level.random.nextInt(8) - level.random.nextInt(8), level.random.nextInt(16) - level.random.nextInt(16));
         if(level.getBlockState(var5).isAir()) {
            if(level.isClientSide) {
               for(int var6 = 0; var6 < 128; ++var6) {
                  double var7 = level.random.nextDouble();
                  float var9 = (level.random.nextFloat() - 0.5F) * 0.2F;
                  float var10 = (level.random.nextFloat() - 0.5F) * 0.2F;
                  float var11 = (level.random.nextFloat() - 0.5F) * 0.2F;
                  double var12 = Mth.lerp(var7, (double)var5.getX(), (double)blockPos.getX()) + (level.random.nextDouble() - 0.5D) + 0.5D;
                  double var14 = Mth.lerp(var7, (double)var5.getY(), (double)blockPos.getY()) + level.random.nextDouble() - 0.5D;
                  double var16 = Mth.lerp(var7, (double)var5.getZ(), (double)blockPos.getZ()) + (level.random.nextDouble() - 0.5D) + 0.5D;
                  level.addParticle(ParticleTypes.PORTAL, var12, var14, var16, (double)var9, (double)var10, (double)var11);
               }
            } else {
               level.setBlock(var5, blockState, 2);
               level.removeBlock(blockPos, false);
            }

            return;
         }
      }

   }

   public int getTickDelay(LevelReader levelReader) {
      return 5;
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
