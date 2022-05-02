package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class JukeboxBlock extends BaseEntityBlock {
   public static final BooleanProperty HAS_RECORD = BlockStateProperties.HAS_RECORD;

   protected JukeboxBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(HAS_RECORD, Boolean.valueOf(false)));
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(((Boolean)blockState.getValue(HAS_RECORD)).booleanValue()) {
         this.dropRecording(level, blockPos);
         blockState = (BlockState)blockState.setValue(HAS_RECORD, Boolean.valueOf(false));
         level.setBlock(blockPos, blockState, 2);
         return true;
      } else {
         return false;
      }
   }

   public void setRecord(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, ItemStack itemStack) {
      BlockEntity var5 = levelAccessor.getBlockEntity(blockPos);
      if(var5 instanceof JukeboxBlockEntity) {
         ((JukeboxBlockEntity)var5).setRecord(itemStack.copy());
         levelAccessor.setBlock(blockPos, (BlockState)blockState.setValue(HAS_RECORD, Boolean.valueOf(true)), 2);
      }
   }

   private void dropRecording(Level level, BlockPos blockPos) {
      if(!level.isClientSide) {
         BlockEntity var3 = level.getBlockEntity(blockPos);
         if(var3 instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity var4 = (JukeboxBlockEntity)var3;
            ItemStack var5 = var4.getRecord();
            if(!var5.isEmpty()) {
               level.levelEvent(1010, blockPos, 0);
               var4.clearContent();
               float var6 = 0.7F;
               double var7 = (double)(level.random.nextFloat() * 0.7F) + 0.15000000596046448D;
               double var9 = (double)(level.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
               double var11 = (double)(level.random.nextFloat() * 0.7F) + 0.15000000596046448D;
               ItemStack var13 = var5.copy();
               ItemEntity var14 = new ItemEntity(level, (double)blockPos.getX() + var7, (double)blockPos.getY() + var9, (double)blockPos.getZ() + var11, var13);
               var14.setDefaultPickUpDelay();
               level.addFreshEntity(var14);
            }
         }
      }
   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var1.getBlock() != var4.getBlock()) {
         this.dropRecording(level, blockPos);
         super.onRemove(var1, level, blockPos, var4, var5);
      }
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new JukeboxBlockEntity();
   }

   public boolean hasAnalogOutputSignal(BlockState blockState) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
      BlockEntity var4 = level.getBlockEntity(blockPos);
      if(var4 instanceof JukeboxBlockEntity) {
         Item var5 = ((JukeboxBlockEntity)var4).getRecord().getItem();
         if(var5 instanceof RecordItem) {
            return ((RecordItem)var5).getAnalogOutput();
         }
      }

      return 0;
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.MODEL;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{HAS_RECORD});
   }
}
