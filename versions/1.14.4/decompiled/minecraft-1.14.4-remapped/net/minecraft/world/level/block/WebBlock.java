package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WebBlock extends Block {
   public WebBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      entity.makeStuckInBlock(blockState, new Vec3(0.25D, 0.05000000074505806D, 0.25D));
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }
}
