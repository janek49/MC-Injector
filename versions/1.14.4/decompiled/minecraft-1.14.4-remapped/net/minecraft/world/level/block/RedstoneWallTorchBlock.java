package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedstoneWallTorchBlock extends RedstoneTorchBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

   protected RedstoneWallTorchBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(LIT, Boolean.valueOf(true)));
   }

   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return WallTorchBlock.getShape(blockState);
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return Blocks.WALL_TORCH.canSurvive(blockState, levelReader, blockPos);
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return Blocks.WALL_TORCH.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockState blockState = Blocks.WALL_TORCH.getStateForPlacement(blockPlaceContext);
      return blockState == null?null:(BlockState)this.defaultBlockState().setValue(FACING, blockState.getValue(FACING));
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(((Boolean)blockState.getValue(LIT)).booleanValue()) {
         Direction var5 = ((Direction)blockState.getValue(FACING)).getOpposite();
         double var6 = 0.27D;
         double var8 = (double)blockPos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)var5.getStepX();
         double var10 = (double)blockPos.getY() + 0.7D + (random.nextDouble() - 0.5D) * 0.2D + 0.22D;
         double var12 = (double)blockPos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)var5.getStepZ();
         level.addParticle(DustParticleOptions.REDSTONE, var8, var10, var12, 0.0D, 0.0D, 0.0D);
      }
   }

   protected boolean hasNeighborSignal(Level level, BlockPos blockPos, BlockState blockState) {
      Direction var4 = ((Direction)blockState.getValue(FACING)).getOpposite();
      return level.hasSignal(blockPos.relative(var4), var4);
   }

   public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return ((Boolean)blockState.getValue(LIT)).booleanValue() && blockState.getValue(FACING) != direction?15:0;
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return Blocks.WALL_TORCH.rotate(var1, rotation);
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return Blocks.WALL_TORCH.mirror(var1, mirror);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, LIT});
   }
}
