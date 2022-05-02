package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CubePointRange;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.SubShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SliceShape extends VoxelShape {
   private final VoxelShape delegate;
   private final Direction.Axis axis;
   private static final DoubleList SLICE_COORDS = new CubePointRange(1);

   public SliceShape(VoxelShape delegate, Direction.Axis axis, int var3) {
      super(makeSlice(delegate.shape, axis, var3));
      this.delegate = delegate;
      this.axis = axis;
   }

   private static DiscreteVoxelShape makeSlice(DiscreteVoxelShape var0, Direction.Axis direction$Axis, int var2) {
      return new SubShape(var0, direction$Axis.choose(var2, 0, 0), direction$Axis.choose(0, var2, 0), direction$Axis.choose(0, 0, var2), direction$Axis.choose(var2 + 1, var0.xSize, var0.xSize), direction$Axis.choose(var0.ySize, var2 + 1, var0.ySize), direction$Axis.choose(var0.zSize, var0.zSize, var2 + 1));
   }

   protected DoubleList getCoords(Direction.Axis direction$Axis) {
      return direction$Axis == this.axis?SLICE_COORDS:this.delegate.getCoords(direction$Axis);
   }
}
