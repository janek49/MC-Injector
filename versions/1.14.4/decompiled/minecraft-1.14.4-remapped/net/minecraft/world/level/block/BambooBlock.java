package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BambooBlock extends Block implements BonemealableBlock {
   protected static final VoxelShape SMALL_SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);
   protected static final VoxelShape LARGE_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
   protected static final VoxelShape COLLISION_SHAPE = Block.box(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D);
   public static final IntegerProperty AGE = BlockStateProperties.AGE_1;
   public static final EnumProperty LEAVES = BlockStateProperties.BAMBOO_LEAVES;
   public static final IntegerProperty STAGE = BlockStateProperties.STAGE;

   public BambooBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, Integer.valueOf(0))).setValue(LEAVES, BambooLeaves.NONE)).setValue(STAGE, Integer.valueOf(0)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{AGE, LEAVES, STAGE});
   }

   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.XZ;
   }

   public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return true;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      VoxelShape voxelShape = blockState.getValue(LEAVES) == BambooLeaves.LARGE?LARGE_SHAPE:SMALL_SHAPE;
      Vec3 var6 = blockState.getOffset(blockGetter, blockPos);
      return voxelShape.move(var6.x, var6.y, var6.z);
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }

   public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      Vec3 var5 = blockState.getOffset(blockGetter, blockPos);
      return COLLISION_SHAPE.move(var5.x, var5.y, var5.z);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      FluidState var2 = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
      if(!var2.isEmpty()) {
         return null;
      } else {
         BlockState var3 = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos().below());
         if(var3.is(BlockTags.BAMBOO_PLANTABLE_ON)) {
            Block var4 = var3.getBlock();
            if(var4 == Blocks.BAMBOO_SAPLING) {
               return (BlockState)this.defaultBlockState().setValue(AGE, Integer.valueOf(0));
            } else if(var4 == Blocks.BAMBOO) {
               int var5 = ((Integer)var3.getValue(AGE)).intValue() > 0?1:0;
               return (BlockState)this.defaultBlockState().setValue(AGE, Integer.valueOf(var5));
            } else {
               return Blocks.BAMBOO_SAPLING.defaultBlockState();
            }
         } else {
            return null;
         }
      }
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!blockState.canSurvive(level, blockPos)) {
         level.destroyBlock(blockPos, true);
      } else if(((Integer)blockState.getValue(STAGE)).intValue() == 0) {
         if(random.nextInt(3) == 0 && level.isEmptyBlock(blockPos.above()) && level.getRawBrightness(blockPos.above(), 0) >= 9) {
            int var5 = this.getHeightBelowUpToMax(level, blockPos) + 1;
            if(var5 < 16) {
               this.growBamboo(blockState, level, blockPos, random, var5);
            }
         }

      }
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return levelReader.getBlockState(blockPos.below()).is(BlockTags.BAMBOO_PLANTABLE_ON);
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(!var1.canSurvive(levelAccessor, var5)) {
         levelAccessor.getBlockTicks().scheduleTick(var5, this, 1);
      }

      if(direction == Direction.UP && var3.getBlock() == Blocks.BAMBOO && ((Integer)var3.getValue(AGE)).intValue() > ((Integer)var1.getValue(AGE)).intValue()) {
         levelAccessor.setBlock(var5, (BlockState)var1.cycle(AGE), 2);
      }

      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean var4) {
      int var5 = this.getHeightAboveUpToMax(blockGetter, blockPos);
      int var6 = this.getHeightBelowUpToMax(blockGetter, blockPos);
      return var5 + var6 + 1 < 16 && ((Integer)blockGetter.getBlockState(blockPos.above(var5)).getValue(STAGE)).intValue() != 1;
   }

   public boolean isBonemealSuccess(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      return true;
   }

   public void performBonemeal(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      int var5 = this.getHeightAboveUpToMax(level, blockPos);
      int var6 = this.getHeightBelowUpToMax(level, blockPos);
      int var7 = var5 + var6 + 1;
      int var8 = 1 + random.nextInt(2);

      for(int var9 = 0; var9 < var8; ++var9) {
         BlockPos var10 = blockPos.above(var5);
         BlockState var11 = level.getBlockState(var10);
         if(var7 >= 16 || ((Integer)var11.getValue(STAGE)).intValue() == 1 || !level.isEmptyBlock(var10.above())) {
            return;
         }

         this.growBamboo(var11, level, var10, random, var7);
         ++var5;
         ++var7;
      }

   }

   public float getDestroyProgress(BlockState blockState, Player player, BlockGetter blockGetter, BlockPos blockPos) {
      return player.getMainHandItem().getItem() instanceof SwordItem?1.0F:super.getDestroyProgress(blockState, player, blockGetter, blockPos);
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   protected void growBamboo(BlockState blockState, Level level, BlockPos blockPos, Random random, int var5) {
      BlockState blockState = level.getBlockState(blockPos.below());
      BlockPos var7 = blockPos.below(2);
      BlockState var8 = level.getBlockState(var7);
      BambooLeaves var9 = BambooLeaves.NONE;
      if(var5 >= 1) {
         if(blockState.getBlock() == Blocks.BAMBOO && blockState.getValue(LEAVES) != BambooLeaves.NONE) {
            if(blockState.getBlock() == Blocks.BAMBOO && blockState.getValue(LEAVES) != BambooLeaves.NONE) {
               var9 = BambooLeaves.LARGE;
               if(var8.getBlock() == Blocks.BAMBOO) {
                  level.setBlock(blockPos.below(), (BlockState)blockState.setValue(LEAVES, BambooLeaves.SMALL), 3);
                  level.setBlock(var7, (BlockState)var8.setValue(LEAVES, BambooLeaves.NONE), 3);
               }
            }
         } else {
            var9 = BambooLeaves.SMALL;
         }
      }

      int var10 = ((Integer)blockState.getValue(AGE)).intValue() != 1 && var8.getBlock() != Blocks.BAMBOO?0:1;
      int var11 = (var5 < 11 || random.nextFloat() >= 0.25F) && var5 != 15?0:1;
      level.setBlock(blockPos.above(), (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(AGE, Integer.valueOf(var10))).setValue(LEAVES, var9)).setValue(STAGE, Integer.valueOf(var11)), 3);
   }

   protected int getHeightAboveUpToMax(BlockGetter blockGetter, BlockPos blockPos) {
      int var3;
      for(var3 = 0; var3 < 16 && blockGetter.getBlockState(blockPos.above(var3 + 1)).getBlock() == Blocks.BAMBOO; ++var3) {
         ;
      }

      return var3;
   }

   protected int getHeightBelowUpToMax(BlockGetter blockGetter, BlockPos blockPos) {
      int var3;
      for(var3 = 0; var3 < 16 && blockGetter.getBlockState(blockPos.below(var3 + 1)).getBlock() == Blocks.BAMBOO; ++var3) {
         ;
      }

      return var3;
   }
}
