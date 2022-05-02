package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;

public class MagmaBlock extends Block {
   public MagmaBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public void stepOn(Level level, BlockPos blockPos, Entity entity) {
      if(!entity.fireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
         entity.hurt(DamageSource.HOT_FLOOR, 1.0F);
      }

      super.stepOn(level, blockPos, entity);
   }

   public int getLightColor(BlockState blockState, BlockAndBiomeGetter blockAndBiomeGetter, BlockPos blockPos) {
      return 15728880;
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      BubbleColumnBlock.growColumn(level, blockPos.above(), true);
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(direction == Direction.UP && var3.getBlock() == Blocks.WATER) {
         levelAccessor.getBlockTicks().scheduleTick(var5, this, this.getTickDelay(levelAccessor));
      }

      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public void randomTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      BlockPos blockPos = blockPos.above();
      if(level.getFluidState(blockPos).is(FluidTags.WATER)) {
         level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
         if(level instanceof ServerLevel) {
            ((ServerLevel)level).sendParticles(ParticleTypes.LARGE_SMOKE, (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.25D, (double)blockPos.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
         }
      }

   }

   public int getTickDelay(LevelReader levelReader) {
      return 20;
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      level.getBlockTicks().scheduleTick(blockPos, this, this.getTickDelay(level));
   }

   public boolean isValidSpawn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType entityType) {
      return entityType.fireImmune();
   }

   public boolean hasPostProcess(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return true;
   }
}
