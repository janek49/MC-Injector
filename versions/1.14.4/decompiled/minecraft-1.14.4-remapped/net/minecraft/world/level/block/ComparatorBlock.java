package net.minecraft.world.level.block;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

public class ComparatorBlock extends DiodeBlock implements EntityBlock {
   public static final EnumProperty MODE = BlockStateProperties.MODE_COMPARATOR;

   public ComparatorBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, Boolean.valueOf(false))).setValue(MODE, ComparatorMode.COMPARE));
   }

   protected int getDelay(BlockState blockState) {
      return 2;
   }

   protected int getOutputSignal(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      BlockEntity var4 = blockGetter.getBlockEntity(blockPos);
      return var4 instanceof ComparatorBlockEntity?((ComparatorBlockEntity)var4).getOutputSignal():0;
   }

   private int calculateOutputSignal(Level level, BlockPos blockPos, BlockState blockState) {
      return blockState.getValue(MODE) == ComparatorMode.SUBTRACT?Math.max(this.getInputSignal(level, blockPos, blockState) - this.getAlternateSignal(level, blockPos, blockState), 0):this.getInputSignal(level, blockPos, blockState);
   }

   protected boolean shouldTurnOn(Level level, BlockPos blockPos, BlockState blockState) {
      int var4 = this.getInputSignal(level, blockPos, blockState);
      return var4 >= 15?true:(var4 == 0?false:var4 >= this.getAlternateSignal(level, blockPos, blockState));
   }

   protected int getInputSignal(Level level, BlockPos blockPos, BlockState blockState) {
      int var4 = super.getInputSignal(level, blockPos, blockState);
      Direction var5 = (Direction)blockState.getValue(FACING);
      BlockPos var6 = blockPos.relative(var5);
      BlockState var7 = level.getBlockState(var6);
      if(var7.hasAnalogOutputSignal()) {
         var4 = var7.getAnalogOutputSignal(level, var6);
      } else if(var4 < 15 && var7.isRedstoneConductor(level, var6)) {
         var6 = var6.relative(var5);
         var7 = level.getBlockState(var6);
         if(var7.hasAnalogOutputSignal()) {
            var4 = var7.getAnalogOutputSignal(level, var6);
         } else if(var7.isAir()) {
            ItemFrame var8 = this.getItemFrame(level, var5, var6);
            if(var8 != null) {
               var4 = var8.getAnalogOutput();
            }
         }
      }

      return var4;
   }

   @Nullable
   private ItemFrame getItemFrame(Level level, Direction direction, BlockPos blockPos) {
      List<ItemFrame> var4 = level.getEntitiesOfClass(ItemFrame.class, new AABB((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), (double)(blockPos.getX() + 1), (double)(blockPos.getY() + 1), (double)(blockPos.getZ() + 1)), (itemFrame) -> {
         return itemFrame != null && itemFrame.getDirection() == direction;
      });
      return var4.size() == 1?(ItemFrame)var4.get(0):null;
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(!player.abilities.mayBuild) {
         return false;
      } else {
         blockState = (BlockState)blockState.cycle(MODE);
         float var7 = blockState.getValue(MODE) == ComparatorMode.SUBTRACT?0.55F:0.5F;
         level.playSound(player, blockPos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3F, var7);
         level.setBlock(blockPos, blockState, 2);
         this.refreshOutputState(level, blockPos, blockState);
         return true;
      }
   }

   protected void checkTickOnNeighbor(Level level, BlockPos blockPos, BlockState blockState) {
      if(!level.getBlockTicks().willTickThisTick(blockPos, this)) {
         int var4 = this.calculateOutputSignal(level, blockPos, blockState);
         BlockEntity var5 = level.getBlockEntity(blockPos);
         int var6 = var5 instanceof ComparatorBlockEntity?((ComparatorBlockEntity)var5).getOutputSignal():0;
         if(var4 != var6 || ((Boolean)blockState.getValue(POWERED)).booleanValue() != this.shouldTurnOn(level, blockPos, blockState)) {
            TickPriority var7 = this.shouldPrioritize(level, blockPos, blockState)?TickPriority.HIGH:TickPriority.NORMAL;
            level.getBlockTicks().scheduleTick(blockPos, this, 2, var7);
         }

      }
   }

   private void refreshOutputState(Level level, BlockPos blockPos, BlockState blockState) {
      int var4 = this.calculateOutputSignal(level, blockPos, blockState);
      BlockEntity var5 = level.getBlockEntity(blockPos);
      int var6 = 0;
      if(var5 instanceof ComparatorBlockEntity) {
         ComparatorBlockEntity var7 = (ComparatorBlockEntity)var5;
         var6 = var7.getOutputSignal();
         var7.setOutputSignal(var4);
      }

      if(var6 != var4 || blockState.getValue(MODE) == ComparatorMode.COMPARE) {
         boolean var7 = this.shouldTurnOn(level, blockPos, blockState);
         boolean var8 = ((Boolean)blockState.getValue(POWERED)).booleanValue();
         if(var8 && !var7) {
            level.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, Boolean.valueOf(false)), 2);
         } else if(!var8 && var7) {
            level.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, Boolean.valueOf(true)), 2);
         }

         this.updateNeighborsInFront(level, blockPos, blockState);
      }

   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      this.refreshOutputState(level, blockPos, blockState);
   }

   public boolean triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int var4, int var5) {
      super.triggerEvent(blockState, level, blockPos, var4, var5);
      BlockEntity var6 = level.getBlockEntity(blockPos);
      return var6 != null && var6.triggerEvent(var4, var5);
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new ComparatorBlockEntity();
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, MODE, POWERED});
   }
}
