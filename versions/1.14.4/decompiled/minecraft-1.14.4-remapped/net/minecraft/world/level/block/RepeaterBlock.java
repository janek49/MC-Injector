package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class RepeaterBlock extends DiodeBlock {
   public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
   public static final IntegerProperty DELAY = BlockStateProperties.DELAY;

   protected RepeaterBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(DELAY, Integer.valueOf(1))).setValue(LOCKED, Boolean.valueOf(false))).setValue(POWERED, Boolean.valueOf(false)));
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(!player.abilities.mayBuild) {
         return false;
      } else {
         level.setBlock(blockPos, (BlockState)blockState.cycle(DELAY), 3);
         return true;
      }
   }

   protected int getDelay(BlockState blockState) {
      return ((Integer)blockState.getValue(DELAY)).intValue() * 2;
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockState blockState = super.getStateForPlacement(blockPlaceContext);
      return (BlockState)blockState.setValue(LOCKED, Boolean.valueOf(this.isLocked(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos(), blockState)));
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return !levelAccessor.isClientSide() && direction.getAxis() != ((Direction)var1.getValue(FACING)).getAxis()?(BlockState)var1.setValue(LOCKED, Boolean.valueOf(this.isLocked(levelAccessor, var5, var1))):super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public boolean isLocked(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
      return this.getAlternateSignal(levelReader, blockPos, blockState) > 0;
   }

   protected boolean isAlternateInput(BlockState blockState) {
      return isDiode(blockState);
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(((Boolean)blockState.getValue(POWERED)).booleanValue()) {
         Direction var5 = (Direction)blockState.getValue(FACING);
         double var6 = (double)((float)blockPos.getX() + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2D;
         double var8 = (double)((float)blockPos.getY() + 0.4F) + (double)(random.nextFloat() - 0.5F) * 0.2D;
         double var10 = (double)((float)blockPos.getZ() + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2D;
         float var12 = -5.0F;
         if(random.nextBoolean()) {
            var12 = (float)(((Integer)blockState.getValue(DELAY)).intValue() * 2 - 1);
         }

         var12 = var12 / 16.0F;
         double var13 = (double)(var12 * (float)var5.getStepX());
         double var15 = (double)(var12 * (float)var5.getStepZ());
         level.addParticle(DustParticleOptions.REDSTONE, var6 + var13, var8, var10 + var15, 0.0D, 0.0D, 0.0D);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, DELAY, LOCKED, POWERED});
   }
}
