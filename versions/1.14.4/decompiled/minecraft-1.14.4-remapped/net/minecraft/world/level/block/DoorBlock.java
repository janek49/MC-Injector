package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DoorBlock extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final EnumProperty HINGE = BlockStateProperties.DOOR_HINGE;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final EnumProperty HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
   protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

   protected DoorBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(OPEN, Boolean.valueOf(false))).setValue(HINGE, DoorHingeSide.LEFT)).setValue(POWERED, Boolean.valueOf(false))).setValue(HALF, DoubleBlockHalf.LOWER));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      Direction var5 = (Direction)blockState.getValue(FACING);
      boolean var6 = !((Boolean)blockState.getValue(OPEN)).booleanValue();
      boolean var7 = blockState.getValue(HINGE) == DoorHingeSide.RIGHT;
      switch(var5) {
      case EAST:
      default:
         return var6?EAST_AABB:(var7?NORTH_AABB:SOUTH_AABB);
      case SOUTH:
         return var6?SOUTH_AABB:(var7?EAST_AABB:WEST_AABB);
      case WEST:
         return var6?WEST_AABB:(var7?SOUTH_AABB:NORTH_AABB);
      case NORTH:
         return var6?NORTH_AABB:(var7?WEST_AABB:EAST_AABB);
      }
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      DoubleBlockHalf var7 = (DoubleBlockHalf)var1.getValue(HALF);
      return direction.getAxis() == Direction.Axis.Y && var7 == DoubleBlockHalf.LOWER == (direction == Direction.UP)?(var3.getBlock() == this && var3.getValue(HALF) != var7?(BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(FACING, var3.getValue(FACING))).setValue(OPEN, var3.getValue(OPEN))).setValue(HINGE, var3.getValue(HINGE))).setValue(POWERED, var3.getValue(POWERED)):Blocks.AIR.defaultBlockState()):(var7 == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !var1.canSurvive(levelAccessor, var5)?Blocks.AIR.defaultBlockState():super.updateShape(var1, direction, var3, levelAccessor, var5, var6));
   }

   public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
      super.playerDestroy(level, player, blockPos, Blocks.AIR.defaultBlockState(), blockEntity, itemStack);
   }

   public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
      DoubleBlockHalf var5 = (DoubleBlockHalf)blockState.getValue(HALF);
      BlockPos var6 = var5 == DoubleBlockHalf.LOWER?blockPos.above():blockPos.below();
      BlockState var7 = level.getBlockState(var6);
      if(var7.getBlock() == this && var7.getValue(HALF) != var5) {
         level.setBlock(var6, Blocks.AIR.defaultBlockState(), 35);
         level.levelEvent(player, 2001, var6, Block.getId(var7));
         ItemStack var8 = player.getMainHandItem();
         if(!level.isClientSide && !player.isCreative()) {
            Block.dropResources(blockState, level, blockPos, (BlockEntity)null, player, var8);
            Block.dropResources(var7, level, var6, (BlockEntity)null, player, var8);
         }
      }

      super.playerWillDestroy(level, blockPos, blockState, player);
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

   private int getCloseSound() {
      return this.material == Material.METAL?1011:1012;
   }

   private int getOpenSound() {
      return this.material == Material.METAL?1005:1006;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockPos var2 = blockPlaceContext.getClickedPos();
      if(var2.getY() < 255 && blockPlaceContext.getLevel().getBlockState(var2.above()).canBeReplaced(blockPlaceContext)) {
         Level var3 = blockPlaceContext.getLevel();
         boolean var4 = var3.hasNeighborSignal(var2) || var3.hasNeighborSignal(var2.above());
         return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection())).setValue(HINGE, this.getHinge(blockPlaceContext))).setValue(POWERED, Boolean.valueOf(var4))).setValue(OPEN, Boolean.valueOf(var4))).setValue(HALF, DoubleBlockHalf.LOWER);
      } else {
         return null;
      }
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
      level.setBlock(blockPos.above(), (BlockState)blockState.setValue(HALF, DoubleBlockHalf.UPPER), 3);
   }

   private DoorHingeSide getHinge(BlockPlaceContext blockPlaceContext) {
      BlockGetter var2 = blockPlaceContext.getLevel();
      BlockPos var3 = blockPlaceContext.getClickedPos();
      Direction var4 = blockPlaceContext.getHorizontalDirection();
      BlockPos var5 = var3.above();
      Direction var6 = var4.getCounterClockWise();
      BlockPos var7 = var3.relative(var6);
      BlockState var8 = var2.getBlockState(var7);
      BlockPos var9 = var5.relative(var6);
      BlockState var10 = var2.getBlockState(var9);
      Direction var11 = var4.getClockWise();
      BlockPos var12 = var3.relative(var11);
      BlockState var13 = var2.getBlockState(var12);
      BlockPos var14 = var5.relative(var11);
      BlockState var15 = var2.getBlockState(var14);
      int var16 = (var8.isCollisionShapeFullBlock(var2, var7)?-1:0) + (var10.isCollisionShapeFullBlock(var2, var9)?-1:0) + (var13.isCollisionShapeFullBlock(var2, var12)?1:0) + (var15.isCollisionShapeFullBlock(var2, var14)?1:0);
      boolean var17 = var8.getBlock() == this && var8.getValue(HALF) == DoubleBlockHalf.LOWER;
      boolean var18 = var13.getBlock() == this && var13.getValue(HALF) == DoubleBlockHalf.LOWER;
      if((!var17 || var18) && var16 <= 0) {
         if((!var18 || var17) && var16 >= 0) {
            int var19 = var4.getStepX();
            int var20 = var4.getStepZ();
            Vec3 var21 = blockPlaceContext.getClickLocation();
            double var22 = var21.x - (double)var3.getX();
            double var24 = var21.z - (double)var3.getZ();
            return (var19 >= 0 || var24 >= 0.5D) && (var19 <= 0 || var24 <= 0.5D) && (var20 >= 0 || var22 <= 0.5D) && (var20 <= 0 || var22 >= 0.5D)?DoorHingeSide.LEFT:DoorHingeSide.RIGHT;
         } else {
            return DoorHingeSide.LEFT;
         }
      } else {
         return DoorHingeSide.RIGHT;
      }
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(this.material == Material.METAL) {
         return false;
      } else {
         blockState = (BlockState)blockState.cycle(OPEN);
         level.setBlock(blockPos, blockState, 10);
         level.levelEvent(player, ((Boolean)blockState.getValue(OPEN)).booleanValue()?this.getOpenSound():this.getCloseSound(), blockPos, 0);
         return true;
      }
   }

   public void setOpen(Level level, BlockPos blockPos, boolean var3) {
      BlockState var4 = level.getBlockState(blockPos);
      if(var4.getBlock() == this && ((Boolean)var4.getValue(OPEN)).booleanValue() != var3) {
         level.setBlock(blockPos, (BlockState)var4.setValue(OPEN, Boolean.valueOf(var3)), 10);
         this.playSound(level, blockPos, var3);
      }
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      boolean var7 = level.hasNeighborSignal(var3) || level.hasNeighborSignal(var3.relative(blockState.getValue(HALF) == DoubleBlockHalf.LOWER?Direction.UP:Direction.DOWN));
      if(block != this && var7 != ((Boolean)blockState.getValue(POWERED)).booleanValue()) {
         if(var7 != ((Boolean)blockState.getValue(OPEN)).booleanValue()) {
            this.playSound(level, var3, var7);
         }

         level.setBlock(var3, (BlockState)((BlockState)blockState.setValue(POWERED, Boolean.valueOf(var7))).setValue(OPEN, Boolean.valueOf(var7)), 2);
      }

   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockPos blockPos = blockPos.below();
      BlockState var5 = levelReader.getBlockState(blockPos);
      return blockState.getValue(HALF) == DoubleBlockHalf.LOWER?var5.isFaceSturdy(levelReader, blockPos, Direction.UP):var5.getBlock() == this;
   }

   private void playSound(Level level, BlockPos blockPos, boolean var3) {
      level.levelEvent((Player)null, var3?this.getOpenSound():this.getCloseSound(), blockPos, 0);
   }

   public PushReaction getPistonPushReaction(BlockState blockState) {
      return PushReaction.DESTROY;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return mirror == Mirror.NONE?var1:(BlockState)var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING))).cycle(HINGE);
   }

   public long getSeed(BlockState blockState, BlockPos blockPos) {
      return Mth.getSeed(blockPos.getX(), blockPos.below(blockState.getValue(HALF) == DoubleBlockHalf.LOWER?0:1).getY(), blockPos.getZ());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{HALF, FACING, OPEN, HINGE, POWERED});
   }
}
