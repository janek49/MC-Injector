package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.PositionImpl;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class DispenserBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = DirectionalBlock.FACING;
   public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
   private static final Map DISPENSER_REGISTRY = (Map)Util.make(new Object2ObjectOpenHashMap(), (object2ObjectOpenHashMap) -> {
      object2ObjectOpenHashMap.defaultReturnValue(new DefaultDispenseItemBehavior());
   });

   public static void registerBehavior(ItemLike itemLike, DispenseItemBehavior dispenseItemBehavior) {
      DISPENSER_REGISTRY.put(itemLike.asItem(), dispenseItemBehavior);
   }

   protected DispenserBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TRIGGERED, Boolean.valueOf(false)));
   }

   public int getTickDelay(LevelReader levelReader) {
      return 4;
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(level.isClientSide) {
         return true;
      } else {
         BlockEntity var7 = level.getBlockEntity(blockPos);
         if(var7 instanceof DispenserBlockEntity) {
            player.openMenu((DispenserBlockEntity)var7);
            if(var7 instanceof DropperBlockEntity) {
               player.awardStat(Stats.INSPECT_DROPPER);
            } else {
               player.awardStat(Stats.INSPECT_DISPENSER);
            }
         }

         return true;
      }
   }

   protected void dispenseFrom(Level level, BlockPos blockPos) {
      BlockSourceImpl var3 = new BlockSourceImpl(level, blockPos);
      DispenserBlockEntity var4 = (DispenserBlockEntity)var3.getEntity();
      int var5 = var4.getRandomSlot();
      if(var5 < 0) {
         level.levelEvent(1001, blockPos, 0);
      } else {
         ItemStack var6 = var4.getItem(var5);
         DispenseItemBehavior var7 = this.getDispenseMethod(var6);
         if(var7 != DispenseItemBehavior.NOOP) {
            var4.setItem(var5, var7.dispense(var3, var6));
         }

      }
   }

   protected DispenseItemBehavior getDispenseMethod(ItemStack itemStack) {
      return (DispenseItemBehavior)DISPENSER_REGISTRY.get(itemStack.getItem());
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      boolean var7 = level.hasNeighborSignal(var3) || level.hasNeighborSignal(var3.above());
      boolean var8 = ((Boolean)blockState.getValue(TRIGGERED)).booleanValue();
      if(var7 && !var8) {
         level.getBlockTicks().scheduleTick(var3, this, this.getTickDelay(level));
         level.setBlock(var3, (BlockState)blockState.setValue(TRIGGERED, Boolean.valueOf(true)), 4);
      } else if(!var7 && var8) {
         level.setBlock(var3, (BlockState)blockState.setValue(TRIGGERED, Boolean.valueOf(false)), 4);
      }

   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!level.isClientSide) {
         this.dispenseFrom(level, blockPos);
      }

   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new DispenserBlockEntity();
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getNearestLookingDirection().getOpposite());
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
      if(itemStack.hasCustomHoverName()) {
         BlockEntity var6 = level.getBlockEntity(blockPos);
         if(var6 instanceof DispenserBlockEntity) {
            ((DispenserBlockEntity)var6).setCustomName(itemStack.getHoverName());
         }
      }

   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var1.getBlock() != var4.getBlock()) {
         BlockEntity var6 = level.getBlockEntity(blockPos);
         if(var6 instanceof DispenserBlockEntity) {
            Containers.dropContents(level, (BlockPos)blockPos, (Container)((DispenserBlockEntity)var6));
            level.updateNeighbourForOutputSignal(blockPos, this);
         }

         super.onRemove(var1, level, blockPos, var4, var5);
      }
   }

   public static Position getDispensePosition(BlockSource blockSource) {
      Direction var1 = (Direction)blockSource.getBlockState().getValue(FACING);
      double var2 = blockSource.x() + 0.7D * (double)var1.getStepX();
      double var4 = blockSource.y() + 0.7D * (double)var1.getStepY();
      double var6 = blockSource.z() + 0.7D * (double)var1.getStepZ();
      return new PositionImpl(var2, var4, var6);
   }

   public boolean hasAnalogOutputSignal(BlockState blockState) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(blockPos));
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.MODEL;
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, TRIGGERED});
   }
}
