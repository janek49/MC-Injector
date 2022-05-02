package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedStoneWireBlock extends Block {
   public static final EnumProperty NORTH = BlockStateProperties.NORTH_REDSTONE;
   public static final EnumProperty EAST = BlockStateProperties.EAST_REDSTONE;
   public static final EnumProperty SOUTH = BlockStateProperties.SOUTH_REDSTONE;
   public static final EnumProperty WEST = BlockStateProperties.WEST_REDSTONE;
   public static final IntegerProperty POWER = BlockStateProperties.POWER;
   public static final Map PROPERTY_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));
   protected static final VoxelShape[] SHAPE_BY_INDEX = new VoxelShape[]{Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.box(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.box(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.box(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.box(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.box(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.box(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.box(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.box(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.box(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.box(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.box(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};
   private boolean shouldSignal = true;
   private final Set toUpdate = Sets.newHashSet();

   public RedStoneWireBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, RedstoneSide.NONE)).setValue(EAST, RedstoneSide.NONE)).setValue(SOUTH, RedstoneSide.NONE)).setValue(WEST, RedstoneSide.NONE)).setValue(POWER, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE_BY_INDEX[getAABBIndex(blockState)];
   }

   private static int getAABBIndex(BlockState blockState) {
      int var1 = 0;
      boolean var2 = blockState.getValue(NORTH) != RedstoneSide.NONE;
      boolean var3 = blockState.getValue(EAST) != RedstoneSide.NONE;
      boolean var4 = blockState.getValue(SOUTH) != RedstoneSide.NONE;
      boolean var5 = blockState.getValue(WEST) != RedstoneSide.NONE;
      if(var2 || var4 && !var2 && !var3 && !var5) {
         var1 |= 1 << Direction.NORTH.get2DDataValue();
      }

      if(var3 || var5 && !var2 && !var3 && !var4) {
         var1 |= 1 << Direction.EAST.get2DDataValue();
      }

      if(var4 || var2 && !var3 && !var4 && !var5) {
         var1 |= 1 << Direction.SOUTH.get2DDataValue();
      }

      if(var5 || var3 && !var2 && !var4 && !var5) {
         var1 |= 1 << Direction.WEST.get2DDataValue();
      }

      return var1;
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockGetter var2 = blockPlaceContext.getLevel();
      BlockPos var3 = blockPlaceContext.getClickedPos();
      return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(WEST, this.getConnectingSide(var2, var3, Direction.WEST))).setValue(EAST, this.getConnectingSide(var2, var3, Direction.EAST))).setValue(NORTH, this.getConnectingSide(var2, var3, Direction.NORTH))).setValue(SOUTH, this.getConnectingSide(var2, var3, Direction.SOUTH));
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return direction == Direction.DOWN?var1:(direction == Direction.UP?(BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(WEST, this.getConnectingSide(levelAccessor, var5, Direction.WEST))).setValue(EAST, this.getConnectingSide(levelAccessor, var5, Direction.EAST))).setValue(NORTH, this.getConnectingSide(levelAccessor, var5, Direction.NORTH))).setValue(SOUTH, this.getConnectingSide(levelAccessor, var5, Direction.SOUTH)):(BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(direction), this.getConnectingSide(levelAccessor, var5, direction)));
   }

   public void updateIndirectNeighbourShapes(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, int var4) {
      BlockPos.PooledMutableBlockPos var5 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var6 = null;

      try {
         for(Direction var8 : Direction.Plane.HORIZONTAL) {
            RedstoneSide var9 = (RedstoneSide)blockState.getValue((Property)PROPERTY_BY_DIRECTION.get(var8));
            if(var9 != RedstoneSide.NONE && levelAccessor.getBlockState(var5.set((Vec3i)blockPos).move(var8)).getBlock() != this) {
               var5.move(Direction.DOWN);
               BlockState var10 = levelAccessor.getBlockState(var5);
               if(var10.getBlock() != Blocks.OBSERVER) {
                  BlockPos var11 = var5.relative(var8.getOpposite());
                  BlockState var12 = var10.updateShape(var8.getOpposite(), levelAccessor.getBlockState(var11), levelAccessor, var5, var11);
                  updateOrDestroy(var10, var12, levelAccessor, var5, var4);
               }

               var5.set((Vec3i)blockPos).move(var8).move(Direction.UP);
               BlockState var11 = levelAccessor.getBlockState(var5);
               if(var11.getBlock() != Blocks.OBSERVER) {
                  BlockPos var12 = var5.relative(var8.getOpposite());
                  BlockState var13 = var11.updateShape(var8.getOpposite(), levelAccessor.getBlockState(var12), levelAccessor, var5, var12);
                  updateOrDestroy(var11, var13, levelAccessor, var5, var4);
               }
            }
         }
      } catch (Throwable var21) {
         var6 = var21;
         throw var21;
      } finally {
         if(var5 != null) {
            if(var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var20) {
                  var6.addSuppressed(var20);
               }
            } else {
               var5.close();
            }
         }

      }

   }

   private RedstoneSide getConnectingSide(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      BlockPos blockPos = blockPos.relative(direction);
      BlockState var5 = blockGetter.getBlockState(blockPos);
      BlockPos var6 = blockPos.above();
      BlockState var7 = blockGetter.getBlockState(var6);
      if(!var7.isRedstoneConductor(blockGetter, var6)) {
         boolean var8 = var5.isFaceSturdy(blockGetter, blockPos, Direction.UP) || var5.getBlock() == Blocks.HOPPER;
         if(var8 && shouldConnectTo(blockGetter.getBlockState(blockPos.above()))) {
            if(var5.isCollisionShapeFullBlock(blockGetter, blockPos)) {
               return RedstoneSide.UP;
            }

            return RedstoneSide.SIDE;
         }
      }

      return !shouldConnectTo(var5, direction) && (var5.isRedstoneConductor(blockGetter, blockPos) || !shouldConnectTo(blockGetter.getBlockState(blockPos.below())))?RedstoneSide.NONE:RedstoneSide.SIDE;
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockPos blockPos = blockPos.below();
      BlockState var5 = levelReader.getBlockState(blockPos);
      return var5.isFaceSturdy(levelReader, blockPos, Direction.UP) || var5.getBlock() == Blocks.HOPPER;
   }

   private BlockState updatePowerStrength(Level level, BlockPos blockPos, BlockState var3) {
      var3 = this.updatePowerStrengthImpl(level, blockPos, var3);
      List<BlockPos> var4 = Lists.newArrayList(this.toUpdate);
      this.toUpdate.clear();

      for(BlockPos var6 : var4) {
         level.updateNeighborsAt(var6, this);
      }

      return var3;
   }

   private BlockState updatePowerStrengthImpl(Level level, BlockPos blockPos, BlockState var3) {
      BlockState var4 = var3;
      int var5 = ((Integer)var3.getValue(POWER)).intValue();
      this.shouldSignal = false;
      int var6 = level.getBestNeighborSignal(blockPos);
      this.shouldSignal = true;
      int var7 = 0;
      if(var6 < 15) {
         for(Direction var9 : Direction.Plane.HORIZONTAL) {
            BlockPos var10 = blockPos.relative(var9);
            BlockState var11 = level.getBlockState(var10);
            var7 = this.checkTarget(var7, var11);
            BlockPos var12 = blockPos.above();
            if(var11.isRedstoneConductor(level, var10) && !level.getBlockState(var12).isRedstoneConductor(level, var12)) {
               var7 = this.checkTarget(var7, level.getBlockState(var10.above()));
            } else if(!var11.isRedstoneConductor(level, var10)) {
               var7 = this.checkTarget(var7, level.getBlockState(var10.below()));
            }
         }
      }

      int var8 = var7 - 1;
      if(var6 > var8) {
         var8 = var6;
      }

      if(var5 != var8) {
         var3 = (BlockState)var3.setValue(POWER, Integer.valueOf(var8));
         if(level.getBlockState(blockPos) == var4) {
            level.setBlock(blockPos, var3, 2);
         }

         this.toUpdate.add(blockPos);

         for(Direction var12 : Direction.values()) {
            this.toUpdate.add(blockPos.relative(var12));
         }
      }

      return var3;
   }

   private void checkCornerChangeAt(Level level, BlockPos blockPos) {
      if(level.getBlockState(blockPos).getBlock() == this) {
         level.updateNeighborsAt(blockPos, this);

         for(Direction var6 : Direction.values()) {
            level.updateNeighborsAt(blockPos.relative(var6), this);
         }

      }
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var4.getBlock() != var1.getBlock() && !level.isClientSide) {
         this.updatePowerStrength(level, blockPos, var1);

         for(Direction var7 : Direction.Plane.VERTICAL) {
            level.updateNeighborsAt(blockPos.relative(var7), this);
         }

         for(Direction var7 : Direction.Plane.HORIZONTAL) {
            this.checkCornerChangeAt(level, blockPos.relative(var7));
         }

         for(Direction var7 : Direction.Plane.HORIZONTAL) {
            BlockPos var8 = blockPos.relative(var7);
            if(level.getBlockState(var8).isRedstoneConductor(level, var8)) {
               this.checkCornerChangeAt(level, var8.above());
            } else {
               this.checkCornerChangeAt(level, var8.below());
            }
         }

      }
   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(!var5 && var1.getBlock() != var4.getBlock()) {
         super.onRemove(var1, level, blockPos, var4, var5);
         if(!level.isClientSide) {
            for(Direction var9 : Direction.values()) {
               level.updateNeighborsAt(blockPos.relative(var9), this);
            }

            this.updatePowerStrength(level, blockPos, var1);

            for(Direction var7 : Direction.Plane.HORIZONTAL) {
               this.checkCornerChangeAt(level, blockPos.relative(var7));
            }

            for(Direction var7 : Direction.Plane.HORIZONTAL) {
               BlockPos var8 = blockPos.relative(var7);
               if(level.getBlockState(var8).isRedstoneConductor(level, var8)) {
                  this.checkCornerChangeAt(level, var8.above());
               } else {
                  this.checkCornerChangeAt(level, var8.below());
               }
            }

         }
      }
   }

   private int checkTarget(int var1, BlockState blockState) {
      if(blockState.getBlock() != this) {
         return var1;
      } else {
         int var3 = ((Integer)blockState.getValue(POWER)).intValue();
         return var3 > var1?var3:var1;
      }
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      if(!level.isClientSide) {
         if(blockState.canSurvive(level, var3)) {
            this.updatePowerStrength(level, var3, blockState);
         } else {
            dropResources(blockState, level, var3);
            level.removeBlock(var3, false);
         }

      }
   }

   public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return !this.shouldSignal?0:blockState.getSignal(blockGetter, blockPos, direction);
   }

   public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      if(!this.shouldSignal) {
         return 0;
      } else {
         int var5 = ((Integer)blockState.getValue(POWER)).intValue();
         if(var5 == 0) {
            return 0;
         } else if(direction == Direction.UP) {
            return var5;
         } else {
            EnumSet<Direction> var6 = EnumSet.noneOf(Direction.class);

            for(Direction var8 : Direction.Plane.HORIZONTAL) {
               if(this.isPowerSourceAt(blockGetter, blockPos, var8)) {
                  var6.add(var8);
               }
            }

            if(direction.getAxis().isHorizontal() && var6.isEmpty()) {
               return var5;
            } else if(var6.contains(direction) && !var6.contains(direction.getCounterClockWise()) && !var6.contains(direction.getClockWise())) {
               return var5;
            } else {
               return 0;
            }
         }
      }
   }

   private boolean isPowerSourceAt(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      BlockPos blockPos = blockPos.relative(direction);
      BlockState var5 = blockGetter.getBlockState(blockPos);
      boolean var6 = var5.isRedstoneConductor(blockGetter, blockPos);
      BlockPos var7 = blockPos.above();
      boolean var8 = blockGetter.getBlockState(var7).isRedstoneConductor(blockGetter, var7);
      return !var8 && var6 && shouldConnectTo(blockGetter, blockPos.above())?true:(shouldConnectTo(var5, direction)?true:(var5.getBlock() == Blocks.REPEATER && ((Boolean)var5.getValue(DiodeBlock.POWERED)).booleanValue() && var5.getValue(DiodeBlock.FACING) == direction?true:!var6 && shouldConnectTo(blockGetter, blockPos.below())));
   }

   protected static boolean shouldConnectTo(BlockGetter blockGetter, BlockPos blockPos) {
      return shouldConnectTo(blockGetter.getBlockState(blockPos));
   }

   protected static boolean shouldConnectTo(BlockState blockState) {
      return shouldConnectTo((BlockState)blockState, (Direction)null);
   }

   protected static boolean shouldConnectTo(BlockState blockState, @Nullable Direction direction) {
      Block var2 = blockState.getBlock();
      if(var2 == Blocks.REDSTONE_WIRE) {
         return true;
      } else if(blockState.getBlock() == Blocks.REPEATER) {
         Direction var3 = (Direction)blockState.getValue(RepeaterBlock.FACING);
         return var3 == direction || var3.getOpposite() == direction;
      } else {
         return Blocks.OBSERVER == blockState.getBlock()?direction == blockState.getValue(ObserverBlock.FACING):blockState.isSignalSource() && direction != null;
      }
   }

   public boolean isSignalSource(BlockState blockState) {
      return this.shouldSignal;
   }

   public static int getColorForData(int i) {
      float var1 = (float)i / 15.0F;
      float var2 = var1 * 0.6F + 0.4F;
      if(i == 0) {
         var2 = 0.3F;
      }

      float var3 = var1 * var1 * 0.7F - 0.5F;
      float var4 = var1 * var1 * 0.6F - 0.7F;
      if(var3 < 0.0F) {
         var3 = 0.0F;
      }

      if(var4 < 0.0F) {
         var4 = 0.0F;
      }

      int var5 = Mth.clamp((int)(var2 * 255.0F), 0, 255);
      int var6 = Mth.clamp((int)(var3 * 255.0F), 0, 255);
      int var7 = Mth.clamp((int)(var4 * 255.0F), 0, 255);
      return -16777216 | var5 << 16 | var6 << 8 | var7;
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      int var5 = ((Integer)blockState.getValue(POWER)).intValue();
      if(var5 != 0) {
         double var6 = (double)blockPos.getX() + 0.5D + ((double)random.nextFloat() - 0.5D) * 0.2D;
         double var8 = (double)((float)blockPos.getY() + 0.0625F);
         double var10 = (double)blockPos.getZ() + 0.5D + ((double)random.nextFloat() - 0.5D) * 0.2D;
         float var12 = (float)var5 / 15.0F;
         float var13 = var12 * 0.6F + 0.4F;
         float var14 = Math.max(0.0F, var12 * var12 * 0.7F - 0.5F);
         float var15 = Math.max(0.0F, var12 * var12 * 0.6F - 0.7F);
         level.addParticle(new DustParticleOptions(var13, var14, var15, 1.0F), var6, var8, var10, 0.0D, 0.0D, 0.0D);
      }
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
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

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{NORTH, EAST, SOUTH, WEST, POWER});
   }
}
