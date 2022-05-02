package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

public class RailBlock extends BaseRailBlock {
   public static final EnumProperty SHAPE = BlockStateProperties.RAIL_SHAPE;

   protected RailBlock(Block.Properties block$Properties) {
      super(false, block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(SHAPE, RailShape.NORTH_SOUTH));
   }

   protected void updateState(BlockState blockState, Level level, BlockPos blockPos, Block block) {
      if(block.defaultBlockState().isSignalSource() && (new RailState(level, blockPos, blockState)).countPotentialConnections() == 3) {
         this.updateDir(level, blockPos, blockState, false);
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
      stateDefinition$Builder.add(new Property[]{SHAPE});
   }
}
