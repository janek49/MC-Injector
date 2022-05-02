package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class RedstoneTorchBlock extends TorchBlock {
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   private static final Map RECENT_TOGGLES = new WeakHashMap();

   protected RedstoneTorchBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, Boolean.valueOf(true)));
   }

   public int getTickDelay(LevelReader levelReader) {
      return 2;
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      for(Direction var9 : Direction.values()) {
         level.updateNeighborsAt(blockPos.relative(var9), this);
      }

   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(!var5) {
         for(Direction var9 : Direction.values()) {
            level.updateNeighborsAt(blockPos.relative(var9), this);
         }

      }
   }

   public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return ((Boolean)blockState.getValue(LIT)).booleanValue() && Direction.UP != direction?15:0;
   }

   protected boolean hasNeighborSignal(Level level, BlockPos blockPos, BlockState blockState) {
      return level.hasSignal(blockPos.below(), Direction.DOWN);
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      handleTick(blockState, level, blockPos, random, this.hasNeighborSignal(level, blockPos, blockState));
   }

   public static void handleTick(BlockState blockState, Level level, BlockPos blockPos, Random random, boolean var4) {
      List<RedstoneTorchBlock.Toggle> var5 = (List)RECENT_TOGGLES.get(level);

      while(var5 != null && !var5.isEmpty() && level.getGameTime() - ((RedstoneTorchBlock.Toggle)var5.get(0)).when > 60L) {
         var5.remove(0);
      }

      if(((Boolean)blockState.getValue(LIT)).booleanValue()) {
         if(var4) {
            level.setBlock(blockPos, (BlockState)blockState.setValue(LIT, Boolean.valueOf(false)), 3);
            if(isToggledTooFrequently(level, blockPos, true)) {
               level.levelEvent(1502, blockPos, 0);
               level.getBlockTicks().scheduleTick(blockPos, level.getBlockState(blockPos).getBlock(), 160);
            }
         }
      } else if(!var4 && !isToggledTooFrequently(level, blockPos, false)) {
         level.setBlock(blockPos, (BlockState)blockState.setValue(LIT, Boolean.valueOf(true)), 3);
      }

   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      if(((Boolean)blockState.getValue(LIT)).booleanValue() == this.hasNeighborSignal(level, var3, blockState) && !level.getBlockTicks().willTickThisTick(var3, this)) {
         level.getBlockTicks().scheduleTick(var3, this, this.getTickDelay(level));
      }

   }

   public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return direction == Direction.DOWN?blockState.getSignal(blockGetter, blockPos, direction):0;
   }

   public boolean isSignalSource(BlockState blockState) {
      return true;
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(((Boolean)blockState.getValue(LIT)).booleanValue()) {
         double var5 = (double)blockPos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
         double var7 = (double)blockPos.getY() + 0.7D + (random.nextDouble() - 0.5D) * 0.2D;
         double var9 = (double)blockPos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
         level.addParticle(DustParticleOptions.REDSTONE, var5, var7, var9, 0.0D, 0.0D, 0.0D);
      }
   }

   public int getLightEmission(BlockState blockState) {
      return ((Boolean)blockState.getValue(LIT)).booleanValue()?super.getLightEmission(blockState):0;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{LIT});
   }

   private static boolean isToggledTooFrequently(Level level, BlockPos blockPos, boolean var2) {
      List<RedstoneTorchBlock.Toggle> var3 = (List)RECENT_TOGGLES.computeIfAbsent(level, (blockGetter) -> {
         return Lists.newArrayList();
      });
      if(var2) {
         var3.add(new RedstoneTorchBlock.Toggle(blockPos.immutable(), level.getGameTime()));
      }

      int var4 = 0;

      for(int var5 = 0; var5 < var3.size(); ++var5) {
         RedstoneTorchBlock.Toggle var6 = (RedstoneTorchBlock.Toggle)var3.get(var5);
         if(var6.pos.equals(blockPos)) {
            ++var4;
            if(var4 >= 8) {
               return true;
            }
         }
      }

      return false;
   }

   public static class Toggle {
      private final BlockPos pos;
      private final long when;

      public Toggle(BlockPos pos, long when) {
         this.pos = pos;
         this.when = when;
      }
   }
}
