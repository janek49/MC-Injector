package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallTorchBlock extends TorchBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final Map AABBS = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(5.5D, 3.0D, 11.0D, 10.5D, 13.0D, 16.0D), Direction.SOUTH, Block.box(5.5D, 3.0D, 0.0D, 10.5D, 13.0D, 5.0D), Direction.WEST, Block.box(11.0D, 3.0D, 5.5D, 16.0D, 13.0D, 10.5D), Direction.EAST, Block.box(0.0D, 3.0D, 5.5D, 5.0D, 13.0D, 10.5D)));

   protected WallTorchBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return getShape(blockState);
   }

   public static VoxelShape getShape(BlockState blockState) {
      return (VoxelShape)AABBS.get(blockState.getValue(FACING));
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      Direction var4 = (Direction)blockState.getValue(FACING);
      BlockPos var5 = blockPos.relative(var4.getOpposite());
      BlockState var6 = levelReader.getBlockState(var5);
      return var6.isFaceSturdy(levelReader, var5, var4);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockState blockState = this.defaultBlockState();
      LevelReader var3 = blockPlaceContext.getLevel();
      BlockPos var4 = blockPlaceContext.getClickedPos();
      Direction[] vars5 = blockPlaceContext.getNearestLookingDirections();

      for(Direction var9 : vars5) {
         if(var9.getAxis().isHorizontal()) {
            Direction var10 = var9.getOpposite();
            blockState = (BlockState)blockState.setValue(FACING, var10);
            if(blockState.canSurvive(var3, var4)) {
               return blockState;
            }
         }
      }

      return null;
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return direction.getOpposite() == var1.getValue(FACING) && !var1.canSurvive(levelAccessor, var5)?Blocks.AIR.defaultBlockState():var1;
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      Direction var5 = (Direction)blockState.getValue(FACING);
      double var6 = (double)blockPos.getX() + 0.5D;
      double var8 = (double)blockPos.getY() + 0.7D;
      double var10 = (double)blockPos.getZ() + 0.5D;
      double var12 = 0.22D;
      double var14 = 0.27D;
      Direction var16 = var5.getOpposite();
      level.addParticle(ParticleTypes.SMOKE, var6 + 0.27D * (double)var16.getStepX(), var8 + 0.22D, var10 + 0.27D * (double)var16.getStepZ(), 0.0D, 0.0D, 0.0D);
      level.addParticle(ParticleTypes.FLAME, var6 + 0.27D * (double)var16.getStepX(), var8 + 0.22D, var10 + 0.27D * (double)var16.getStepZ(), 0.0D, 0.0D, 0.0D);
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING});
   }
}
