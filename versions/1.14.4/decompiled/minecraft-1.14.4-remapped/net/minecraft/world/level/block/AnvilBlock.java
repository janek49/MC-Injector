package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AnvilBlock extends FallingBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final VoxelShape BASE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
   private static final VoxelShape X_LEG1 = Block.box(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D);
   private static final VoxelShape X_LEG2 = Block.box(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D);
   private static final VoxelShape X_TOP = Block.box(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D);
   private static final VoxelShape Z_LEG1 = Block.box(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D);
   private static final VoxelShape Z_LEG2 = Block.box(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D);
   private static final VoxelShape Z_TOP = Block.box(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D);
   private static final VoxelShape X_AXIS_AABB = Shapes.or(BASE, new VoxelShape[]{X_LEG1, X_LEG2, X_TOP});
   private static final VoxelShape Z_AXIS_AABB = Shapes.or(BASE, new VoxelShape[]{Z_LEG1, Z_LEG2, Z_TOP});
   private static final TranslatableComponent CONTAINER_TITLE = new TranslatableComponent("container.repair", new Object[0]);

   public AnvilBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getClockWise());
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      player.openMenu(blockState.getMenuProvider(level, blockPos));
      return true;
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos blockPos) {
      return new SimpleMenuProvider((var2, inventory, player) -> {
         return new AnvilMenu(var2, inventory, ContainerLevelAccess.create(level, blockPos));
      }, CONTAINER_TITLE);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      Direction var5 = (Direction)blockState.getValue(FACING);
      return var5.getAxis() == Direction.Axis.X?X_AXIS_AABB:Z_AXIS_AABB;
   }

   protected void falling(FallingBlockEntity fallingBlockEntity) {
      fallingBlockEntity.setHurtsEntities(true);
   }

   public void onLand(Level level, BlockPos blockPos, BlockState var3, BlockState var4) {
      level.levelEvent(1031, blockPos, 0);
   }

   public void onBroken(Level level, BlockPos blockPos) {
      level.levelEvent(1029, blockPos, 0);
   }

   @Nullable
   public static BlockState damage(BlockState blockState) {
      Block var1 = blockState.getBlock();
      return var1 == Blocks.ANVIL?(BlockState)Blocks.CHIPPED_ANVIL.defaultBlockState().setValue(FACING, blockState.getValue(FACING)):(var1 == Blocks.CHIPPED_ANVIL?(BlockState)Blocks.DAMAGED_ANVIL.defaultBlockState().setValue(FACING, blockState.getValue(FACING)):null);
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING});
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
