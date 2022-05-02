package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FenceGateBlock extends HorizontalDirectionalBlock {
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty IN_WALL = BlockStateProperties.IN_WALL;
   protected static final VoxelShape Z_SHAPE = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
   protected static final VoxelShape X_SHAPE = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
   protected static final VoxelShape Z_SHAPE_LOW = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 13.0D, 10.0D);
   protected static final VoxelShape X_SHAPE_LOW = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 13.0D, 16.0D);
   protected static final VoxelShape Z_COLLISION_SHAPE = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 24.0D, 10.0D);
   protected static final VoxelShape X_COLLISION_SHAPE = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 24.0D, 16.0D);
   protected static final VoxelShape Z_OCCLUSION_SHAPE = Shapes.or(Block.box(0.0D, 5.0D, 7.0D, 2.0D, 16.0D, 9.0D), Block.box(14.0D, 5.0D, 7.0D, 16.0D, 16.0D, 9.0D));
   protected static final VoxelShape X_OCCLUSION_SHAPE = Shapes.or(Block.box(7.0D, 5.0D, 0.0D, 9.0D, 16.0D, 2.0D), Block.box(7.0D, 5.0D, 14.0D, 9.0D, 16.0D, 16.0D));
   protected static final VoxelShape Z_OCCLUSION_SHAPE_LOW = Shapes.or(Block.box(0.0D, 2.0D, 7.0D, 2.0D, 13.0D, 9.0D), Block.box(14.0D, 2.0D, 7.0D, 16.0D, 13.0D, 9.0D));
   protected static final VoxelShape X_OCCLUSION_SHAPE_LOW = Shapes.or(Block.box(7.0D, 2.0D, 0.0D, 9.0D, 13.0D, 2.0D), Block.box(7.0D, 2.0D, 14.0D, 9.0D, 13.0D, 16.0D));

   public FenceGateBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(OPEN, Boolean.valueOf(false))).setValue(POWERED, Boolean.valueOf(false))).setValue(IN_WALL, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return ((Boolean)blockState.getValue(IN_WALL)).booleanValue()?(((Direction)blockState.getValue(FACING)).getAxis() == Direction.Axis.X?X_SHAPE_LOW:Z_SHAPE_LOW):(((Direction)blockState.getValue(FACING)).getAxis() == Direction.Axis.X?X_SHAPE:Z_SHAPE);
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      Direction.Axis var7 = direction.getAxis();
      if(((Direction)var1.getValue(FACING)).getClockWise().getAxis() != var7) {
         return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
      } else {
         boolean var8 = this.isWall(var3) || this.isWall(levelAccessor.getBlockState(var5.relative(direction.getOpposite())));
         return (BlockState)var1.setValue(IN_WALL, Boolean.valueOf(var8));
      }
   }

   public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return ((Boolean)blockState.getValue(OPEN)).booleanValue()?Shapes.empty():(((Direction)blockState.getValue(FACING)).getAxis() == Direction.Axis.Z?Z_COLLISION_SHAPE:X_COLLISION_SHAPE);
   }

   public VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return ((Boolean)blockState.getValue(IN_WALL)).booleanValue()?(((Direction)blockState.getValue(FACING)).getAxis() == Direction.Axis.X?X_OCCLUSION_SHAPE_LOW:Z_OCCLUSION_SHAPE_LOW):(((Direction)blockState.getValue(FACING)).getAxis() == Direction.Axis.X?X_OCCLUSION_SHAPE:Z_OCCLUSION_SHAPE);
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      switch(pathComputationType) {
      case LAND:
         return ((Boolean)blockState.getValue(OPEN)).booleanValue();
      case WATER:
         return false;
      case AIR:
         return ((Boolean)blockState.getValue(OPEN)).booleanValue();
      default:
         return false;
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      Level var2 = blockPlaceContext.getLevel();
      BlockPos var3 = blockPlaceContext.getClickedPos();
      boolean var4 = var2.hasNeighborSignal(var3);
      Direction var5 = blockPlaceContext.getHorizontalDirection();
      Direction.Axis var6 = var5.getAxis();
      boolean var7 = var6 == Direction.Axis.Z && (this.isWall(var2.getBlockState(var3.west())) || this.isWall(var2.getBlockState(var3.east()))) || var6 == Direction.Axis.X && (this.isWall(var2.getBlockState(var3.north())) || this.isWall(var2.getBlockState(var3.south())));
      return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var5)).setValue(OPEN, Boolean.valueOf(var4))).setValue(POWERED, Boolean.valueOf(var4))).setValue(IN_WALL, Boolean.valueOf(var7));
   }

   private boolean isWall(BlockState blockState) {
      return blockState.getBlock().is(BlockTags.WALLS);
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(((Boolean)blockState.getValue(OPEN)).booleanValue()) {
         blockState = (BlockState)blockState.setValue(OPEN, Boolean.valueOf(false));
         level.setBlock(blockPos, blockState, 10);
      } else {
         Direction var7 = player.getDirection();
         if(blockState.getValue(FACING) == var7.getOpposite()) {
            blockState = (BlockState)blockState.setValue(FACING, var7);
         }

         blockState = (BlockState)blockState.setValue(OPEN, Boolean.valueOf(true));
         level.setBlock(blockPos, blockState, 10);
      }

      level.levelEvent(player, ((Boolean)blockState.getValue(OPEN)).booleanValue()?1008:1014, blockPos, 0);
      return true;
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      if(!level.isClientSide) {
         boolean var7 = level.hasNeighborSignal(var3);
         if(((Boolean)blockState.getValue(POWERED)).booleanValue() != var7) {
            level.setBlock(var3, (BlockState)((BlockState)blockState.setValue(POWERED, Boolean.valueOf(var7))).setValue(OPEN, Boolean.valueOf(var7)), 2);
            if(((Boolean)blockState.getValue(OPEN)).booleanValue() != var7) {
               level.levelEvent((Player)null, var7?1008:1014, var3, 0);
            }
         }

      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, OPEN, POWERED, IN_WALL});
   }

   public static boolean connectsToDirection(BlockState blockState, Direction direction) {
      return ((Direction)blockState.getValue(FACING)).getAxis() == direction.getClockWise().getAxis();
   }
}
