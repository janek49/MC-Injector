package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CactusBlock extends Block {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
   protected static final VoxelShape COLLISION_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
   protected static final VoxelShape OUTLINE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   protected CactusBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, Integer.valueOf(0)));
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!blockState.canSurvive(level, blockPos)) {
         level.destroyBlock(blockPos, true);
      } else {
         BlockPos blockPos = blockPos.above();
         if(level.isEmptyBlock(blockPos)) {
            int var6;
            for(var6 = 1; level.getBlockState(blockPos.below(var6)).getBlock() == this; ++var6) {
               ;
            }

            if(var6 < 3) {
               int var7 = ((Integer)blockState.getValue(AGE)).intValue();
               if(var7 == 15) {
                  level.setBlockAndUpdate(blockPos, this.defaultBlockState());
                  BlockState var8 = (BlockState)blockState.setValue(AGE, Integer.valueOf(0));
                  level.setBlock(blockPos, var8, 4);
                  var8.neighborChanged(level, blockPos, this, blockPos, false);
               } else {
                  level.setBlock(blockPos, (BlockState)blockState.setValue(AGE, Integer.valueOf(var7 + 1)), 4);
               }

            }
         }
      }
   }

   public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return COLLISION_SHAPE;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return OUTLINE_SHAPE;
   }

   public boolean canOcclude(BlockState blockState) {
      return true;
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(!var1.canSurvive(levelAccessor, var5)) {
         levelAccessor.getBlockTicks().scheduleTick(var5, this, 1);
      }

      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      for(Direction var5 : Direction.Plane.HORIZONTAL) {
         BlockState var6 = levelReader.getBlockState(blockPos.relative(var5));
         Material var7 = var6.getMaterial();
         if(var7.isSolid() || levelReader.getFluidState(blockPos.relative(var5)).is(FluidTags.LAVA)) {
            return false;
         }
      }

      Block var4 = levelReader.getBlockState(blockPos.below()).getBlock();
      return (var4 == Blocks.CACTUS || var4 == Blocks.SAND || var4 == Blocks.RED_SAND) && !levelReader.getBlockState(blockPos.above()).getMaterial().isLiquid();
   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      entity.hurt(DamageSource.CACTUS, 1.0F);
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{AGE});
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
