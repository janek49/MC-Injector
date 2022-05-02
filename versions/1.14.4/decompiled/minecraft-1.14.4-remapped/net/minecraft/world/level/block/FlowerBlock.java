package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlowerBlock extends BushBlock {
   protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
   private final MobEffect suspiciousStewEffect;
   private final int effectDuration;

   public FlowerBlock(MobEffect suspiciousStewEffect, int effectDuration, Block.Properties block$Properties) {
      super(block$Properties);
      this.suspiciousStewEffect = suspiciousStewEffect;
      if(suspiciousStewEffect.isInstantenous()) {
         this.effectDuration = effectDuration;
      } else {
         this.effectDuration = effectDuration * 20;
      }

   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      Vec3 var5 = blockState.getOffset(blockGetter, blockPos);
      return SHAPE.move(var5.x, var5.y, var5.z);
   }

   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.XZ;
   }

   public MobEffect getSuspiciousStewEffect() {
      return this.suspiciousStewEffect;
   }

   public int getEffectDuration() {
      return this.effectDuration;
   }
}
