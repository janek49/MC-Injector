package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseRailBlock extends Block {
   protected static final VoxelShape FLAT_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   protected static final VoxelShape HALF_BLOCK_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   private final boolean isStraight;

   public static boolean isRail(Level level, BlockPos blockPos) {
      return isRail(level.getBlockState(blockPos));
   }

   public static boolean isRail(BlockState blockState) {
      return blockState.is(BlockTags.RAILS);
   }

   protected BaseRailBlock(boolean isStraight, Block.Properties block$Properties) {
      super(block$Properties);
      this.isStraight = isStraight;
   }

   public boolean isStraight() {
      return this.isStraight;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      RailShape var5 = blockState.getBlock() == this?(RailShape)blockState.getValue(this.getShapeProperty()):null;
      return var5 != null && var5.isAscending()?HALF_BLOCK_AABB:FLAT_AABB;
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return canSupportRigidBlock(levelReader, blockPos.below());
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var4.getBlock() != var1.getBlock()) {
         if(!level.isClientSide) {
            var1 = this.updateDir(level, blockPos, var1, true);
            if(this.isStraight) {
               var1.neighborChanged(level, blockPos, this, blockPos, var5);
            }
         }

      }
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      if(!level.isClientSide) {
         RailShape var7 = (RailShape)blockState.getValue(this.getShapeProperty());
         boolean var8 = false;
         BlockPos var9 = var3.below();
         if(!canSupportRigidBlock(level, var9)) {
            var8 = true;
         }

         BlockPos var10 = var3.east();
         if(var7 == RailShape.ASCENDING_EAST && !canSupportRigidBlock(level, var10)) {
            var8 = true;
         } else {
            BlockPos var11 = var3.west();
            if(var7 == RailShape.ASCENDING_WEST && !canSupportRigidBlock(level, var11)) {
               var8 = true;
            } else {
               BlockPos var12 = var3.north();
               if(var7 == RailShape.ASCENDING_NORTH && !canSupportRigidBlock(level, var12)) {
                  var8 = true;
               } else {
                  BlockPos var13 = var3.south();
                  if(var7 == RailShape.ASCENDING_SOUTH && !canSupportRigidBlock(level, var13)) {
                     var8 = true;
                  }
               }
            }
         }

         if(var8 && !level.isEmptyBlock(var3)) {
            if(!var6) {
               dropResources(blockState, level, var3);
            }

            level.removeBlock(var3, var6);
         } else {
            this.updateState(blockState, level, var3, block);
         }

      }
   }

   protected void updateState(BlockState blockState, Level level, BlockPos blockPos, Block block) {
   }

   protected BlockState updateDir(Level level, BlockPos blockPos, BlockState var3, boolean var4) {
      return level.isClientSide?var3:(new RailState(level, blockPos, var3)).place(level.hasNeighborSignal(blockPos), var4).getState();
   }

   public PushReaction getPistonPushReaction(BlockState blockState) {
      return PushReaction.NORMAL;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(!var5) {
         super.onRemove(var1, level, blockPos, var4, var5);
         if(((RailShape)var1.getValue(this.getShapeProperty())).isAscending()) {
            level.updateNeighborsAt(blockPos.above(), this);
         }

         if(this.isStraight) {
            level.updateNeighborsAt(blockPos, this);
            level.updateNeighborsAt(blockPos.below(), this);
         }

      }
   }

   public abstract Property getShapeProperty();
}
