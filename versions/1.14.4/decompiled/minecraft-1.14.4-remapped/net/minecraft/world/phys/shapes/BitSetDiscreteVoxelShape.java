package net.minecraft.world.phys.shapes;

import java.util.BitSet;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.IndexMerger;

public final class BitSetDiscreteVoxelShape extends DiscreteVoxelShape {
   private final BitSet storage;
   private int xMin;
   private int yMin;
   private int zMin;
   private int xMax;
   private int yMax;
   private int zMax;

   public BitSetDiscreteVoxelShape(int var1, int var2, int var3) {
      this(var1, var2, var3, var1, var2, var3, 0, 0, 0);
   }

   public BitSetDiscreteVoxelShape(int var1, int var2, int var3, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
      super(var1, var2, var3);
      this.storage = new BitSet(var1 * var2 * var3);
      this.xMin = xMin;
      this.yMin = yMin;
      this.zMin = zMin;
      this.xMax = xMax;
      this.yMax = yMax;
      this.zMax = zMax;
   }

   public BitSetDiscreteVoxelShape(DiscreteVoxelShape discreteVoxelShape) {
      super(discreteVoxelShape.xSize, discreteVoxelShape.ySize, discreteVoxelShape.zSize);
      if(discreteVoxelShape instanceof BitSetDiscreteVoxelShape) {
         this.storage = (BitSet)((BitSetDiscreteVoxelShape)discreteVoxelShape).storage.clone();
      } else {
         this.storage = new BitSet(this.xSize * this.ySize * this.zSize);

         for(int var2 = 0; var2 < this.xSize; ++var2) {
            for(int var3 = 0; var3 < this.ySize; ++var3) {
               for(int var4 = 0; var4 < this.zSize; ++var4) {
                  if(discreteVoxelShape.isFull(var2, var3, var4)) {
                     this.storage.set(this.getIndex(var2, var3, var4));
                  }
               }
            }
         }
      }

      this.xMin = discreteVoxelShape.firstFull(Direction.Axis.X);
      this.yMin = discreteVoxelShape.firstFull(Direction.Axis.Y);
      this.zMin = discreteVoxelShape.firstFull(Direction.Axis.Z);
      this.xMax = discreteVoxelShape.lastFull(Direction.Axis.X);
      this.yMax = discreteVoxelShape.lastFull(Direction.Axis.Y);
      this.zMax = discreteVoxelShape.lastFull(Direction.Axis.Z);
   }

   protected int getIndex(int var1, int var2, int var3) {
      return (var1 * this.ySize + var2) * this.zSize + var3;
   }

   public boolean isFull(int var1, int var2, int var3) {
      return this.storage.get(this.getIndex(var1, var2, var3));
   }

   public void setFull(int var1, int var2, int var3, boolean var4, boolean var5) {
      this.storage.set(this.getIndex(var1, var2, var3), var5);
      if(var4 && var5) {
         this.xMin = Math.min(this.xMin, var1);
         this.yMin = Math.min(this.yMin, var2);
         this.zMin = Math.min(this.zMin, var3);
         this.xMax = Math.max(this.xMax, var1 + 1);
         this.yMax = Math.max(this.yMax, var2 + 1);
         this.zMax = Math.max(this.zMax, var3 + 1);
      }

   }

   public boolean isEmpty() {
      return this.storage.isEmpty();
   }

   public int firstFull(Direction.Axis direction$Axis) {
      return direction$Axis.choose(this.xMin, this.yMin, this.zMin);
   }

   public int lastFull(Direction.Axis direction$Axis) {
      return direction$Axis.choose(this.xMax, this.yMax, this.zMax);
   }

   protected boolean isZStripFull(int var1, int var2, int var3, int var4) {
      return var3 >= 0 && var4 >= 0 && var1 >= 0?(var3 < this.xSize && var4 < this.ySize && var2 <= this.zSize?this.storage.nextClearBit(this.getIndex(var3, var4, var1)) >= this.getIndex(var3, var4, var2):false):false;
   }

   protected void setZStrip(int var1, int var2, int var3, int var4, boolean var5) {
      this.storage.set(this.getIndex(var3, var4, var1), this.getIndex(var3, var4, var2), var5);
   }

   static BitSetDiscreteVoxelShape join(DiscreteVoxelShape var0, DiscreteVoxelShape var1, IndexMerger var2, IndexMerger var3, IndexMerger var4, BooleanOp booleanOp) {
      BitSetDiscreteVoxelShape bitSetDiscreteVoxelShape = new BitSetDiscreteVoxelShape(var2.getList().size() - 1, var3.getList().size() - 1, var4.getList().size() - 1);
      int[] vars7 = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
      var2.forMergedIndexes((var7, var8, var9) -> {
         boolean[] vars10 = new boolean[]{false};
         boolean var11 = var3.forMergedIndexes((var10, var11, var12) -> {
            boolean[] booleans = new boolean[]{false};
            boolean var14 = var4.forMergedIndexes((var12x, var13, var14) -> {
               boolean var15 = booleanOp.apply(var0.isFullWide(var7, var10, var12x), var1.isFullWide(var8, var11, var13));
               if(var15) {
                  bitSetDiscreteVoxelShape.storage.set(bitSetDiscreteVoxelShape.getIndex(var9, var12, var14));
                  vars7[2] = Math.min(vars7[2], var14);
                  vars7[5] = Math.max(vars7[5], var14);
                  booleans[0] = true;
               }

               return true;
            });
            if(booleans[0]) {
               vars7[1] = Math.min(vars7[1], var12);
               vars7[4] = Math.max(vars7[4], var12);
               vars10[0] = true;
            }

            return var14;
         });
         if(vars10[0]) {
            vars7[0] = Math.min(vars7[0], var9);
            vars7[3] = Math.max(vars7[3], var9);
         }

         return var11;
      });
      bitSetDiscreteVoxelShape.xMin = vars7[0];
      bitSetDiscreteVoxelShape.yMin = vars7[1];
      bitSetDiscreteVoxelShape.zMin = vars7[2];
      bitSetDiscreteVoxelShape.xMax = vars7[3] + 1;
      bitSetDiscreteVoxelShape.yMax = vars7[4] + 1;
      bitSetDiscreteVoxelShape.zMax = vars7[5] + 1;
      return bitSetDiscreteVoxelShape;
   }
}
