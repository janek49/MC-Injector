package net.minecraft.world.level.material;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class FlowingFluid extends Fluid {
   public static final BooleanProperty FALLING = BlockStateProperties.FALLING;
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_FLOWING;
   private static final ThreadLocal OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
      Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap(200) {
         protected void rehash(int i) {
         }
      };
      object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
      return object2ByteLinkedOpenHashMap;
   });
   private final Map shapes = Maps.newIdentityHashMap();

   protected void createFluidStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FALLING});
   }

   public Vec3 getFlow(BlockGetter blockGetter, BlockPos blockPos, FluidState fluidState) {
      double var4 = 0.0D;
      double var6 = 0.0D;
      BlockPos.PooledMutableBlockPos var8 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var9 = null;

      Vec3 var28;
      try {
         for(Direction var11 : Direction.Plane.HORIZONTAL) {
            var8.set((Vec3i)blockPos).move(var11);
            FluidState var12 = blockGetter.getFluidState(var8);
            if(this.affectsFlow(var12)) {
               float var13 = var12.getOwnHeight();
               float var14 = 0.0F;
               if(var13 == 0.0F) {
                  if(!blockGetter.getBlockState(var8).getMaterial().blocksMotion()) {
                     BlockPos var15 = var8.below();
                     FluidState var16 = blockGetter.getFluidState(var15);
                     if(this.affectsFlow(var16)) {
                        var13 = var16.getOwnHeight();
                        if(var13 > 0.0F) {
                           var14 = fluidState.getOwnHeight() - (var13 - 0.8888889F);
                        }
                     }
                  }
               } else if(var13 > 0.0F) {
                  var14 = fluidState.getOwnHeight() - var13;
               }

               if(var14 != 0.0F) {
                  var4 += (double)((float)var11.getStepX() * var14);
                  var6 += (double)((float)var11.getStepZ() * var14);
               }
            }
         }

         Vec3 var10 = new Vec3(var4, 0.0D, var6);
         if(((Boolean)fluidState.getValue(FALLING)).booleanValue()) {
            for(Direction var12 : Direction.Plane.HORIZONTAL) {
               var8.set((Vec3i)blockPos).move(var12);
               if(this.isSolidFace(blockGetter, var8, var12) || this.isSolidFace(blockGetter, var8.above(), var12)) {
                  var10 = var10.normalize().add(0.0D, -6.0D, 0.0D);
                  break;
               }
            }
         }

         var28 = var10.normalize();
      } catch (Throwable var24) {
         var9 = var24;
         throw var24;
      } finally {
         if(var8 != null) {
            if(var9 != null) {
               try {
                  var8.close();
               } catch (Throwable var23) {
                  var9.addSuppressed(var23);
               }
            } else {
               var8.close();
            }
         }

      }

      return var28;
   }

   private boolean affectsFlow(FluidState fluidState) {
      return fluidState.isEmpty() || fluidState.getType().isSame(this);
   }

   protected boolean isSolidFace(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      BlockState var4 = blockGetter.getBlockState(blockPos);
      FluidState var5 = blockGetter.getFluidState(blockPos);
      return var5.getType().isSame(this)?false:(direction == Direction.UP?true:(var4.getMaterial() == Material.ICE?false:var4.isFaceSturdy(blockGetter, blockPos, direction)));
   }

   protected void spread(LevelAccessor levelAccessor, BlockPos blockPos, FluidState fluidState) {
      if(!fluidState.isEmpty()) {
         BlockState var4 = levelAccessor.getBlockState(blockPos);
         BlockPos var5 = blockPos.below();
         BlockState var6 = levelAccessor.getBlockState(var5);
         FluidState var7 = this.getNewLiquid(levelAccessor, var5, var6);
         if(this.canSpreadTo(levelAccessor, blockPos, var4, Direction.DOWN, var5, var6, levelAccessor.getFluidState(var5), var7.getType())) {
            this.spreadTo(levelAccessor, var5, var6, Direction.DOWN, var7);
            if(this.sourceNeighborCount(levelAccessor, blockPos) >= 3) {
               this.spreadToSides(levelAccessor, blockPos, fluidState, var4);
            }
         } else if(fluidState.isSource() || !this.isWaterHole(levelAccessor, var7.getType(), blockPos, var4, var5, var6)) {
            this.spreadToSides(levelAccessor, blockPos, fluidState, var4);
         }

      }
   }

   private void spreadToSides(LevelAccessor levelAccessor, BlockPos blockPos, FluidState fluidState, BlockState blockState) {
      int var5 = fluidState.getAmount() - this.getDropOff(levelAccessor);
      if(((Boolean)fluidState.getValue(FALLING)).booleanValue()) {
         var5 = 7;
      }

      if(var5 > 0) {
         Map<Direction, FluidState> var6 = this.getSpread(levelAccessor, blockPos, blockState);

         for(Entry<Direction, FluidState> var8 : var6.entrySet()) {
            Direction var9 = (Direction)var8.getKey();
            FluidState var10 = (FluidState)var8.getValue();
            BlockPos var11 = blockPos.relative(var9);
            BlockState var12 = levelAccessor.getBlockState(var11);
            if(this.canSpreadTo(levelAccessor, blockPos, blockState, var9, var11, var12, levelAccessor.getFluidState(var11), var10.getType())) {
               this.spreadTo(levelAccessor, var11, var12, var9, var10);
            }
         }

      }
   }

   protected FluidState getNewLiquid(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
      int var4 = 0;
      int var5 = 0;

      for(Direction var7 : Direction.Plane.HORIZONTAL) {
         BlockPos var8 = blockPos.relative(var7);
         BlockState var9 = levelReader.getBlockState(var8);
         FluidState var10 = var9.getFluidState();
         if(var10.getType().isSame(this) && this.canPassThroughWall(var7, levelReader, blockPos, blockState, var8, var9)) {
            if(var10.isSource()) {
               ++var5;
            }

            var4 = Math.max(var4, var10.getAmount());
         }
      }

      if(this.canConvertToSource() && var5 >= 2) {
         BlockState var6 = levelReader.getBlockState(blockPos.below());
         FluidState var7 = var6.getFluidState();
         if(var6.getMaterial().isSolid() || this.isSourceBlockOfThisType(var7)) {
            return this.getSource(false);
         }
      }

      BlockPos var6 = blockPos.above();
      BlockState var7 = levelReader.getBlockState(var6);
      FluidState var8 = var7.getFluidState();
      if(!var8.isEmpty() && var8.getType().isSame(this) && this.canPassThroughWall(Direction.UP, levelReader, blockPos, blockState, var6, var7)) {
         return this.getFlowing(8, true);
      } else {
         int var9 = var4 - this.getDropOff(levelReader);
         if(var9 <= 0) {
            return Fluids.EMPTY.defaultFluidState();
         } else {
            return this.getFlowing(var9, false);
         }
      }
   }

   private boolean canPassThroughWall(Direction direction, BlockGetter blockGetter, BlockPos var3, BlockState var4, BlockPos var5, BlockState var6) {
      Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> var7;
      if(!var4.getBlock().hasDynamicShape() && !var6.getBlock().hasDynamicShape()) {
         var7 = (Object2ByteLinkedOpenHashMap)OCCLUSION_CACHE.get();
      } else {
         var7 = null;
      }

      Block.BlockStatePairKey var8;
      if(var7 != null) {
         var8 = new Block.BlockStatePairKey(var4, var6, direction);
         byte var9 = var7.getAndMoveToFirst(var8);
         if(var9 != 127) {
            return var9 != 0;
         }
      } else {
         var8 = null;
      }

      VoxelShape var9 = var4.getCollisionShape(blockGetter, var3);
      VoxelShape var10 = var6.getCollisionShape(blockGetter, var5);
      boolean var11 = !Shapes.mergedFaceOccludes(var9, var10, direction);
      if(var7 != null) {
         if(var7.size() == 200) {
            var7.removeLastByte();
         }

         var7.putAndMoveToFirst(var8, (byte)(var11?1:0));
      }

      return var11;
   }

   public abstract Fluid getFlowing();

   public FluidState getFlowing(int var1, boolean var2) {
      return (FluidState)((FluidState)this.getFlowing().defaultFluidState().setValue(LEVEL, Integer.valueOf(var1))).setValue(FALLING, Boolean.valueOf(var2));
   }

   public abstract Fluid getSource();

   public FluidState getSource(boolean b) {
      return (FluidState)this.getSource().defaultFluidState().setValue(FALLING, Boolean.valueOf(b));
   }

   protected abstract boolean canConvertToSource();

   protected void spreadTo(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
      if(blockState.getBlock() instanceof LiquidBlockContainer) {
         ((LiquidBlockContainer)blockState.getBlock()).placeLiquid(levelAccessor, blockPos, blockState, fluidState);
      } else {
         if(!blockState.isAir()) {
            this.beforeDestroyingBlock(levelAccessor, blockPos, blockState);
         }

         levelAccessor.setBlock(blockPos, fluidState.createLegacyBlock(), 3);
      }

   }

   protected abstract void beforeDestroyingBlock(LevelAccessor var1, BlockPos var2, BlockState var3);

   private static short getCacheKey(BlockPos var0, BlockPos var1) {
      int var2 = var1.getX() - var0.getX();
      int var3 = var1.getZ() - var0.getZ();
      return (short)((var2 + 128 & 255) << 8 | var3 + 128 & 255);
   }

   protected int getSlopeDistance(LevelReader levelReader, BlockPos var2, int var3, Direction direction, BlockState blockState, BlockPos var6, Short2ObjectMap short2ObjectMap, Short2BooleanMap short2BooleanMap) {
      int var9 = 1000;

      for(Direction var11 : Direction.Plane.HORIZONTAL) {
         if(var11 != direction) {
            BlockPos var12 = var2.relative(var11);
            short var13 = getCacheKey(var6, var12);
            Pair<BlockState, FluidState> var14 = (Pair)short2ObjectMap.computeIfAbsent(var13, (var2) -> {
               BlockState var3 = levelReader.getBlockState(var12);
               return Pair.of(var3, var3.getFluidState());
            });
            BlockState var15 = (BlockState)var14.getFirst();
            FluidState var16 = (FluidState)var14.getSecond();
            if(this.canPassThrough(levelReader, this.getFlowing(), var2, blockState, var11, var12, var15, var16)) {
               boolean var17 = short2BooleanMap.computeIfAbsent(var13, (var4) -> {
                  BlockPos blockPos = var12.below();
                  BlockState var6 = levelReader.getBlockState(blockPos);
                  return this.isWaterHole(levelReader, this.getFlowing(), var12, var15, blockPos, var6);
               });
               if(var17) {
                  return var3;
               }

               if(var3 < this.getSlopeFindDistance(levelReader)) {
                  int var18 = this.getSlopeDistance(levelReader, var12, var3 + 1, var11.getOpposite(), var15, var6, short2ObjectMap, short2BooleanMap);
                  if(var18 < var9) {
                     var9 = var18;
                  }
               }
            }
         }
      }

      return var9;
   }

   private boolean isWaterHole(BlockGetter blockGetter, Fluid fluid, BlockPos var3, BlockState var4, BlockPos var5, BlockState var6) {
      return !this.canPassThroughWall(Direction.DOWN, blockGetter, var3, var4, var5, var6)?false:(var6.getFluidState().getType().isSame(this)?true:this.canHoldFluid(blockGetter, var5, var6, fluid));
   }

   private boolean canPassThrough(BlockGetter blockGetter, Fluid fluid, BlockPos var3, BlockState var4, Direction direction, BlockPos var6, BlockState var7, FluidState fluidState) {
      return !this.isSourceBlockOfThisType(fluidState) && this.canPassThroughWall(direction, blockGetter, var3, var4, var6, var7) && this.canHoldFluid(blockGetter, var6, var7, fluid);
   }

   private boolean isSourceBlockOfThisType(FluidState fluidState) {
      return fluidState.getType().isSame(this) && fluidState.isSource();
   }

   protected abstract int getSlopeFindDistance(LevelReader var1);

   private int sourceNeighborCount(LevelReader levelReader, BlockPos blockPos) {
      int var3 = 0;

      for(Direction var5 : Direction.Plane.HORIZONTAL) {
         BlockPos var6 = blockPos.relative(var5);
         FluidState var7 = levelReader.getFluidState(var6);
         if(this.isSourceBlockOfThisType(var7)) {
            ++var3;
         }
      }

      return var3;
   }

   protected Map getSpread(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
      int var4 = 1000;
      Map<Direction, FluidState> var5 = Maps.newEnumMap(Direction.class);
      Short2ObjectMap<Pair<BlockState, FluidState>> var6 = new Short2ObjectOpenHashMap();
      Short2BooleanMap var7 = new Short2BooleanOpenHashMap();

      for(Direction var9 : Direction.Plane.HORIZONTAL) {
         BlockPos var10 = blockPos.relative(var9);
         short var11 = getCacheKey(blockPos, var10);
         Pair<BlockState, FluidState> var12 = (Pair)var6.computeIfAbsent(var11, (var2) -> {
            BlockState var3 = levelReader.getBlockState(var10);
            return Pair.of(var3, var3.getFluidState());
         });
         BlockState var13 = (BlockState)var12.getFirst();
         FluidState var14 = (FluidState)var12.getSecond();
         FluidState var15 = this.getNewLiquid(levelReader, var10, var13);
         if(this.canPassThrough(levelReader, var15.getType(), blockPos, blockState, var9, var10, var13, var14)) {
            BlockPos var17 = var10.below();
            boolean var18 = var7.computeIfAbsent(var11, (var5) -> {
               BlockState blockState = levelReader.getBlockState(var17);
               return this.isWaterHole(levelReader, this.getFlowing(), var10, var13, var17, blockState);
            });
            int var16;
            if(var18) {
               var16 = 0;
            } else {
               var16 = this.getSlopeDistance(levelReader, var10, 1, var9.getOpposite(), var13, blockPos, var6, var7);
            }

            if(var16 < var4) {
               var5.clear();
            }

            if(var16 <= var4) {
               var5.put(var9, var15);
               var4 = var16;
            }
         }
      }

      return var5;
   }

   private boolean canHoldFluid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
      Block var5 = blockState.getBlock();
      if(var5 instanceof LiquidBlockContainer) {
         return ((LiquidBlockContainer)var5).canPlaceLiquid(blockGetter, blockPos, blockState, fluid);
      } else if(!(var5 instanceof DoorBlock) && !var5.is(BlockTags.SIGNS) && var5 != Blocks.LADDER && var5 != Blocks.SUGAR_CANE && var5 != Blocks.BUBBLE_COLUMN) {
         Material var6 = blockState.getMaterial();
         return var6 != Material.PORTAL && var6 != Material.STRUCTURAL_AIR && var6 != Material.WATER_PLANT && var6 != Material.REPLACEABLE_WATER_PLANT?!var6.blocksMotion():false;
      } else {
         return false;
      }
   }

   protected boolean canSpreadTo(BlockGetter blockGetter, BlockPos var2, BlockState var3, Direction direction, BlockPos var5, BlockState var6, FluidState fluidState, Fluid fluid) {
      return fluidState.canBeReplacedWith(blockGetter, var5, fluid, direction) && this.canPassThroughWall(direction, blockGetter, var2, var3, var5, var6) && this.canHoldFluid(blockGetter, var5, var6, fluid);
   }

   protected abstract int getDropOff(LevelReader var1);

   protected int getSpreadDelay(Level level, BlockPos blockPos, FluidState var3, FluidState var4) {
      return this.getTickDelay(level);
   }

   public void tick(Level level, BlockPos blockPos, FluidState fluidState) {
      if(!fluidState.isSource()) {
         FluidState fluidState = this.getNewLiquid(level, blockPos, level.getBlockState(blockPos));
         int var5 = this.getSpreadDelay(level, blockPos, fluidState, fluidState);
         if(fluidState.isEmpty()) {
            fluidState = fluidState;
            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
         } else if(!fluidState.equals(fluidState)) {
            fluidState = fluidState;
            BlockState var6 = fluidState.createLegacyBlock();
            level.setBlock(blockPos, var6, 2);
            level.getLiquidTicks().scheduleTick(blockPos, fluidState.getType(), var5);
            level.updateNeighborsAt(blockPos, var6.getBlock());
         }
      }

      this.spread(level, blockPos, fluidState);
   }

   protected static int getLegacyLevel(FluidState fluidState) {
      return fluidState.isSource()?0:8 - Math.min(fluidState.getAmount(), 8) + (((Boolean)fluidState.getValue(FALLING)).booleanValue()?8:0);
   }

   private static boolean hasSameAbove(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos) {
      return fluidState.getType().isSame(blockGetter.getFluidState(blockPos.above()).getType());
   }

   public float getHeight(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos) {
      return hasSameAbove(fluidState, blockGetter, blockPos)?1.0F:fluidState.getOwnHeight();
   }

   public float getOwnHeight(FluidState fluidState) {
      return (float)fluidState.getAmount() / 9.0F;
   }

   public VoxelShape getShape(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos) {
      return fluidState.getAmount() == 9 && hasSameAbove(fluidState, blockGetter, blockPos)?Shapes.block():(VoxelShape)this.shapes.computeIfAbsent(fluidState, (fluidState) -> {
         return Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, (double)fluidState.getHeight(blockGetter, blockPos), 1.0D);
      });
   }
}
