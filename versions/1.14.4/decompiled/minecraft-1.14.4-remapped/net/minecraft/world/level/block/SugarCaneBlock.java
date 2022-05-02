package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SugarCaneBlock extends Block {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

   protected SugarCaneBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!blockState.canSurvive(level, blockPos)) {
         level.destroyBlock(blockPos, true);
      } else if(level.isEmptyBlock(blockPos.above())) {
         int var5;
         for(var5 = 1; level.getBlockState(blockPos.below(var5)).getBlock() == this; ++var5) {
            ;
         }

         if(var5 < 3) {
            int var6 = ((Integer)blockState.getValue(AGE)).intValue();
            if(var6 == 15) {
               level.setBlockAndUpdate(blockPos.above(), this.defaultBlockState());
               level.setBlock(blockPos, (BlockState)blockState.setValue(AGE, Integer.valueOf(0)), 4);
            } else {
               level.setBlock(blockPos, (BlockState)blockState.setValue(AGE, Integer.valueOf(var6 + 1)), 4);
            }
         }
      }

   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(!var1.canSurvive(levelAccessor, var5)) {
         levelAccessor.getBlockTicks().scheduleTick(var5, this, 1);
      }

      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      Block var4 = levelReader.getBlockState(blockPos.below()).getBlock();
      if(var4 == this) {
         return true;
      } else {
         if(var4 == Blocks.GRASS_BLOCK || var4 == Blocks.DIRT || var4 == Blocks.COARSE_DIRT || var4 == Blocks.PODZOL || var4 == Blocks.SAND || var4 == Blocks.RED_SAND) {
            BlockPos var5 = blockPos.below();

            for(Direction var7 : Direction.Plane.HORIZONTAL) {
               BlockState var8 = levelReader.getBlockState(var5.relative(var7));
               FluidState var9 = levelReader.getFluidState(var5.relative(var7));
               if(var9.is(FluidTags.WATER) || var8.getBlock() == Blocks.FROSTED_ICE) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{AGE});
   }
}
