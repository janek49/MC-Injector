package net.minecraft.world.phys.shapes;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public final class SubShape extends DiscreteVoxelShape {
   private final DiscreteVoxelShape parent;
   private final int startX;
   private final int startY;
   private final int startZ;
   private final int endX;
   private final int endY;
   private final int endZ;

   protected SubShape(DiscreteVoxelShape parent, int startX, int startY, int startZ, int endX, int endY, int endZ) {
      super(endX - startX, endY - startY, endZ - startZ);
      this.parent = parent;
      this.startX = startX;
      this.startY = startY;
      this.startZ = startZ;
      this.endX = endX;
      this.endY = endY;
      this.endZ = endZ;
   }

   public boolean isFull(int var1, int var2, int var3) {
      return this.parent.isFull(this.startX + var1, this.startY + var2, this.startZ + var3);
   }

   public void setFull(int var1, int var2, int var3, boolean var4, boolean var5) {
      this.parent.setFull(this.startX + var1, this.startY + var2, this.startZ + var3, var4, var5);
   }

   public int firstFull(Direction.Axis direction$Axis) {
      return Math.max(0, this.parent.firstFull(direction$Axis) - direction$Axis.choose(this.startX, this.startY, this.startZ));
   }

   public int lastFull(Direction.Axis direction$Axis) {
      return Math.min(direction$Axis.choose(this.endX, this.endY, this.endZ), this.parent.lastFull(direction$Axis) - direction$Axis.choose(this.startX, this.startY, this.startZ));
   }
}
