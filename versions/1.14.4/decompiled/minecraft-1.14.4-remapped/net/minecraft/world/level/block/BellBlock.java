package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BellBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final EnumProperty ATTACHMENT = BlockStateProperties.BELL_ATTACHMENT;
   private static final VoxelShape NORTH_SOUTH_FLOOR_SHAPE = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);
   private static final VoxelShape EAST_WEST_FLOOR_SHAPE = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
   private static final VoxelShape BELL_TOP_SHAPE = Block.box(5.0D, 6.0D, 5.0D, 11.0D, 13.0D, 11.0D);
   private static final VoxelShape BELL_BOTTOM_SHAPE = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 6.0D, 12.0D);
   private static final VoxelShape BELL_SHAPE = Shapes.or(BELL_BOTTOM_SHAPE, BELL_TOP_SHAPE);
   private static final VoxelShape NORTH_SOUTH_BETWEEN = Shapes.or(BELL_SHAPE, Block.box(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 16.0D));
   private static final VoxelShape EAST_WEST_BETWEEN = Shapes.or(BELL_SHAPE, Block.box(0.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
   private static final VoxelShape TO_WEST = Shapes.or(BELL_SHAPE, Block.box(0.0D, 13.0D, 7.0D, 13.0D, 15.0D, 9.0D));
   private static final VoxelShape TO_EAST = Shapes.or(BELL_SHAPE, Block.box(3.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
   private static final VoxelShape TO_NORTH = Shapes.or(BELL_SHAPE, Block.box(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 13.0D));
   private static final VoxelShape TO_SOUTH = Shapes.or(BELL_SHAPE, Block.box(7.0D, 13.0D, 3.0D, 9.0D, 15.0D, 16.0D));
   private static final VoxelShape CEILING_SHAPE = Shapes.or(BELL_SHAPE, Block.box(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D));

   public BellBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(ATTACHMENT, BellAttachType.FLOOR));
   }

   public void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
      if(entity instanceof AbstractArrow) {
         Entity entity = ((AbstractArrow)entity).getOwner();
         Player var6 = entity instanceof Player?(Player)entity:null;
         this.onHit(level, blockState, level.getBlockEntity(blockHitResult.getBlockPos()), blockHitResult, var6, true);
      }

   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      return this.onHit(level, blockState, level.getBlockEntity(blockPos), blockHitResult, player, true);
   }

   public boolean onHit(Level level, BlockState blockState, @Nullable BlockEntity blockEntity, BlockHitResult blockHitResult, @Nullable Player player, boolean var6) {
      Direction var7 = blockHitResult.getDirection();
      BlockPos var8 = blockHitResult.getBlockPos();
      boolean var9 = !var6 || this.isProperHit(blockState, var7, blockHitResult.getLocation().y - (double)var8.getY());
      if(!level.isClientSide && blockEntity instanceof BellBlockEntity && var9) {
         ((BellBlockEntity)blockEntity).onHit(var7);
         this.ring(level, var8);
         if(player != null) {
            player.awardStat(Stats.BELL_RING);
         }

         return true;
      } else {
         return true;
      }
   }

   private boolean isProperHit(BlockState blockState, Direction direction, double var3) {
      if(direction.getAxis() != Direction.Axis.Y && var3 <= 0.8123999834060669D) {
         Direction direction = (Direction)blockState.getValue(FACING);
         BellAttachType var6 = (BellAttachType)blockState.getValue(ATTACHMENT);
         switch(var6) {
         case FLOOR:
            return direction.getAxis() == direction.getAxis();
         case SINGLE_WALL:
         case DOUBLE_WALL:
            return direction.getAxis() != direction.getAxis();
         case CEILING:
            return true;
         default:
            return false;
         }
      } else {
         return false;
      }
   }

   private void ring(Level level, BlockPos blockPos) {
      level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0F, 1.0F);
   }

   private VoxelShape getVoxelShape(BlockState blockState) {
      Direction var2 = (Direction)blockState.getValue(FACING);
      BellAttachType var3 = (BellAttachType)blockState.getValue(ATTACHMENT);
      return var3 == BellAttachType.FLOOR?(var2 != Direction.NORTH && var2 != Direction.SOUTH?EAST_WEST_FLOOR_SHAPE:NORTH_SOUTH_FLOOR_SHAPE):(var3 == BellAttachType.CEILING?CEILING_SHAPE:(var3 == BellAttachType.DOUBLE_WALL?(var2 != Direction.NORTH && var2 != Direction.SOUTH?EAST_WEST_BETWEEN:NORTH_SOUTH_BETWEEN):(var2 == Direction.NORTH?TO_NORTH:(var2 == Direction.SOUTH?TO_SOUTH:(var2 == Direction.EAST?TO_EAST:TO_WEST)))));
   }

   public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return this.getVoxelShape(blockState);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return this.getVoxelShape(blockState);
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      Direction var3 = blockPlaceContext.getClickedFace();
      BlockPos var4 = blockPlaceContext.getClickedPos();
      Level var5 = blockPlaceContext.getLevel();
      Direction.Axis var6 = var3.getAxis();
      if(var6 == Direction.Axis.Y) {
         BlockState blockState = (BlockState)((BlockState)this.defaultBlockState().setValue(ATTACHMENT, var3 == Direction.DOWN?BellAttachType.CEILING:BellAttachType.FLOOR)).setValue(FACING, blockPlaceContext.getHorizontalDirection());
         if(blockState.canSurvive(blockPlaceContext.getLevel(), var4)) {
            return blockState;
         }
      } else {
         boolean var7 = var6 == Direction.Axis.X && var5.getBlockState(var4.west()).isFaceSturdy(var5, var4.west(), Direction.EAST) && var5.getBlockState(var4.east()).isFaceSturdy(var5, var4.east(), Direction.WEST) || var6 == Direction.Axis.Z && var5.getBlockState(var4.north()).isFaceSturdy(var5, var4.north(), Direction.SOUTH) && var5.getBlockState(var4.south()).isFaceSturdy(var5, var4.south(), Direction.NORTH);
         BlockState blockState = (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var3.getOpposite())).setValue(ATTACHMENT, var7?BellAttachType.DOUBLE_WALL:BellAttachType.SINGLE_WALL);
         if(blockState.canSurvive(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos())) {
            return blockState;
         }

         boolean var8 = var5.getBlockState(var4.below()).isFaceSturdy(var5, var4.below(), Direction.UP);
         blockState = (BlockState)blockState.setValue(ATTACHMENT, var8?BellAttachType.FLOOR:BellAttachType.CEILING);
         if(blockState.canSurvive(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos())) {
            return blockState;
         }
      }

      return null;
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      BellAttachType var7 = (BellAttachType)var1.getValue(ATTACHMENT);
      Direction var8 = getConnectedDirection(var1).getOpposite();
      if(var8 == direction && !var1.canSurvive(levelAccessor, var5) && var7 != BellAttachType.DOUBLE_WALL) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if(direction.getAxis() == ((Direction)var1.getValue(FACING)).getAxis()) {
            if(var7 == BellAttachType.DOUBLE_WALL && !var3.isFaceSturdy(levelAccessor, var6, direction)) {
               return (BlockState)((BlockState)var1.setValue(ATTACHMENT, BellAttachType.SINGLE_WALL)).setValue(FACING, direction.getOpposite());
            }

            if(var7 == BellAttachType.SINGLE_WALL && var8.getOpposite() == direction && var3.isFaceSturdy(levelAccessor, var6, (Direction)var1.getValue(FACING))) {
               return (BlockState)var1.setValue(ATTACHMENT, BellAttachType.DOUBLE_WALL);
            }
         }

         return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
      }
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return FaceAttachedHorizontalDirectionalBlock.canAttach(levelReader, blockPos, getConnectedDirection(blockState).getOpposite());
   }

   private static Direction getConnectedDirection(BlockState blockState) {
      switch((BellAttachType)blockState.getValue(ATTACHMENT)) {
      case FLOOR:
         return Direction.UP;
      case CEILING:
         return Direction.DOWN;
      default:
         return ((Direction)blockState.getValue(FACING)).getOpposite();
      }
   }

   public PushReaction getPistonPushReaction(BlockState blockState) {
      return PushReaction.DESTROY;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, ATTACHMENT});
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new BellBlockEntity();
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
