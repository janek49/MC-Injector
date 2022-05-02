package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class RotatedPillarBlock extends Block {
   public static final EnumProperty AXIS = BlockStateProperties.AXIS;

   public RotatedPillarBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AXIS, Direction.Axis.Y));
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      switch(rotation) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         switch((Direction.Axis)var1.getValue(AXIS)) {
         case X:
            return (BlockState)var1.setValue(AXIS, Direction.Axis.Z);
         case Z:
            return (BlockState)var1.setValue(AXIS, Direction.Axis.X);
         default:
            return var1;
         }
      default:
         return var1;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{AXIS});
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)this.defaultBlockState().setValue(AXIS, blockPlaceContext.getClickedFace().getAxis());
   }
}
