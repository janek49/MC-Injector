package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

public class PoweredRailBlock extends BaseRailBlock {
   public static final EnumProperty SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   protected PoweredRailBlock(Block.Properties block$Properties) {
      super(true, block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(SHAPE, RailShape.NORTH_SOUTH)).setValue(POWERED, Boolean.valueOf(false)));
   }

   protected boolean findPoweredRailSignal(Level level, BlockPos blockPos, BlockState blockState, boolean var4, int var5) {
      if(var5 >= 8) {
         return false;
      } else {
         int var6 = blockPos.getX();
         int var7 = blockPos.getY();
         int var8 = blockPos.getZ();
         boolean var9 = true;
         RailShape var10 = (RailShape)blockState.getValue(SHAPE);
         switch(var10) {
         case NORTH_SOUTH:
            if(var4) {
               ++var8;
            } else {
               --var8;
            }
            break;
         case EAST_WEST:
            if(var4) {
               --var6;
            } else {
               ++var6;
            }
            break;
         case ASCENDING_EAST:
            if(var4) {
               --var6;
            } else {
               ++var6;
               ++var7;
               var9 = false;
            }

            var10 = RailShape.EAST_WEST;
            break;
         case ASCENDING_WEST:
            if(var4) {
               --var6;
               ++var7;
               var9 = false;
            } else {
               ++var6;
            }

            var10 = RailShape.EAST_WEST;
            break;
         case ASCENDING_NORTH:
            if(var4) {
               ++var8;
            } else {
               --var8;
               ++var7;
               var9 = false;
            }

            var10 = RailShape.NORTH_SOUTH;
            break;
         case ASCENDING_SOUTH:
            if(var4) {
               ++var8;
               ++var7;
               var9 = false;
            } else {
               --var8;
            }

            var10 = RailShape.NORTH_SOUTH;
         }

         return this.isSameRailWithPower(level, new BlockPos(var6, var7, var8), var4, var5, var10)?true:var9 && this.isSameRailWithPower(level, new BlockPos(var6, var7 - 1, var8), var4, var5, var10);
      }
   }

   protected boolean isSameRailWithPower(Level level, BlockPos blockPos, boolean var3, int var4, RailShape railShape) {
      BlockState var6 = level.getBlockState(blockPos);
      if(var6.getBlock() != this) {
         return false;
      } else {
         RailShape var7 = (RailShape)var6.getValue(SHAPE);
         return railShape != RailShape.EAST_WEST || var7 != RailShape.NORTH_SOUTH && var7 != RailShape.ASCENDING_NORTH && var7 != RailShape.ASCENDING_SOUTH?(railShape != RailShape.NORTH_SOUTH || var7 != RailShape.EAST_WEST && var7 != RailShape.ASCENDING_EAST && var7 != RailShape.ASCENDING_WEST?(((Boolean)var6.getValue(POWERED)).booleanValue()?(level.hasNeighborSignal(blockPos)?true:this.findPoweredRailSignal(level, blockPos, var6, var3, var4 + 1)):false):false):false;
      }
   }

   protected void updateState(BlockState blockState, Level level, BlockPos blockPos, Block block) {
      boolean var5 = ((Boolean)blockState.getValue(POWERED)).booleanValue();
      boolean var6 = level.hasNeighborSignal(blockPos) || this.findPoweredRailSignal(level, blockPos, blockState, true, 0) || this.findPoweredRailSignal(level, blockPos, blockState, false, 0);
      if(var6 != var5) {
         level.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, Boolean.valueOf(var6)), 3);
         level.updateNeighborsAt(blockPos.below(), this);
         if(((RailShape)blockState.getValue(SHAPE)).isAscending()) {
            level.updateNeighborsAt(blockPos.above(), this);
         }
      }

   }

   public Property getShapeProperty() {
      return SHAPE;
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
         case NORTH_SOUTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_SOUTH);
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
         }
      case CLOCKWISE_90:
         switch((RailShape)var1.getValue(SHAPE)) {
         case NORTH_SOUTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_SOUTH);
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
