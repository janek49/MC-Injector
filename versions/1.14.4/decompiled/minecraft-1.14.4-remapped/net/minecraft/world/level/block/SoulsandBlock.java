package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SoulsandBlock extends Block {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

   public SoulsandBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.4D, 1.0D, 0.4D));
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      BubbleColumnBlock.growColumn(level, blockPos.above(), false);
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      level.getBlockTicks().scheduleTick(var3, this, this.getTickDelay(level));
   }

   public boolean isRedstoneConductor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return true;
   }

   public int getTickDelay(LevelReader levelReader) {
      return 20;
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      level.getBlockTicks().scheduleTick(blockPos, this, this.getTickDelay(level));
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }

   public boolean isValidSpawn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType entityType) {
      return true;
   }
}
