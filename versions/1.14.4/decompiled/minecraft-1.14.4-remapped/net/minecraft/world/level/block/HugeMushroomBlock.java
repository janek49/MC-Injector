package net.minecraft.world.level.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class HugeMushroomBlock extends Block {
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   public static final BooleanProperty UP = PipeBlock.UP;
   public static final BooleanProperty DOWN = PipeBlock.DOWN;
   private static final Map PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

   public HugeMushroomBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, Boolean.valueOf(true))).setValue(EAST, Boolean.valueOf(true))).setValue(SOUTH, Boolean.valueOf(true))).setValue(WEST, Boolean.valueOf(true))).setValue(UP, Boolean.valueOf(true))).setValue(DOWN, Boolean.valueOf(true)));
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockGetter var2 = blockPlaceContext.getLevel();
      BlockPos var3 = blockPlaceContext.getClickedPos();
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(DOWN, Boolean.valueOf(this != var2.getBlockState(var3.below()).getBlock()))).setValue(UP, Boolean.valueOf(this != var2.getBlockState(var3.above()).getBlock()))).setValue(NORTH, Boolean.valueOf(this != var2.getBlockState(var3.north()).getBlock()))).setValue(EAST, Boolean.valueOf(this != var2.getBlockState(var3.east()).getBlock()))).setValue(SOUTH, Boolean.valueOf(this != var2.getBlockState(var3.south()).getBlock()))).setValue(WEST, Boolean.valueOf(this != var2.getBlockState(var3.west()).getBlock()));
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return var3.getBlock() == this?(BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(direction), Boolean.valueOf(false)):super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.NORTH)), var1.getValue(NORTH))).setValue((Property)PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.SOUTH)), var1.getValue(SOUTH))).setValue((Property)PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.EAST)), var1.getValue(EAST))).setValue((Property)PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.WEST)), var1.getValue(WEST))).setValue((Property)PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.UP)), var1.getValue(UP))).setValue((Property)PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.DOWN)), var1.getValue(DOWN));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.NORTH)), var1.getValue(NORTH))).setValue((Property)PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.SOUTH)), var1.getValue(SOUTH))).setValue((Property)PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.EAST)), var1.getValue(EAST))).setValue((Property)PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.WEST)), var1.getValue(WEST))).setValue((Property)PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.UP)), var1.getValue(UP))).setValue((Property)PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.DOWN)), var1.getValue(DOWN));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{UP, DOWN, NORTH, EAST, SOUTH, WEST});
   }
}
