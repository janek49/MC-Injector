package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallBlock extends CrossCollisionBlock {
   public static final BooleanProperty UP = BlockStateProperties.UP;
   private final VoxelShape[] shapeWithPostByIndex;
   private final VoxelShape[] collisionShapeWithPostByIndex;

   public WallBlock(Block.Properties block$Properties) {
      super(0.0F, 3.0F, 0.0F, 14.0F, 24.0F, block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(UP, Boolean.valueOf(true))).setValue(NORTH, Boolean.valueOf(false))).setValue(EAST, Boolean.valueOf(false))).setValue(SOUTH, Boolean.valueOf(false))).setValue(WEST, Boolean.valueOf(false))).setValue(WATERLOGGED, Boolean.valueOf(false)));
      this.shapeWithPostByIndex = this.makeShapes(4.0F, 3.0F, 16.0F, 0.0F, 14.0F);
      this.collisionShapeWithPostByIndex = this.makeShapes(4.0F, 3.0F, 24.0F, 0.0F, 24.0F);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return ((Boolean)blockState.getValue(UP)).booleanValue()?this.shapeWithPostByIndex[this.getAABBIndex(blockState)]:super.getShape(blockState, blockGetter, blockPos, collisionContext);
   }

   public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return ((Boolean)blockState.getValue(UP)).booleanValue()?this.collisionShapeWithPostByIndex[this.getAABBIndex(blockState)]:super.getCollisionShape(blockState, blockGetter, blockPos, collisionContext);
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }

   private boolean connectsTo(BlockState blockState, boolean var2, Direction direction) {
      Block var4 = blockState.getBlock();
      boolean var5 = var4.is(BlockTags.WALLS) || var4 instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(blockState, direction);
      return !isExceptionForConnection(var4) && var2 || var5;
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      LevelReader var2 = blockPlaceContext.getLevel();
      BlockPos var3 = blockPlaceContext.getClickedPos();
      FluidState var4 = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
      BlockPos var5 = var3.north();
      BlockPos var6 = var3.east();
      BlockPos var7 = var3.south();
      BlockPos var8 = var3.west();
      BlockState var9 = var2.getBlockState(var5);
      BlockState var10 = var2.getBlockState(var6);
      BlockState var11 = var2.getBlockState(var7);
      BlockState var12 = var2.getBlockState(var8);
      boolean var13 = this.connectsTo(var9, var9.isFaceSturdy(var2, var5, Direction.SOUTH), Direction.SOUTH);
      boolean var14 = this.connectsTo(var10, var10.isFaceSturdy(var2, var6, Direction.WEST), Direction.WEST);
      boolean var15 = this.connectsTo(var11, var11.isFaceSturdy(var2, var7, Direction.NORTH), Direction.NORTH);
      boolean var16 = this.connectsTo(var12, var12.isFaceSturdy(var2, var8, Direction.EAST), Direction.EAST);
      boolean var17 = (!var13 || var14 || !var15 || var16) && (var13 || !var14 || var15 || !var16);
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(UP, Boolean.valueOf(var17 || !var2.isEmptyBlock(var3.above())))).setValue(NORTH, Boolean.valueOf(var13))).setValue(EAST, Boolean.valueOf(var14))).setValue(SOUTH, Boolean.valueOf(var15))).setValue(WEST, Boolean.valueOf(var16))).setValue(WATERLOGGED, Boolean.valueOf(var4.getType() == Fluids.WATER));
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(((Boolean)var1.getValue(WATERLOGGED)).booleanValue()) {
         levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      }

      if(direction == Direction.DOWN) {
         return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
      } else {
         Direction direction = direction.getOpposite();
         boolean var8 = direction == Direction.NORTH?this.connectsTo(var3, var3.isFaceSturdy(levelAccessor, var6, direction), direction):((Boolean)var1.getValue(NORTH)).booleanValue();
         boolean var9 = direction == Direction.EAST?this.connectsTo(var3, var3.isFaceSturdy(levelAccessor, var6, direction), direction):((Boolean)var1.getValue(EAST)).booleanValue();
         boolean var10 = direction == Direction.SOUTH?this.connectsTo(var3, var3.isFaceSturdy(levelAccessor, var6, direction), direction):((Boolean)var1.getValue(SOUTH)).booleanValue();
         boolean var11 = direction == Direction.WEST?this.connectsTo(var3, var3.isFaceSturdy(levelAccessor, var6, direction), direction):((Boolean)var1.getValue(WEST)).booleanValue();
         boolean var12 = (!var8 || var9 || !var10 || var11) && (var8 || !var9 || var10 || !var11);
         return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(UP, Boolean.valueOf(var12 || !levelAccessor.isEmptyBlock(var5.above())))).setValue(NORTH, Boolean.valueOf(var8))).setValue(EAST, Boolean.valueOf(var9))).setValue(SOUTH, Boolean.valueOf(var10))).setValue(WEST, Boolean.valueOf(var11));
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{UP, NORTH, EAST, WEST, SOUTH, WATERLOGGED});
   }
}
