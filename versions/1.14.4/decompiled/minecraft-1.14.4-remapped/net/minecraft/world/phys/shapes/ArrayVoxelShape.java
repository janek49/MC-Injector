package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ArrayVoxelShape extends VoxelShape {
   private final DoubleList xs;
   private final DoubleList ys;
   private final DoubleList zs;

   protected ArrayVoxelShape(DiscreteVoxelShape discreteVoxelShape, double[] vars2, double[] vars3, double[] vars4) {
      this(discreteVoxelShape, (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(vars2, discreteVoxelShape.getXSize() + 1)), (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(vars3, discreteVoxelShape.getYSize() + 1)), (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(vars4, discreteVoxelShape.getZSize() + 1)));
   }

   ArrayVoxelShape(DiscreteVoxelShape discreteVoxelShape, DoubleList xs, DoubleList ys, DoubleList zs) {
      super(discreteVoxelShape);
      int var5 = discreteVoxelShape.getXSize() + 1;
      int var6 = discreteVoxelShape.getYSize() + 1;
      int var7 = discreteVoxelShape.getZSize() + 1;
      if(var5 == xs.size() && var6 == ys.size() && var7 == zs.size()) {
         this.xs = xs;
         this.ys = ys;
         this.zs = zs;
      } else {
         throw new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape.");
      }
   }

   protected DoubleList getCoords(Direction.Axis direction$Axis) {
      switch(direction$Axis) {
      case X:
         return this.xs;
      case Y:
         return this.ys;
      case Z:
         return this.zs;
      default:
         throw new IllegalArgumentException();
      }
   }
}
