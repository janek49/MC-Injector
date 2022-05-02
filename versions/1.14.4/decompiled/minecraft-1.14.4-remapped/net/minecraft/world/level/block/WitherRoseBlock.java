package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WitherRoseBlock extends FlowerBlock {
   public WitherRoseBlock(MobEffect mobEffect, Block.Properties block$Properties) {
      super(mobEffect, 8, block$Properties);
   }

   protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      Block var4 = blockState.getBlock();
      return super.mayPlaceOn(blockState, blockGetter, blockPos) || var4 == Blocks.NETHERRACK || var4 == Blocks.SOUL_SAND;
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      VoxelShape var5 = this.getShape(blockState, level, blockPos, CollisionContext.empty());
      Vec3 var6 = var5.bounds().getCenter();
      double var7 = (double)blockPos.getX() + var6.x;
      double var9 = (double)blockPos.getZ() + var6.z;

      for(int var11 = 0; var11 < 3; ++var11) {
         if(random.nextBoolean()) {
            level.addParticle(ParticleTypes.SMOKE, var7 + (double)(random.nextFloat() / 5.0F), (double)blockPos.getY() + (0.5D - (double)random.nextFloat()), var9 + (double)(random.nextFloat() / 5.0F), 0.0D, 0.0D, 0.0D);
         }
      }

   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      if(!level.isClientSide && level.getDifficulty() != Difficulty.PEACEFUL) {
         if(entity instanceof LivingEntity) {
            LivingEntity var5 = (LivingEntity)entity;
            if(!var5.isInvulnerableTo(DamageSource.WITHER)) {
               var5.addEffect(new MobEffectInstance(MobEffects.WITHER, 40));
            }
         }

      }
   }
}
