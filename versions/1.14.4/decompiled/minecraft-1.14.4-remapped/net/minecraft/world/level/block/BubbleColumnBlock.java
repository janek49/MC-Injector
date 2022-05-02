package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BubbleColumnBlock extends Block implements BucketPickup {
   public static final BooleanProperty DRAG_DOWN = BlockStateProperties.DRAG;

   public BubbleColumnBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(DRAG_DOWN, Boolean.valueOf(true)));
   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      BlockState blockState = level.getBlockState(blockPos.above());
      if(blockState.isAir()) {
         entity.onAboveBubbleCol(((Boolean)blockState.getValue(DRAG_DOWN)).booleanValue());
         if(!level.isClientSide) {
            ServerLevel var6 = (ServerLevel)level;

            for(int var7 = 0; var7 < 2; ++var7) {
               var6.sendParticles(ParticleTypes.SPLASH, (double)((float)blockPos.getX() + level.random.nextFloat()), (double)(blockPos.getY() + 1), (double)((float)blockPos.getZ() + level.random.nextFloat()), 1, 0.0D, 0.0D, 0.0D, 1.0D);
               var6.sendParticles(ParticleTypes.BUBBLE, (double)((float)blockPos.getX() + level.random.nextFloat()), (double)(blockPos.getY() + 1), (double)((float)blockPos.getZ() + level.random.nextFloat()), 1, 0.0D, 0.01D, 0.0D, 0.2D);
            }
         }
      } else {
         entity.onInsideBubbleColumn(((Boolean)blockState.getValue(DRAG_DOWN)).booleanValue());
      }

   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      growColumn(level, blockPos.above(), getDrag(level, blockPos.below()));
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      growColumn(level, blockPos.above(), getDrag(level, blockPos));
   }

   public FluidState getFluidState(BlockState blockState) {
      return Fluids.WATER.getSource(false);
   }

   public static void growColumn(LevelAccessor levelAccessor, BlockPos blockPos, boolean var2) {
      if(canExistIn(levelAccessor, blockPos)) {
         levelAccessor.setBlock(blockPos, (BlockState)Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(DRAG_DOWN, Boolean.valueOf(var2)), 2);
      }

   }

   public static boolean canExistIn(LevelAccessor levelAccessor, BlockPos blockPos) {
      FluidState var2 = levelAccessor.getFluidState(blockPos);
      return levelAccessor.getBlockState(blockPos).getBlock() == Blocks.WATER && var2.getAmount() >= 8 && var2.isSource();
   }

   private static boolean getDrag(BlockGetter blockGetter, BlockPos blockPos) {
      BlockState var2 = blockGetter.getBlockState(blockPos);
      Block var3 = var2.getBlock();
      return var3 == Blocks.BUBBLE_COLUMN?((Boolean)var2.getValue(DRAG_DOWN)).booleanValue():var3 != Blocks.SOUL_SAND;
   }

   public int getTickDelay(LevelReader levelReader) {
      return 5;
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      double var5 = (double)blockPos.getX();
      double var7 = (double)blockPos.getY();
      double var9 = (double)blockPos.getZ();
      if(((Boolean)blockState.getValue(DRAG_DOWN)).booleanValue()) {
         level.addAlwaysVisibleParticle(ParticleTypes.CURRENT_DOWN, var5 + 0.5D, var7 + 0.8D, var9, 0.0D, 0.0D, 0.0D);
         if(random.nextInt(200) == 0) {
            level.playLocalSound(var5, var7, var9, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
         }
      } else {
         level.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, var5 + 0.5D, var7, var9 + 0.5D, 0.0D, 0.04D, 0.0D);
         level.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, var5 + (double)random.nextFloat(), var7 + (double)random.nextFloat(), var9 + (double)random.nextFloat(), 0.0D, 0.04D, 0.0D);
         if(random.nextInt(200) == 0) {
            level.playLocalSound(var5, var7, var9, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
         }
      }

   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(!var1.canSurvive(levelAccessor, var5)) {
         return Blocks.WATER.defaultBlockState();
      } else {
         if(direction == Direction.DOWN) {
            levelAccessor.setBlock(var5, (BlockState)Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(DRAG_DOWN, Boolean.valueOf(getDrag(levelAccessor, var6))), 2);
         } else if(direction == Direction.UP && var3.getBlock() != Blocks.BUBBLE_COLUMN && canExistIn(levelAccessor, var6)) {
            levelAccessor.getBlockTicks().scheduleTick(var5, this, this.getTickDelay(levelAccessor));
         }

         levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
         return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
      }
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      Block var4 = levelReader.getBlockState(blockPos.below()).getBlock();
      return var4 == Blocks.BUBBLE_COLUMN || var4 == Blocks.MAGMA_BLOCK || var4 == Blocks.SOUL_SAND;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return Shapes.empty();
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.TRANSLUCENT;
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.INVISIBLE;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{DRAG_DOWN});
   }

   public Fluid takeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
      levelAccessor.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 11);
      return Fluids.WATER;
   }
}
