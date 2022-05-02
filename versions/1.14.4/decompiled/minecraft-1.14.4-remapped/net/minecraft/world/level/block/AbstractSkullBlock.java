package net.minecraft.world.level.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractSkullBlock extends BaseEntityBlock {
   private final SkullBlock.Type type;

   public AbstractSkullBlock(SkullBlock.Type type, Block.Properties block$Properties) {
      super(block$Properties);
      this.type = type;
   }

   public boolean hasCustomBreakingProgress(BlockState blockState) {
      return true;
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new SkullBlockEntity();
   }

   public SkullBlock.Type getType() {
      return this.type;
   }
}
