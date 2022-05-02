package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class CubePointRange extends AbstractDoubleList {
   private final int parts;

   CubePointRange(int parts) {
      this.parts = parts;
   }

   public double getDouble(int i) {
      return (double)i / (double)this.parts;
   }

   public int size() {
      return this.parts + 1;
   }
}
