package net.minecraft.world.level.block.state.predicate;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPredicate implements Predicate {
   private final Block block;

   public BlockPredicate(Block block) {
      this.block = block;
   }

   public static BlockPredicate forBlock(Block block) {
      return new BlockPredicate(block);
   }

   public boolean test(@Nullable BlockState blockState) {
      return blockState != null && blockState.getBlock() == this.block;
   }

   // $FF: synthetic method
   public boolean test(@Nullable Object var1) {
      return this.test((BlockState)var1);
   }
}
