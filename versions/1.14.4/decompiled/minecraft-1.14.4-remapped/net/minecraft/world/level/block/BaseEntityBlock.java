package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseEntityBlock extends Block implements EntityBlock {
   protected BaseEntityBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.INVISIBLE;
   }

   public boolean triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int var4, int var5) {
      super.triggerEvent(blockState, level, blockPos, var4, var5);
      BlockEntity var6 = level.getBlockEntity(blockPos);
      return var6 == null?false:var6.triggerEvent(var4, var5);
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos blockPos) {
      BlockEntity var4 = level.getBlockEntity(blockPos);
      return var4 instanceof MenuProvider?(MenuProvider)var4:null;
   }
}
