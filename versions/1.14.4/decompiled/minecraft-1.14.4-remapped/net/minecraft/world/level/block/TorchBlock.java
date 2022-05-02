package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TorchBlock extends Block {
   protected static final VoxelShape AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D);

   protected TorchBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return AABB;
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return direction == Direction.DOWN && !this.canSurvive(var1, levelAccessor, var5)?Blocks.AIR.defaultBlockState():super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return canSupportCenter(levelReader, blockPos.below(), Direction.UP);
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      double var5 = (double)blockPos.getX() + 0.5D;
      double var7 = (double)blockPos.getY() + 0.7D;
      double var9 = (double)blockPos.getZ() + 0.5D;
      level.addParticle(ParticleTypes.SMOKE, var5, var7, var9, 0.0D, 0.0D, 0.0D);
      level.addParticle(ParticleTypes.FLAME, var5, var7, var9, 0.0D, 0.0D, 0.0D);
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }
}
