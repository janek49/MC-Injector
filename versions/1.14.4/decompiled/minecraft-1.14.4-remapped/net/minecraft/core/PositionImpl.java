package net.minecraft.core;

import net.minecraft.core.Position;

public class PositionImpl implements Position {
   protected final double x;
   protected final double y;
   protected final double z;

   public PositionImpl(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public double x() {
      return this.x;
   }

   public double y() {
      return this.y;
   }

   public double z() {
      return this.z;
   }
}
