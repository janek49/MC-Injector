package net.minecraft.world.level.block;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;

public class DetectorRailBlock extends BaseRailBlock {
   public static final EnumProperty SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   public DetectorRailBlock(Block.Properties block$Properties) {
      super(true, block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, Boolean.valueOf(false))).setValue(SHAPE, RailShape.NORTH_SOUTH));
   }

   public int getTickDelay(LevelReader levelReader) {
      return 20;
   }

   public boolean isSignalSource(BlockState blockState) {
      return true;
   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      if(!level.isClientSide) {
         if(!((Boolean)blockState.getValue(POWERED)).booleanValue()) {
            this.checkPressed(level, blockPos, blockState);
         }
      }
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!level.isClientSide && ((Boolean)blockState.getValue(POWERED)).booleanValue()) {
         this.checkPressed(level, blockPos, blockState);
      }
   }

   public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return ((Boolean)blockState.getValue(POWERED)).booleanValue()?15:0;
   }

   public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return !((Boolean)blockState.getValue(POWERED)).booleanValue()?0:(direction == Direction.UP?15:0);
   }

   private void checkPressed(Level level, BlockPos blockPos, BlockState blockState) {
      boolean var4 = ((Boolean)blockState.getValue(POWERED)).booleanValue();
      boolean var5 = false;
      List<AbstractMinecart> var6 = this.getInteractingMinecartOfType(level, blockPos, AbstractMinecart.class, (Predicate)null);
      if(!var6.isEmpty()) {
         var5 = true;
      }

      if(var5 && !var4) {
         BlockState var7 = (BlockState)blockState.setValue(POWERED, Boolean.valueOf(true));
         level.setBlock(blockPos, var7, 3);
         this.updatePowerToConnected(level, blockPos, var7, true);
         level.updateNeighborsAt(blockPos, this);
         level.updateNeighborsAt(blockPos.below(), this);
         level.setBlocksDirty(blockPos, blockState, var7);
      }

      if(!var5 && var4) {
         BlockState var7 = (BlockState)blockState.setValue(POWERED, Boolean.valueOf(false));
         level.setBlock(blockPos, var7, 3);
         this.updatePowerToConnected(level, blockPos, var7, false);
         level.updateNeighborsAt(blockPos, this);
         level.updateNeighborsAt(blockPos.below(), this);
         level.setBlocksDirty(blockPos, blockState, var7);
      }

      if(var5) {
         level.getBlockTicks().scheduleTick(blockPos, this, this.getTickDelay(level));
      }

      level.updateNeighbourForOutputSignal(blockPos, this);
   }

   protected void updatePowerToConnected(Level level, BlockPos blockPos, BlockState blockState, boolean var4) {
      RailState var5 = new RailState(level, blockPos, blockState);

      for(BlockPos var8 : var5.getConnections()) {
         BlockState var9 = level.getBlockState(var8);
         var9.neighborChanged(level, var8, var9.getBlock(), blockPos, false);
      }

   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var4.getBlock() != var1.getBlock()) {
         super.onPlace(var1, level, blockPos, var4, var5);
         this.checkPressed(level, blockPos, var1);
      }
   }

   public Property getShapeProperty() {
      return SHAPE;
   }

   public boolean hasAnalogOutputSignal(BlockState blockState) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
      if(((Boolean)blockState.getValue(POWERED)).booleanValue()) {
         List<MinecartCommandBlock> var4 = this.getInteractingMinecartOfType(level, blockPos, MinecartCommandBlock.class, (Predicate)null);
         if(!var4.isEmpty()) {
            return ((MinecartCommandBlock)var4.get(0)).getCommandBlock().getSuccessCount();
         }

         List<AbstractMinecart> var5 = this.getInteractingMinecartOfType(level, blockPos, AbstractMinecart.class, EntitySelector.CONTAINER_ENTITY_SELECTOR);
         if(!var5.isEmpty()) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)var5.get(0));
         }
      }

      return 0;
   }

   protected List getInteractingMinecartOfType(Level level, BlockPos blockPos, Class class, @Nullable Predicate predicate) {
      return level.getEntitiesOfClass(class, this.getSearchBB(blockPos), predicate);
   }

   private AABB getSearchBB(BlockPos blockPos) {
      float var2 = 0.2F;
      return new AABB((double)((float)blockPos.getX() + 0.2F), (double)blockPos.getY(), (double)((float)blockPos.getZ() + 0.2F), (double)((float)(blockPos.getX() + 1) - 0.2F), (double)((float)(blockPos.getY() + 1) - 0.2F), (double)((float)(blockPos.getZ() + 1) - 0.2F));
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      switch(rotation) {
      case CLOCKWISE_180:
         switch((RailShape)var1.getValue(SHAPE)) {
         case ASCENDING_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
         case SOUTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
         }
      case COUNTERCLOCKWISE_90:
         switch((RailShape)var1.getValue(SHAPE)) {
         case ASCENDING_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_NORTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_SOUTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case SOUTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_SOUTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_SOUTH);
         }
      case CLOCKWISE_90:
         switch((RailShape)var1.getValue(SHAPE)) {
         case ASCENDING_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_NORTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_SOUTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case SOUTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_SOUTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_SOUTH);
         }
      default:
         return var1;
      }
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      RailShape var3 = (RailShape)var1.getValue(SHAPE);
      switch(mirror) {
      case LEFT_RIGHT:
         switch(var3) {
         case ASCENDING_NORTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
         default:
            return super.mirror(var1, mirror);
         }
      case FRONT_BACK:
         switch(var3) {
         case ASCENDING_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
         case ASCENDING_SOUTH:
         default:
            break;
         case SOUTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
         }
      }

      return super.mirror(var1, mirror);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{SHAPE, POWERED});
   }
}
