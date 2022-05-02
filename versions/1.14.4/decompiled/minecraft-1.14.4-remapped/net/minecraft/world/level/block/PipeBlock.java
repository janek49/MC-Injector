package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PipeBlock extends Block {
   private static final Direction[] DIRECTIONS = Direction.values();
   public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
   public static final BooleanProperty EAST = BlockStateProperties.EAST;
   public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
   public static final BooleanProperty WEST = BlockStateProperties.WEST;
   public static final BooleanProperty UP = BlockStateProperties.UP;
   public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
   public static final Map PROPERTY_BY_DIRECTION = (Map)Util.make(Maps.newEnumMap(Direction.class), (enumMap) -> {
      enumMap.put(Direction.NORTH, NORTH);
      enumMap.put(Direction.EAST, EAST);
      enumMap.put(Direction.SOUTH, SOUTH);
      enumMap.put(Direction.WEST, WEST);
      enumMap.put(Direction.UP, UP);
      enumMap.put(Direction.DOWN, DOWN);
   });
   protected final VoxelShape[] shapeByIndex;

   protected PipeBlock(float var1, Block.Properties block$Properties) {
      super(block$Properties);
      this.shapeByIndex = this.makeShapes(var1);
   }

   private VoxelShape[] makeShapes(float f) {
      float var2 = 0.5F - f;
      float var3 = 0.5F + f;
      VoxelShape var4 = Block.box((double)(var2 * 16.0F), (double)(var2 * 16.0F), (double)(var2 * 16.0F), (double)(var3 * 16.0F), (double)(var3 * 16.0F), (double)(var3 * 16.0F));
      VoxelShape[] vars5 = new VoxelShape[DIRECTIONS.length];

      for(int var6 = 0; var6 < DIRECTIONS.length; ++var6) {
         Direction var7 = DIRECTIONS[var6];
         vars5[var6] = Shapes.box(0.5D + Math.min((double)(-f), (double)var7.getStepX() * 0.5D), 0.5D + Math.min((double)(-f), (double)var7.getStepY() * 0.5D), 0.5D + Math.min((double)(-f), (double)var7.getStepZ() * 0.5D), 0.5D + Math.max((double)f, (double)var7.getStepX() * 0.5D), 0.5D + Math.max((double)f, (double)var7.getStepY() * 0.5D), 0.5D + Math.max((double)f, (double)var7.getStepZ() * 0.5D));
      }

      VoxelShape[] vars6 = new VoxelShape[64];

      for(int var7 = 0; var7 < 64; ++var7) {
         VoxelShape var8 = var4;

         for(int var9 = 0; var9 < DIRECTIONS.length; ++var9) {
            if((var7 & 1 << var9) != 0) {
               var8 = Shapes.or(var8, vars5[var9]);
            }
         }

         vars6[var7] = var8;
      }

      return vars6;
   }

   public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return false;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return this.shapeByIndex[this.getAABBIndex(blockState)];
   }

   protected int getAABBIndex(BlockState blockState) {
      int var2 = 0;

      for(int var3 = 0; var3 < DIRECTIONS.length; ++var3) {
         if(((Boolean)blockState.getValue((Property)PROPERTY_BY_DIRECTION.get(DIRECTIONS[var3]))).booleanValue()) {
            var2 |= 1 << var3;
         }
      }

      return var2;
   }
}
