package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.CubePointRange;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CubeVoxelShape extends VoxelShape {
   protected CubeVoxelShape(DiscreteVoxelShape discreteVoxelShape) {
      super(discreteVoxelShape);
   }

   protected DoubleList getCoords(Direction.Axis direction$Axis) {
      return new CubePointRange(this.shape.getSize(direction$Axis));
   }

   protected int findIndex(Direction.Axis direction$Axis, double var2) {
      int var4 = this.shape.getSize(direction$Axis);
      return Mth.clamp(Mth.floor(var2 * (double)var4), -1, var4);
   }
}
