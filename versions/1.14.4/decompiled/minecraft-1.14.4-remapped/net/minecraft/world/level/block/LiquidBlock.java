package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LiquidBlock extends Block implements BucketPickup {
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
   protected final FlowingFluid fluid;
   private final List stateCache;

   protected LiquidBlock(FlowingFluid fluid, Block.Properties block$Properties) {
      super(block$Properties);
      this.fluid = fluid;
      this.stateCache = Lists.newArrayList();
      this.stateCache.add(fluid.getSource(false));

      for(int var3 = 1; var3 < 8; ++var3) {
         this.stateCache.add(fluid.getFlowing(8 - var3, false));
      }

      this.stateCache.add(fluid.getFlowing(8, true));
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, Integer.valueOf(0)));
   }

   public void randomTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      level.getFluidState(blockPos).randomTick(level, blockPos, random);
   }

   public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return false;
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return !this.fluid.is(FluidTags.LAVA);
   }

   public FluidState getFluidState(BlockState blockState) {
      int var2 = ((Integer)blockState.getValue(LEVEL)).intValue();
      return (FluidState)this.stateCache.get(Math.min(var2, 8));
   }

   public boolean skipRendering(BlockState var1, BlockState var2, Direction direction) {
      return var2.getFluidState().getType().isSame(this.fluid)?true:super.canOcclude(var1);
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.INVISIBLE;
   }

   public List getDrops(BlockState blockState, LootContext.Builder lootContext$Builder) {
      return Collections.emptyList();
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return Shapes.empty();
   }

   public int getTickDelay(LevelReader levelReader) {
      return this.fluid.getTickDelay(levelReader);
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(this.shouldSpreadLiquid(level, blockPos, var1)) {
         level.getLiquidTicks().scheduleTick(blockPos, var1.getFluidState().getType(), this.getTickDelay(level));
      }

   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(var1.getFluidState().isSource() || var3.getFluidState().isSource()) {
         levelAccessor.getLiquidTicks().scheduleTick(var5, var1.getFluidState().getType(), this.getTickDelay(levelAccessor));
      }

      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      if(this.shouldSpreadLiquid(level, var3, blockState)) {
         level.getLiquidTicks().scheduleTick(var3, blockState.getFluidState().getType(), this.getTickDelay(level));
      }

   }

   public boolean shouldSpreadLiquid(Level level, BlockPos blockPos, BlockState blockState) {
      if(this.fluid.is(FluidTags.LAVA)) {
         boolean var4 = false;

         for(Direction var8 : Direction.values()) {
            if(var8 != Direction.DOWN && level.getFluidState(blockPos.relative(var8)).is(FluidTags.WATER)) {
               var4 = true;
               break;
            }
         }

         if(var4) {
            FluidState var5 = level.getFluidState(blockPos);
            if(var5.isSource()) {
               level.setBlockAndUpdate(blockPos, Blocks.OBSIDIAN.defaultBlockState());
               this.fizz(level, blockPos);
               return false;
            }

            if(var5.getHeight(level, blockPos) >= 0.44444445F) {
               level.setBlockAndUpdate(blockPos, Blocks.COBBLESTONE.defaultBlockState());
               this.fizz(level, blockPos);
               return false;
            }
         }
      }

      return true;
   }

   private void fizz(LevelAccessor levelAccessor, BlockPos blockPos) {
      levelAccessor.levelEvent(1501, blockPos, 0);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{LEVEL});
   }

   public Fluid takeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
      if(((Integer)blockState.getValue(LEVEL)).intValue() == 0) {
         levelAccessor.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 11);
         return this.fluid;
      } else {
         return Fluids.EMPTY;
      }
   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      if(this.fluid.is(FluidTags.LAVA)) {
         entity.setInLava();
      }

   }
}
