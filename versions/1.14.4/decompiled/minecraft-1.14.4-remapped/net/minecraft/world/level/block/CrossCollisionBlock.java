package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrossCollisionBlock extends Block implements SimpleWaterloggedBlock {
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final Map PROPERTY_BY_DIRECTION = (Map)PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((map$Entry) -> {
      return ((Direction)map$Entry.getKey()).getAxis().isHorizontal();
   }).collect(Util.toMap());
   protected final VoxelShape[] collisionShapeByIndex;
   protected final VoxelShape[] shapeByIndex;
   private final Object2IntMap stateToIndex = new Object2IntOpenHashMap();

   protected CrossCollisionBlock(float var1, float var2, float var3, float var4, float var5, Block.Properties block$Properties) {
      super(block$Properties);
      this.collisionShapeByIndex = this.makeShapes(var1, var2, var5, 0.0F, var5);
      this.shapeByIndex = this.makeShapes(var1, var2, var3, 0.0F, var4);
   }

   protected VoxelShape[] makeShapes(float var1, float var2, float var3, float var4, float var5) {
      float var6 = 8.0F - var1;
      float var7 = 8.0F + var1;
      float var8 = 8.0F - var2;
      float var9 = 8.0F + var2;
      VoxelShape var10 = Block.box((double)var6, 0.0D, (double)var6, (double)var7, (double)var3, (double)var7);
      VoxelShape var11 = Block.box((double)var8, (double)var4, 0.0D, (double)var9, (double)var5, (double)var9);
      VoxelShape var12 = Block.box((double)var8, (double)var4, (double)var8, (double)var9, (double)var5, 16.0D);
      VoxelShape var13 = Block.box(0.0D, (double)var4, (double)var8, (double)var9, (double)var5, (double)var9);
      VoxelShape var14 = Block.box((double)var8, (double)var4, (double)var8, 16.0D, (double)var5, (double)var9);
      VoxelShape var15 = Shapes.or(var11, var14);
      VoxelShape var16 = Shapes.or(var12, var13);
      VoxelShape[] vars17 = new VoxelShape[]{Shapes.empty(), var12, var13, var16, var11, Shapes.or(var12, var11), Shapes.or(var13, var11), Shapes.or(var16, var11), var14, Shapes.or(var12, var14), Shapes.or(var13, var14), Shapes.or(var16, var14), var15, Shapes.or(var12, var15), Shapes.or(var13, var15), Shapes.or(var16, var15)};

      for(int var18 = 0; var18 < 16; ++var18) {
         vars17[var18] = Shapes.or(var10, vars17[var18]);
      }

      return vars17;
   }

   public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return !((Boolean)blockState.getValue(WATERLOGGED)).booleanValue();
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return this.shapeByIndex[this.getAABBIndex(blockState)];
   }

   public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return this.collisionShapeByIndex[this.getAABBIndex(blockState)];
   }

   private static int indexFor(Direction direction) {
      return 1 << direction.get2DDataValue();
   }

   protected int getAABBIndex(BlockState blockState) {
      return this.stateToIndex.computeIntIfAbsent(blockState, (blockState) -> {
         int var1 = 0;
         if(((Boolean)blockState.getValue(NORTH)).booleanValue()) {
            var1 |= indexFor(Direction.NORTH);
         }

         if(((Boolean)blockState.getValue(EAST)).booleanValue()) {
            var1 |= indexFor(Direction.EAST);
         }

         if(((Boolean)blockState.getValue(SOUTH)).booleanValue()) {
            var1 |= indexFor(Direction.SOUTH);
         }

         if(((Boolean)blockState.getValue(WEST)).booleanValue()) {
            var1 |= indexFor(Direction.WEST);
         }

         return var1;
      });
   }

   public FluidState getFluidState(BlockState blockState) {
      return ((Boolean)blockState.getValue(WATERLOGGED)).booleanValue()?Fluids.WATER.getSource(false):super.getFluidState(blockState);
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      switch(rotation) {
      case CLOCKWISE_180:
         return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, var1.getValue(SOUTH))).setValue(EAST, var1.getValue(WEST))).setValue(SOUTH, var1.getValue(NORTH))).setValue(WEST, var1.getValue(EAST));
      case COUNTERCLOCKWISE_90:
         return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, var1.getValue(EAST))).setValue(EAST, var1.getValue(SOUTH))).setValue(SOUTH, var1.getValue(WEST))).setValue(WEST, var1.getValue(NORTH));
      case CLOCKWISE_90:
         return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, var1.getValue(WEST))).setValue(EAST, var1.getValue(NORTH))).setValue(SOUTH, var1.getValue(EAST))).setValue(WEST, var1.getValue(SOUTH));
      default:
         return var1;
      }
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      switch(mirror) {
      case LEFT_RIGHT:
         return (BlockState)((BlockState)var1.setValue(NORTH, var1.getValue(SOUTH))).setValue(SOUTH, var1.getValue(NORTH));
      case FRONT_BACK:
         return (BlockState)((BlockState)var1.setValue(EAST, var1.getValue(WEST))).setValue(WEST, var1.getValue(EAST));
      default:
         return super.mirror(var1, mirror);
      }
   }
}
