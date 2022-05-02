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
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public abstract class LavaFluid extends FlowingFluid {
   public Fluid getFlowing() {
      return Fluids.FLOWING_LAVA;
   }

   public Fluid getSource() {
      return Fluids.LAVA;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.SOLID;
   }

   public Item getBucket() {
      return Items.LAVA_BUCKET;
   }

   public void animateTick(Level level, BlockPos blockPos, FluidState fluidState, Random random) {
      BlockPos blockPos = blockPos.above();
      if(level.getBlockState(blockPos).isAir() && !level.getBlockState(blockPos).isSolidRender(level, blockPos)) {
         if(random.nextInt(100) == 0) {
            double var6 = (double)((float)blockPos.getX() + random.nextFloat());
            double var8 = (double)(blockPos.getY() + 1);
            double var10 = (double)((float)blockPos.getZ() + random.nextFloat());
            level.addParticle(ParticleTypes.LAVA, var6, var8, var10, 0.0D, 0.0D, 0.0D);
            level.playLocalSound(var6, var8, var10, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
         }

         if(random.nextInt(200) == 0) {
            level.playLocalSound((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
         }
      }

   }

   public void randomTick(Level level, BlockPos blockPos, FluidState fluidState, Random random) {
      if(level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
         int var5 = random.nextInt(3);
         if(var5 > 0) {
            BlockPos var6 = blockPos;

            for(int var7 = 0; var7 < var5; ++var7) {
               var6 = var6.offset(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
               if(!level.isLoaded(var6)) {
                  return;
               }

               BlockState var8 = level.getBlockState(var6);
               if(var8.isAir()) {
                  if(this.hasFlammableNeighbours(level, var6)) {
                     level.setBlockAndUpdate(var6, Blocks.FIRE.defaultBlockState());
                     return;
                  }
               } else if(var8.getMaterial().blocksMotion()) {
                  return;
               }
            }
         } else {
            for(int var6 = 0; var6 < 3; ++var6) {
               BlockPos var7 = blockPos.offset(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
               if(!level.isLoaded(var7)) {
                  return;
               }

               if(level.isEmptyBlock(var7.above()) && this.isFlammable(level, var7)) {
                  level.setBlockAndUpdate(var7.above(), Blocks.FIRE.defaultBlockState());
               }
            }
         }

      }
   }

   private boolean hasFlammableNeighbours(LevelReader levelReader, BlockPos blockPos) {
      for(Direction var6 : Direction.values()) {
         if(this.isFlammable(levelReader, blockPos.relative(var6))) {
            return true;
         }
      }

      return false;
   }

   private boolean isFlammable(LevelReader levelReader, BlockPos blockPos) {
      return blockPos.getY() >= 0 && blockPos.getY() < 256 && !levelReader.hasChunkAt(blockPos)?false:levelReader.getBlockState(blockPos).getMaterial().isFlammable();
   }

   @Nullable
   public ParticleOptions getDripParticle() {
      return ParticleTypes.DRIPPING_LAVA;
   }

   protected void beforeDestroyingBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
      this.fizz(levelAccessor, blockPos);
   }

   public int getSlopeFindDistance(LevelReader levelReader) {
      return levelReader.getDimension().isUltraWarm()?4:2;
   }

   public BlockState createLegacyBlock(FluidState fluidState) {
      return (BlockState)Blocks.LAVA.defaultBlockState().setValue(LiquidBlock.LEVEL, Integer.valueOf(getLegacyLevel(fluidState)));
   }

   public boolean isSame(Fluid fluid) {
      return fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA;
   }

   public int getDropOff(LevelReader levelReader) {
      return levelReader.getDimension().isUltraWarm()?1:2;
   }

   public boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos, Fluid fluid, Direction direction) {
      return fluidState.getHeight(blockGetter, blockPos) >= 0.44444445F && fluid.is(FluidTags.WATER);
   }

   public int getTickDelay(LevelReader levelReader) {
      return levelReader.getDimension().isHasCeiling()?10:30;
   }

   public int getSpreadDelay(Level level, BlockPos blockPos, FluidState var3, FluidState var4) {
      int var5 = this.getTickDelay(level);
      if(!var3.isEmpty() && !var4.isEmpty() && !((Boolean)var3.getValue(FALLING)).booleanValue() && !((Boolean)var4.getValue(FALLING)).booleanValue() && var4.getHeight(level, blockPos) > var3.getHeight(level, blockPos) && level.getRandom().nextInt(4) != 0) {
         var5 *= 4;
      }

      return var5;
   }

   private void fizz(LevelAccessor levelAccessor, BlockPos blockPos) {
      levelAccessor.levelEvent(1501, blockPos, 0);
   }

   protected boolean canConvertToSource() {
      return false;
   }

   protected void spreadTo(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
      if(direction == Direction.DOWN) {
         FluidState fluidState = levelAccessor.getFluidState(blockPos);
         if(this.is(FluidTags.LAVA) && fluidState.is(FluidTags.WATER)) {
            if(blockState.getBlock() instanceof LiquidBlock) {
               levelAccessor.setBlock(blockPos, Blocks.STONE.defaultBlockState(), 3);
            }

            this.fizz(levelAccessor, blockPos);
            return;
         }
      }

      super.spreadTo(levelAccessor, blockPos, blockState, direction, fluidState);
   }

   protected boolean isRandomlyTicking() {
      return true;
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public static class Flowing extends LavaFluid {
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

   public static class Source extends LavaFluid {
      public int getAmount(FluidState fluidState) {
         return 8;
      }

      public boolean isSource(FluidState fluidState) {
         return true;
      }
   }
}
