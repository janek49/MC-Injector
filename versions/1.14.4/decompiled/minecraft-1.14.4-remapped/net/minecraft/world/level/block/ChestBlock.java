package net.minecraft.world.level.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChestBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty TYPE = BlockStateProperties.CHEST_TYPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape NORTH_AABB = Block.box(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
   protected static final VoxelShape EAST_AABB = Block.box(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
   protected static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
   private static final ChestBlock.ChestSearchCallback CHEST_COMBINER = new ChestBlock.ChestSearchCallback() {
      public Container acceptDouble(ChestBlockEntity var1, ChestBlockEntity var2) {
         return new CompoundContainer(var1, var2);
      }

      public Container acceptSingle(ChestBlockEntity chestBlockEntity) {
         return chestBlockEntity;
      }

      // $FF: synthetic method
      public Object acceptSingle(ChestBlockEntity var1) {
         return this.acceptSingle(var1);
      }

      // $FF: synthetic method
      public Object acceptDouble(ChestBlockEntity var1, ChestBlockEntity var2) {
         return this.acceptDouble(var1, var2);
      }
   };
   private static final ChestBlock.ChestSearchCallback MENU_PROVIDER_COMBINER = new ChestBlock.ChestSearchCallback() {
      public MenuProvider acceptDouble(final ChestBlockEntity var1, final ChestBlockEntity var2) {
         final Container var3 = new CompoundContainer(var1, var2);
         return new MenuProvider() {
            @Nullable
            public AbstractContainerMenu createMenu(int var1x, Inventory inventory, Player player) {
               if(var1.canOpen(player) && var2.canOpen(player)) {
                  var1.unpackLootTable(inventory.player);
                  var2.unpackLootTable(inventory.player);
                  return ChestMenu.sixRows(var1x, inventory, var3);
               } else {
                  return null;
               }
            }

            public Component getDisplayName() {
               return (Component)(var1.hasCustomName()?var1.getDisplayName():(var2.hasCustomName()?var2.getDisplayName():new TranslatableComponent("container.chestDouble", new Object[0])));
            }
         };
      }

      public MenuProvider acceptSingle(ChestBlockEntity chestBlockEntity) {
         return chestBlockEntity;
      }

      // $FF: synthetic method
      public Object acceptSingle(ChestBlockEntity var1) {
         return this.acceptSingle(var1);
      }

      // $FF: synthetic method
      public Object acceptDouble(ChestBlockEntity var1, ChestBlockEntity var2) {
         return this.acceptDouble(var1, var2);
      }
   };

   protected ChestBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, ChestType.SINGLE)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public boolean hasCustomBreakingProgress(BlockState blockState) {
      return true;
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(((Boolean)var1.getValue(WATERLOGGED)).booleanValue()) {
         levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      }

      if(var3.getBlock() == this && direction.getAxis().isHorizontal()) {
         ChestType var7 = (ChestType)var3.getValue(TYPE);
         if(var1.getValue(TYPE) == ChestType.SINGLE && var7 != ChestType.SINGLE && var1.getValue(FACING) == var3.getValue(FACING) && getConnectedDirection(var3) == direction.getOpposite()) {
            return (BlockState)var1.setValue(TYPE, var7.getOpposite());
         }
      } else if(getConnectedDirection(var1) == direction) {
         return (BlockState)var1.setValue(TYPE, ChestType.SINGLE);
      }

      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      if(blockState.getValue(TYPE) == ChestType.SINGLE) {
         return AABB;
      } else {
         switch(getConnectedDirection(blockState)) {
         case NORTH:
         default:
            return NORTH_AABB;
         case SOUTH:
            return SOUTH_AABB;
         case WEST:
            return WEST_AABB;
         case EAST:
            return EAST_AABB;
         }
      }
   }

   public static Direction getConnectedDirection(BlockState blockState) {
      Direction direction = (Direction)blockState.getValue(FACING);
      return blockState.getValue(TYPE) == ChestType.LEFT?direction.getClockWise():direction.getCounterClockWise();
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      ChestType var2 = ChestType.SINGLE;
      Direction var3 = blockPlaceContext.getHorizontalDirection().getOpposite();
      FluidState var4 = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
      boolean var5 = blockPlaceContext.isSneaking();
      Direction var6 = blockPlaceContext.getClickedFace();
      if(var6.getAxis().isHorizontal() && var5) {
         Direction var7 = this.candidatePartnerFacing(blockPlaceContext, var6.getOpposite());
         if(var7 != null && var7.getAxis() != var6.getAxis()) {
            var3 = var7;
            var2 = var7.getCounterClockWise() == var6.getOpposite()?ChestType.RIGHT:ChestType.LEFT;
         }
      }

      if(var2 == ChestType.SINGLE && !var5) {
         if(var3 == this.candidatePartnerFacing(blockPlaceContext, var3.getClockWise())) {
            var2 = ChestType.LEFT;
         } else if(var3 == this.candidatePartnerFacing(blockPlaceContext, var3.getCounterClockWise())) {
            var2 = ChestType.RIGHT;
         }
      }

      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var3)).setValue(TYPE, var2)).setValue(WATERLOGGED, Boolean.valueOf(var4.getType() == Fluids.WATER));
   }

   public FluidState getFluidState(BlockState blockState) {
      return ((Boolean)blockState.getValue(WATERLOGGED)).booleanValue()?Fluids.WATER.getSource(false):super.getFluidState(blockState);
   }

   @Nullable
   private Direction candidatePartnerFacing(BlockPlaceContext blockPlaceContext, Direction var2) {
      BlockState var3 = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos().relative(var2));
      return var3.getBlock() == this && var3.getValue(TYPE) == ChestType.SINGLE?(Direction)var3.getValue(FACING):null;
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
      if(itemStack.hasCustomHoverName()) {
         BlockEntity var6 = level.getBlockEntity(blockPos);
         if(var6 instanceof ChestBlockEntity) {
            ((ChestBlockEntity)var6).setCustomName(itemStack.getHoverName());
         }
      }

   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var1.getBlock() != var4.getBlock()) {
         BlockEntity var6 = level.getBlockEntity(blockPos);
         if(var6 instanceof Container) {
            Containers.dropContents(level, blockPos, (Container)var6);
            level.updateNeighbourForOutputSignal(blockPos, this);
         }

         super.onRemove(var1, level, blockPos, var4, var5);
      }
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(level.isClientSide) {
         return true;
      } else {
         MenuProvider var7 = this.getMenuProvider(blockState, level, blockPos);
         if(var7 != null) {
            player.openMenu(var7);
            player.awardStat(this.getOpenChestStat());
         }

         return true;
      }
   }

   protected Stat getOpenChestStat() {
      return Stats.CUSTOM.get(Stats.OPEN_CHEST);
   }

   @Nullable
   public static Object combineWithNeigbour(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, boolean var3, ChestBlock.ChestSearchCallback chestBlock$ChestSearchCallback) {
      BlockEntity var5 = levelAccessor.getBlockEntity(blockPos);
      if(!(var5 instanceof ChestBlockEntity)) {
         return null;
      } else if(!var3 && isChestBlockedAt(levelAccessor, blockPos)) {
         return null;
      } else {
         ChestBlockEntity var6 = (ChestBlockEntity)var5;
         ChestType var7 = (ChestType)blockState.getValue(TYPE);
         if(var7 == ChestType.SINGLE) {
            return chestBlock$ChestSearchCallback.acceptSingle(var6);
         } else {
            BlockPos var8 = blockPos.relative(getConnectedDirection(blockState));
            BlockState var9 = levelAccessor.getBlockState(var8);
            if(var9.getBlock() == blockState.getBlock()) {
               ChestType var10 = (ChestType)var9.getValue(TYPE);
               if(var10 != ChestType.SINGLE && var7 != var10 && var9.getValue(FACING) == blockState.getValue(FACING)) {
                  if(!var3 && isChestBlockedAt(levelAccessor, var8)) {
                     return null;
                  }

                  BlockEntity var11 = levelAccessor.getBlockEntity(var8);
                  if(var11 instanceof ChestBlockEntity) {
                     ChestBlockEntity var12 = var7 == ChestType.RIGHT?var6:(ChestBlockEntity)var11;
                     ChestBlockEntity var13 = var7 == ChestType.RIGHT?(ChestBlockEntity)var11:var6;
                     return chestBlock$ChestSearchCallback.acceptDouble(var12, var13);
                  }
               }
            }

            return chestBlock$ChestSearchCallback.acceptSingle(var6);
         }
      }
   }

   @Nullable
   public static Container getContainer(BlockState blockState, Level level, BlockPos blockPos, boolean var3) {
      return (Container)combineWithNeigbour(blockState, level, blockPos, var3, CHEST_COMBINER);
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos blockPos) {
      return (MenuProvider)combineWithNeigbour(blockState, level, blockPos, false, MENU_PROVIDER_COMBINER);
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new ChestBlockEntity();
   }

   private static boolean isChestBlockedAt(LevelAccessor levelAccessor, BlockPos blockPos) {
      return isBlockedChestByBlock(levelAccessor, blockPos) || isCatSittingOnChest(levelAccessor, blockPos);
   }

   private static boolean isBlockedChestByBlock(BlockGetter blockGetter, BlockPos blockPos) {
      BlockPos blockPos = blockPos.above();
      return blockGetter.getBlockState(blockPos).isRedstoneConductor(blockGetter, blockPos);
   }

   private static boolean isCatSittingOnChest(LevelAccessor levelAccessor, BlockPos blockPos) {
      List<Cat> var2 = levelAccessor.getEntitiesOfClass(Cat.class, new AABB((double)blockPos.getX(), (double)(blockPos.getY() + 1), (double)blockPos.getZ(), (double)(blockPos.getX() + 1), (double)(blockPos.getY() + 2), (double)(blockPos.getZ() + 1)));
      if(!var2.isEmpty()) {
         for(Cat var4 : var2) {
            if(var4.isSitting()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean hasAnalogOutputSignal(BlockState blockState) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
      return AbstractContainerMenu.getRedstoneSignalFromContainer(getContainer(blockState, level, blockPos, false));
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, TYPE, WATERLOGGED});
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }

   interface ChestSearchCallback {
      Object acceptDouble(ChestBlockEntity var1, ChestBlockEntity var2);

      Object acceptSingle(ChestBlockEntity var1);
   }
}
