package net.minecraft.world.level.block;

import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VineBlock extends Block {
   public static final BooleanProperty UP = PipeBlock.UP;
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   public static final Map PROPERTY_BY_DIRECTION = (Map)PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((map$Entry) -> {
      return map$Entry.getKey() != Direction.DOWN;
   }).collect(Util.toMap());
   protected static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
   protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);

   public VineBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(UP, Boolean.valueOf(false))).setValue(NORTH, Boolean.valueOf(false))).setValue(EAST, Boolean.valueOf(false))).setValue(SOUTH, Boolean.valueOf(false))).setValue(WEST, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      VoxelShape voxelShape = Shapes.empty();
      if(((Boolean)blockState.getValue(UP)).booleanValue()) {
         voxelShape = Shapes.or(voxelShape, UP_AABB);
      }

      if(((Boolean)blockState.getValue(NORTH)).booleanValue()) {
         voxelShape = Shapes.or(voxelShape, SOUTH_AABB);
      }

      if(((Boolean)blockState.getValue(EAST)).booleanValue()) {
         voxelShape = Shapes.or(voxelShape, WEST_AABB);
      }

      if(((Boolean)blockState.getValue(SOUTH)).booleanValue()) {
         voxelShape = Shapes.or(voxelShape, NORTH_AABB);
      }

      if(((Boolean)blockState.getValue(WEST)).booleanValue()) {
         voxelShape = Shapes.or(voxelShape, EAST_AABB);
      }

      return voxelShape;
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return this.hasFaces(this.getUpdatedState(blockState, levelReader, blockPos));
   }

   private boolean hasFaces(BlockState blockState) {
      return this.countFaces(blockState) > 0;
   }

   private int countFaces(BlockState blockState) {
      int var2 = 0;

      for(BooleanProperty var4 : PROPERTY_BY_DIRECTION.values()) {
         if(((Boolean)blockState.getValue(var4)).booleanValue()) {
            ++var2;
         }
      }

      return var2;
   }

   private boolean canSupportAtFace(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      if(direction == Direction.DOWN) {
         return false;
      } else {
         BlockPos blockPos = blockPos.relative(direction);
         if(isAcceptableNeighbour(blockGetter, blockPos, direction)) {
            return true;
         } else if(direction.getAxis() == Direction.Axis.Y) {
            return false;
         } else {
            BooleanProperty var5 = (BooleanProperty)PROPERTY_BY_DIRECTION.get(direction);
            BlockState var6 = blockGetter.getBlockState(blockPos.above());
            return var6.getBlock() == this && ((Boolean)var6.getValue(var5)).booleanValue();
         }
      }
   }

   public static boolean isAcceptableNeighbour(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      BlockState var3 = blockGetter.getBlockState(blockPos);
      return Block.isFaceFull(var3.getCollisionShape(blockGetter, blockPos), direction.getOpposite());
   }

   private BlockState getUpdatedState(BlockState var1, BlockGetter blockGetter, BlockPos blockPos) {
      BlockPos blockPos = blockPos.above();
      if(((Boolean)var1.getValue(UP)).booleanValue()) {
         var1 = (BlockState)var1.setValue(UP, Boolean.valueOf(isAcceptableNeighbour(blockGetter, blockPos, Direction.DOWN)));
      }

      BlockState var5 = null;

      for(Direction var7 : Direction.Plane.HORIZONTAL) {
         BooleanProperty var8 = getPropertyForFace(var7);
         if(((Boolean)var1.getValue(var8)).booleanValue()) {
            boolean var9 = this.canSupportAtFace(blockGetter, blockPos, var7);
            if(!var9) {
               if(var5 == null) {
                  var5 = blockGetter.getBlockState(blockPos);
               }

               var9 = var5.getBlock() == this && ((Boolean)var5.getValue(var8)).booleanValue();
            }

            var1 = (BlockState)var1.setValue(var8, Boolean.valueOf(var9));
         }
      }

      return var1;
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(direction == Direction.DOWN) {
         return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
      } else {
         BlockState var7 = this.getUpdatedState(var1, levelAccessor, var5);
         return !this.hasFaces(var7)?Blocks.AIR.defaultBlockState():var7;
      }
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!level.isClientSide) {
         BlockState blockState = this.getUpdatedState(blockState, level, blockPos);
         if(blockState != blockState) {
            if(this.hasFaces(blockState)) {
               level.setBlock(blockPos, blockState, 2);
            } else {
               dropResources(blockState, level, blockPos);
               level.removeBlock(blockPos, false);
            }

         } else if(level.random.nextInt(4) == 0) {
            Direction var6 = Direction.getRandomFace(random);
            BlockPos var7 = blockPos.above();
            if(var6.getAxis().isHorizontal() && !((Boolean)blockState.getValue(getPropertyForFace(var6))).booleanValue()) {
               if(this.canSpread(level, blockPos)) {
                  BlockPos var8 = blockPos.relative(var6);
                  BlockState var9 = level.getBlockState(var8);
                  if(var9.isAir()) {
                     Direction var10 = var6.getClockWise();
                     Direction var11 = var6.getCounterClockWise();
                     boolean var12 = ((Boolean)blockState.getValue(getPropertyForFace(var10))).booleanValue();
                     boolean var13 = ((Boolean)blockState.getValue(getPropertyForFace(var11))).booleanValue();
                     BlockPos var14 = var8.relative(var10);
                     BlockPos var15 = var8.relative(var11);
                     if(var12 && isAcceptableNeighbour(level, var14, var10)) {
                        level.setBlock(var8, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(var10), Boolean.valueOf(true)), 2);
                     } else if(var13 && isAcceptableNeighbour(level, var15, var11)) {
                        level.setBlock(var8, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(var11), Boolean.valueOf(true)), 2);
                     } else {
                        Direction var16 = var6.getOpposite();
                        if(var12 && level.isEmptyBlock(var14) && isAcceptableNeighbour(level, blockPos.relative(var10), var16)) {
                           level.setBlock(var14, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(var16), Boolean.valueOf(true)), 2);
                        } else if(var13 && level.isEmptyBlock(var15) && isAcceptableNeighbour(level, blockPos.relative(var11), var16)) {
                           level.setBlock(var15, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(var16), Boolean.valueOf(true)), 2);
                        } else if((double)level.random.nextFloat() < 0.05D && isAcceptableNeighbour(level, var8.above(), Direction.UP)) {
                           level.setBlock(var8, (BlockState)this.defaultBlockState().setValue(UP, Boolean.valueOf(true)), 2);
                        }
                     }
                  } else if(isAcceptableNeighbour(level, var8, var6)) {
                     level.setBlock(blockPos, (BlockState)blockState.setValue(getPropertyForFace(var6), Boolean.valueOf(true)), 2);
                  }

               }
            } else {
               if(var6 == Direction.UP && blockPos.getY() < 255) {
                  if(this.canSupportAtFace(level, blockPos, var6)) {
                     level.setBlock(blockPos, (BlockState)blockState.setValue(UP, Boolean.valueOf(true)), 2);
                     return;
                  }

                  if(level.isEmptyBlock(var7)) {
                     if(!this.canSpread(level, blockPos)) {
                        return;
                     }

                     BlockState var8 = blockState;

                     for(Direction var10 : Direction.Plane.HORIZONTAL) {
                        if(random.nextBoolean() || !isAcceptableNeighbour(level, var7.relative(var10), Direction.UP)) {
                           var8 = (BlockState)var8.setValue(getPropertyForFace(var10), Boolean.valueOf(false));
                        }
                     }

                     if(this.hasHorizontalConnection(var8)) {
                        level.setBlock(var7, var8, 2);
                     }

                     return;
                  }
               }

               if(blockPos.getY() > 0) {
                  BlockPos var8 = blockPos.below();
                  BlockState var9 = level.getBlockState(var8);
                  if(var9.isAir() || var9.getBlock() == this) {
                     BlockState var10 = var9.isAir()?this.defaultBlockState():var9;
                     BlockState var11 = this.copyRandomFaces(blockState, var10, random);
                     if(var10 != var11 && this.hasHorizontalConnection(var11)) {
                        level.setBlock(var8, var11, 2);
                     }
                  }
               }

            }
         }
      }
   }

   private BlockState copyRandomFaces(BlockState var1, BlockState var2, Random random) {
      for(Direction var5 : Direction.Plane.HORIZONTAL) {
         if(random.nextBoolean()) {
            BooleanProperty var6 = getPropertyForFace(var5);
            if(((Boolean)var1.getValue(var6)).booleanValue()) {
               var2 = (BlockState)var2.setValue(var6, Boolean.valueOf(true));
            }
         }
      }

      return var2;
   }

   private boolean hasHorizontalConnection(BlockState blockState) {
      return ((Boolean)blockState.getValue(NORTH)).booleanValue() || ((Boolean)blockState.getValue(EAST)).booleanValue() || ((Boolean)blockState.getValue(SOUTH)).booleanValue() || ((Boolean)blockState.getValue(WEST)).booleanValue();
   }

   private boolean canSpread(BlockGetter blockGetter, BlockPos blockPos) {
      int var3 = 4;
      Iterable<BlockPos> var4 = BlockPos.betweenClosed(blockPos.getX() - 4, blockPos.getY() - 1, blockPos.getZ() - 4, blockPos.getX() + 4, blockPos.getY() + 1, blockPos.getZ() + 4);
      int var5 = 5;

      for(BlockPos var7 : var4) {
         if(blockGetter.getBlockState(var7).getBlock() == this) {
            --var5;
            if(var5 <= 0) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
      BlockState blockState = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos());
      return blockState.getBlock() == this?this.countFaces(blockState) < PROPERTY_BY_DIRECTION.size():super.canBeReplaced(blockState, blockPlaceContext);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockState blockState = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos());
      boolean var3 = blockState.getBlock() == this;
      BlockState var4 = var3?blockState:this.defaultBlockState();

      for(Direction var8 : blockPlaceContext.getNearestLookingDirections()) {
         if(var8 != Direction.DOWN) {
            BooleanProperty var9 = getPropertyForFace(var8);
            boolean var10 = var3 && ((Boolean)blockState.getValue(var9)).booleanValue();
            if(!var10 && this.canSupportAtFace(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos(), var8)) {
               return (BlockState)var4.setValue(var9, Boolean.valueOf(true));
            }
         }
      }

      return var3?var4:null;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{UP, NORTH, EAST, SOUTH, WEST});
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

   public static BooleanProperty getPropertyForFace(Direction direction) {
      return (BooleanProperty)PROPERTY_BY_DIRECTION.get(direction);
   }
}
