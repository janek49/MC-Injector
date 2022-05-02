package net.minecraft.world.level.block;

import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StairBlock extends Block implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty HALF = BlockStateProperties.HALF;
   public static final EnumProperty SHAPE = BlockStateProperties.STAIRS_SHAPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape TOP_AABB = SlabBlock.TOP_AABB;
   protected static final VoxelShape BOTTOM_AABB = SlabBlock.BOTTOM_AABB;
   protected static final VoxelShape OCTET_NNN = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
   protected static final VoxelShape OCTET_NNP = Block.box(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
   protected static final VoxelShape OCTET_NPN = Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
   protected static final VoxelShape OCTET_NPP = Block.box(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
   protected static final VoxelShape OCTET_PNN = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
   protected static final VoxelShape OCTET_PNP = Block.box(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
   protected static final VoxelShape OCTET_PPN = Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
   protected static final VoxelShape OCTET_PPP = Block.box(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape[] TOP_SHAPES = makeShapes(TOP_AABB, OCTET_NNN, OCTET_PNN, OCTET_NNP, OCTET_PNP);
   protected static final VoxelShape[] BOTTOM_SHAPES = makeShapes(BOTTOM_AABB, OCTET_NPN, OCTET_PPN, OCTET_NPP, OCTET_PPP);
   private static final int[] SHAPE_BY_STATE = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
   private final Block base;
   private final BlockState baseState;

   private static VoxelShape[] makeShapes(VoxelShape var0, VoxelShape var1, VoxelShape var2, VoxelShape var3, VoxelShape var4) {
      return (VoxelShape[])IntStream.range(0, 16).mapToObj((var5) -> {
         return makeStairShape(var5, var0, var1, var2, var3, var4);
      }).toArray((i) -> {
         return new VoxelShape[i];
      });
   }

   private static VoxelShape makeStairShape(int var0, VoxelShape var1, VoxelShape var2, VoxelShape var3, VoxelShape var4, VoxelShape var5) {
      VoxelShape var6 = var1;
      if((var0 & 1) != 0) {
         var6 = Shapes.or(var1, var2);
      }

      if((var0 & 2) != 0) {
         var6 = Shapes.or(var6, var3);
      }

      if((var0 & 4) != 0) {
         var6 = Shapes.or(var6, var4);
      }

      if((var0 & 8) != 0) {
         var6 = Shapes.or(var6, var5);
      }

      return var6;
   }

   protected StairBlock(BlockState baseState, Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(HALF, Half.BOTTOM)).setValue(SHAPE, StairsShape.STRAIGHT)).setValue(WATERLOGGED, Boolean.valueOf(false)));
      this.base = baseState.getBlock();
      this.baseState = baseState;
   }

   public boolean useShapeForLightOcclusion(BlockState blockState) {
      return true;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return (blockState.getValue(HALF) == Half.TOP?TOP_SHAPES:BOTTOM_SHAPES)[SHAPE_BY_STATE[this.getShapeIndex(blockState)]];
   }

   private int getShapeIndex(BlockState blockState) {
      return ((StairsShape)blockState.getValue(SHAPE)).ordinal() * 4 + ((Direction)blockState.getValue(FACING)).get2DDataValue();
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      this.base.animateTick(blockState, level, blockPos, random);
   }

   public void attack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
      this.baseState.attack(level, blockPos, player);
   }

   public void destroy(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
      this.base.destroy(levelAccessor, blockPos, blockState);
   }

   public float getExplosionResistance() {
      return this.base.getExplosionResistance();
   }

   public BlockLayer getRenderLayer() {
      return this.base.getRenderLayer();
   }

   public int getTickDelay(LevelReader levelReader) {
      return this.base.getTickDelay(levelReader);
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var1.getBlock() != var1.getBlock()) {
         this.baseState.neighborChanged(level, blockPos, Blocks.AIR, blockPos, false);
         this.base.onPlace(this.baseState, level, blockPos, var4, false);
      }
   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var1.getBlock() != var4.getBlock()) {
         this.baseState.onRemove(level, blockPos, var4, var5);
      }
   }

   public void stepOn(Level level, BlockPos blockPos, Entity entity) {
      this.base.stepOn(level, blockPos, entity);
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      this.base.tick(blockState, level, blockPos, random);
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      return this.baseState.use(level, player, interactionHand, blockHitResult);
   }

   public void wasExploded(Level level, BlockPos blockPos, Explosion explosion) {
      this.base.wasExploded(level, blockPos, explosion);
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      Direction var2 = blockPlaceContext.getClickedFace();
      BlockPos var3 = blockPlaceContext.getClickedPos();
      FluidState var4 = blockPlaceContext.getLevel().getFluidState(var3);
      BlockState var5 = (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection())).setValue(HALF, var2 != Direction.DOWN && (var2 == Direction.UP || blockPlaceContext.getClickLocation().y - (double)var3.getY() <= 0.5D)?Half.BOTTOM:Half.TOP)).setValue(WATERLOGGED, Boolean.valueOf(var4.getType() == Fluids.WATER));
      return (BlockState)var5.setValue(SHAPE, getStairsShape(var5, blockPlaceContext.getLevel(), var3));
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(((Boolean)var1.getValue(WATERLOGGED)).booleanValue()) {
         levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      }

      return direction.getAxis().isHorizontal()?(BlockState)var1.setValue(SHAPE, getStairsShape(var1, levelAccessor, var5)):super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   private static StairsShape getStairsShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      Direction var3 = (Direction)blockState.getValue(FACING);
      BlockState var4 = blockGetter.getBlockState(blockPos.relative(var3));
      if(isStairs(var4) && blockState.getValue(HALF) == var4.getValue(HALF)) {
         Direction var5 = (Direction)var4.getValue(FACING);
         if(var5.getAxis() != ((Direction)blockState.getValue(FACING)).getAxis() && canTakeShape(blockState, blockGetter, blockPos, var5.getOpposite())) {
            if(var5 == var3.getCounterClockWise()) {
               return StairsShape.OUTER_LEFT;
            }

            return StairsShape.OUTER_RIGHT;
         }
      }

      BlockState var5 = blockGetter.getBlockState(blockPos.relative(var3.getOpposite()));
      if(isStairs(var5) && blockState.getValue(HALF) == var5.getValue(HALF)) {
         Direction var6 = (Direction)var5.getValue(FACING);
         if(var6.getAxis() != ((Direction)blockState.getValue(FACING)).getAxis() && canTakeShape(blockState, blockGetter, blockPos, var6)) {
            if(var6 == var3.getCounterClockWise()) {
               return StairsShape.INNER_LEFT;
            }

            return StairsShape.INNER_RIGHT;
         }
      }

      return StairsShape.STRAIGHT;
   }

   private static boolean canTakeShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      BlockState blockState = blockGetter.getBlockState(blockPos.relative(direction));
      return !isStairs(blockState) || blockState.getValue(FACING) != blockState.getValue(FACING) || blockState.getValue(HALF) != blockState.getValue(HALF);
   }

   public static boolean isStairs(BlockState blockState) {
      return blockState.getBlock() instanceof StairBlock;
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      Direction var3 = (Direction)var1.getValue(FACING);
      StairsShape var4 = (StairsShape)var1.getValue(SHAPE);
      switch(mirror) {
      case LEFT_RIGHT:
         if(var3.getAxis() == Direction.Axis.Z) {
            switch(var4) {
            case INNER_LEFT:
               return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
            case INNER_RIGHT:
               return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
            case OUTER_LEFT:
               return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
               return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
            default:
               return var1.rotate(Rotation.CLOCKWISE_180);
            }
         }
         break;
      case FRONT_BACK:
         if(var3.getAxis() == Direction.Axis.X) {
            switch(var4) {
            case INNER_LEFT:
               return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
            case INNER_RIGHT:
               return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
            case OUTER_LEFT:
               return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
               return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
            case STRAIGHT:
               return var1.rotate(Rotation.CLOCKWISE_180);
            }
         }
      }

      return super.mirror(var1, mirror);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, HALF, SHAPE, WATERLOGGED});
   }

   public FluidState getFluidState(BlockState blockState) {
      return ((Boolean)blockState.getValue(WATERLOGGED)).booleanValue()?Fluids.WATER.getSource(false):super.getFluidState(blockState);
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
