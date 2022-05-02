package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SeaPickleBlock extends BushBlock implements BonemealableBlock, SimpleWaterloggedBlock {
   public static final IntegerProperty PICKLES = BlockStateProperties.PICKLES;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape ONE_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D);
   protected static final VoxelShape TWO_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 6.0D, 13.0D);
   protected static final VoxelShape THREE_AABB = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
   protected static final VoxelShape FOUR_AABB = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D);

   protected SeaPickleBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(PICKLES, Integer.valueOf(1))).setValue(WATERLOGGED, Boolean.valueOf(true)));
   }

   public int getLightEmission(BlockState blockState) {
      return this.isDead(blockState)?0:super.getLightEmission(blockState) + 3 * ((Integer)blockState.getValue(PICKLES)).intValue();
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockState blockState = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos());
      if(blockState.getBlock() == this) {
         return (BlockState)blockState.setValue(PICKLES, Integer.valueOf(Math.min(4, ((Integer)blockState.getValue(PICKLES)).intValue() + 1)));
      } else {
         FluidState var3 = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
         boolean var4 = var3.is(FluidTags.WATER) && var3.getAmount() == 8;
         return (BlockState)super.getStateForPlacement(blockPlaceContext).setValue(WATERLOGGED, Boolean.valueOf(var4));
      }
   }

   private boolean isDead(BlockState blockState) {
      return !((Boolean)blockState.getValue(WATERLOGGED)).booleanValue();
   }

   protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return !blockState.getCollisionShape(blockGetter, blockPos).getFaceShape(Direction.UP).isEmpty();
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockPos blockPos = blockPos.below();
      return this.mayPlaceOn(levelReader.getBlockState(blockPos), levelReader, blockPos);
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(!var1.canSurvive(levelAccessor, var5)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if(((Boolean)var1.getValue(WATERLOGGED)).booleanValue()) {
            levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
         }

         return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
      }
   }

   public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
      return blockPlaceContext.getItemInHand().getItem() == this.asItem() && ((Integer)blockState.getValue(PICKLES)).intValue() < 4?true:super.canBeReplaced(blockState, blockPlaceContext);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      switch(((Integer)blockState.getValue(PICKLES)).intValue()) {
      case 1:
      default:
         return ONE_AABB;
      case 2:
         return TWO_AABB;
      case 3:
         return THREE_AABB;
      case 4:
         return FOUR_AABB;
      }
   }

   public FluidState getFluidState(BlockState blockState) {
      return ((Boolean)blockState.getValue(WATERLOGGED)).booleanValue()?Fluids.WATER.getSource(false):super.getFluidState(blockState);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{PICKLES, WATERLOGGED});
   }

   public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean var4) {
      return true;
   }

   public boolean isBonemealSuccess(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      return true;
   }

   public void performBonemeal(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      if(!this.isDead(blockState) && level.getBlockState(blockPos.below()).is(BlockTags.CORAL_BLOCKS)) {
         int var5 = 5;
         int var6 = 1;
         int var7 = 2;
         int var8 = 0;
         int var9 = blockPos.getX() - 2;
         int var10 = 0;

         for(int var11 = 0; var11 < 5; ++var11) {
            for(int var12 = 0; var12 < var6; ++var12) {
               int var13 = 2 + blockPos.getY() - 1;

               for(int var14 = var13 - 2; var14 < var13; ++var14) {
                  BlockPos var15 = new BlockPos(var9 + var11, var14, blockPos.getZ() - var10 + var12);
                  if(var15 != blockPos && random.nextInt(6) == 0 && level.getBlockState(var15).getBlock() == Blocks.WATER) {
                     BlockState var16 = level.getBlockState(var15.below());
                     if(var16.is(BlockTags.CORAL_BLOCKS)) {
                        level.setBlock(var15, (BlockState)Blocks.SEA_PICKLE.defaultBlockState().setValue(PICKLES, Integer.valueOf(random.nextInt(4) + 1)), 3);
                     }
                  }
               }
            }

            if(var8 < 2) {
               var6 += 2;
               ++var10;
            } else {
               var6 -= 2;
               --var10;
            }

            ++var8;
         }

         level.setBlock(blockPos, (BlockState)blockState.setValue(PICKLES, Integer.valueOf(4)), 2);
      }

   }
}
