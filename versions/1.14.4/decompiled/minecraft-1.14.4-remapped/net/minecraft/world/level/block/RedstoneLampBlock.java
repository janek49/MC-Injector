package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class RedstoneLampBlock extends Block {
   public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

   public RedstoneLampBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)));
   }

   public int getLightEmission(BlockState blockState) {
      return ((Boolean)blockState.getValue(LIT)).booleanValue()?super.getLightEmission(blockState):0;
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      super.onPlace(var1, level, blockPos, var4, var5);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)this.defaultBlockState().setValue(LIT, Boolean.valueOf(blockPlaceContext.getLevel().hasNeighborSignal(blockPlaceContext.getClickedPos())));
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      if(!level.isClientSide) {
         boolean var7 = ((Boolean)blockState.getValue(LIT)).booleanValue();
         if(var7 != level.hasNeighborSignal(var3)) {
            if(var7) {
               level.getBlockTicks().scheduleTick(var3, this, 4);
            } else {
               level.setBlock(var3, (BlockState)blockState.cycle(LIT), 2);
            }
         }

      }
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!level.isClientSide) {
         if(((Boolean)blockState.getValue(LIT)).booleanValue() && !level.hasNeighborSignal(blockPos)) {
            level.setBlock(blockPos, (BlockState)blockState.cycle(LIT), 2);
         }

      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{LIT});
   }
}
