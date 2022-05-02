package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EnderChestBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
   public static final TranslatableComponent CONTAINER_TITLE = new TranslatableComponent("container.enderchest", new Object[0]);

   protected EnderChestBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public boolean hasCustomBreakingProgress(BlockState blockState) {
      return true;
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      FluidState var2 = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite())).setValue(WATERLOGGED, Boolean.valueOf(var2.getType() == Fluids.WATER));
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      PlayerEnderChestContainer var7 = player.getEnderChestInventory();
      BlockEntity var8 = level.getBlockEntity(blockPos);
      if(var7 != null && var8 instanceof EnderChestBlockEntity) {
         BlockPos var9 = blockPos.above();
         if(level.getBlockState(var9).isRedstoneConductor(level, var9)) {
            return true;
         } else if(level.isClientSide) {
            return true;
         } else {
            EnderChestBlockEntity var10 = (EnderChestBlockEntity)var8;
            var7.setActiveChest(var10);
            player.openMenu(new SimpleMenuProvider((var1, inventory, player) -> {
               return ChestMenu.threeRows(var1, inventory, var7);
            }, CONTAINER_TITLE));
            player.awardStat(Stats.OPEN_ENDERCHEST);
            return true;
         }
      } else {
         return true;
      }
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new EnderChestBlockEntity();
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      for(int var5 = 0; var5 < 3; ++var5) {
         int var6 = random.nextInt(2) * 2 - 1;
         int var7 = random.nextInt(2) * 2 - 1;
         double var8 = (double)blockPos.getX() + 0.5D + 0.25D * (double)var6;
         double var10 = (double)((float)blockPos.getY() + random.nextFloat());
         double var12 = (double)blockPos.getZ() + 0.5D + 0.25D * (double)var7;
         double var14 = (double)(random.nextFloat() * (float)var6);
         double var16 = ((double)random.nextFloat() - 0.5D) * 0.125D;
         double var18 = (double)(random.nextFloat() * (float)var7);
         level.addParticle(ParticleTypes.PORTAL, var8, var10, var12, var14, var16, var18);
      }

   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, WATERLOGGED});
   }

   public FluidState getFluidState(BlockState blockState) {
      return ((Boolean)blockState.getValue(WATERLOGGED)).booleanValue()?Fluids.WATER.getSource(false):super.getFluidState(blockState);
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(((Boolean)var1.getValue(WATERLOGGED)).booleanValue()) {
         levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      }

      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
