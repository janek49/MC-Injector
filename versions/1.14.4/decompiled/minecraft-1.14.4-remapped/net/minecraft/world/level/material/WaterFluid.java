package net.minecraft.world.level.material;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public abstract class WaterFluid extends FlowingFluid {
   public Fluid getFlowing() {
      return Fluids.FLOWING_WATER;
   }

   public Fluid getSource() {
      return Fluids.WATER;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.TRANSLUCENT;
   }

   public Item getBucket() {
      return Items.WATER_BUCKET;
   }

   public void animateTick(Level level, BlockPos blockPos, FluidState fluidState, Random random) {
      if(!fluidState.isSource() && !((Boolean)fluidState.getValue(FALLING)).booleanValue()) {
         if(random.nextInt(64) == 0) {
            level.playLocalSound((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
         }
      } else if(random.nextInt(10) == 0) {
         level.addParticle(ParticleTypes.UNDERWATER, (double)((float)blockPos.getX() + random.nextFloat()), (double)((float)blockPos.getY() + random.nextFloat()), (double)((float)blockPos.getZ() + random.nextFloat()), 0.0D, 0.0D, 0.0D);
      }

   }

   @Nullable
   public ParticleOptions getDripParticle() {
      return ParticleTypes.DRIPPING_WATER;
   }

   protected boolean canConvertToSource() {
      return true;
   }

   protected void beforeDestroyingBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
      BlockEntity var4 = blockState.getBlock().isEntityBlock()?levelAccessor.getBlockEntity(blockPos):null;
      Block.dropResources(blockState, levelAccessor.getLevel(), blockPos, var4);
   }

   public int getSlopeFindDistance(LevelReader levelReader) {
      return 4;
   }

   public BlockState createLegacyBlock(FluidState fluidState) {
      return (BlockState)Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, Integer.valueOf(getLegacyLevel(fluidState)));
   }

   public boolean isSame(Fluid fluid) {
      return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
   }

   public int getDropOff(LevelReader levelReader) {
      return 1;
   }

   public int getTickDelay(LevelReader levelReader) {
      return 5;
   }

   public boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos, Fluid fluid, Direction direction) {
      return direction == Direction.DOWN && !fluid.is(FluidTags.WATER);
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public static class Flowing extends WaterFluid {
      protected void createFluidStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
         super.createFluidStateDefinition(stateDefinition$Builder);
         stateDefinition$Builder.add(new Property[]{LEVEL});
      }

      public int getAmount(FluidState fluidState) {
         return ((Integer)fluidState.getValue(LEVEL)).intValue();
      }

      public boolean isSource(FluidState fluidState) {
         return false;
      }
   }

   public static class Source extends WaterFluid {
      public int getAmount(FluidState fluidState) {
         return 8;
      }

      public boolean isSource(FluidState fluidState) {
         return true;
      }
   }
}
