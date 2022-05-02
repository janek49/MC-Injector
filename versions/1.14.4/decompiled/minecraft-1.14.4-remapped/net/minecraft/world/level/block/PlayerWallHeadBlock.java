package net.minecraft.world.level.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerWallHeadBlock extends WallSkullBlock {
   protected PlayerWallHeadBlock(Block.Properties block$Properties) {
      super(SkullBlock.Types.PLAYER, block$Properties);
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
      Blocks.PLAYER_HEAD.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
   }

   public List getDrops(BlockState blockState, LootContext.Builder lootContext$Builder) {
      return Blocks.PLAYER_HEAD.getDrops(blockState, lootContext$Builder);
   }
}
