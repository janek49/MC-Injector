package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.phys.Vec3;

public class SlimeBlock extends HalfTransparentBlock {
   public SlimeBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.TRANSLUCENT;
   }

   public void fallOn(Level level, BlockPos blockPos, Entity entity, float var4) {
      if(entity.isSneaking()) {
         super.fallOn(level, blockPos, entity, var4);
      } else {
         entity.causeFallDamage(var4, 0.0F);
      }

   }

   public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
      if(entity.isSneaking()) {
         super.updateEntityAfterFallOn(blockGetter, entity);
      } else {
         Vec3 var3 = entity.getDeltaMovement();
         if(var3.y < 0.0D) {
            double var4 = entity instanceof LivingEntity?1.0D:0.8D;
            entity.setDeltaMovement(var3.x, -var3.y * var4, var3.z);
         }
      }

   }

   public void stepOn(Level level, BlockPos blockPos, Entity entity) {
      double var4 = Math.abs(entity.getDeltaMovement().y);
      if(var4 < 0.1D && !entity.isSneaking()) {
         double var6 = 0.4D + var4 * 0.2D;
         entity.setDeltaMovement(entity.getDeltaMovement().multiply(var6, 1.0D, var6));
      }

      super.stepOn(level, blockPos, entity);
   }
}
