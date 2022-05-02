package net.minecraft.world.phys.shapes;

import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;

public abstract class DiscreteVoxelShape {
   private static final Direction.Axis[] AXIS_VALUES = Direction.Axis.values();
   protected final int xSize;
   protected final int ySize;
   protected final int zSize;

   protected DiscreteVoxelShape(int xSize, int ySize, int zSize) {
      this.xSize = xSize;
      this.ySize = ySize;
      this.zSize = zSize;
   }

   public boolean isFullWide(AxisCycle axisCycle, int var2, int var3, int var4) {
      return this.isFullWide(axisCycle.cycle(var2, var3, var4, Direction.Axis.X), axisCycle.cycle(var2, var3, var4, Direction.Axis.Y), axisCycle.cycle(var2, var3, var4, Direction.Axis.Z));
   }

   public boolean isFullWide(int var1, int var2, int var3) {
      return var1 >= 0 && var2 >= 0 && var3 >= 0?(var1 < this.xSize && var2 < this.ySize && var3 < this.zSize?this.isFull(var1, var2, var3):false):false;
   }

   public boolean isFull(AxisCycle axisCycle, int var2, int var3, int var4) {
      return this.isFull(axisCycle.cycle(var2, var3, var4, Direction.Axis.X), axisCycle.cycle(var2, var3, var4, Direction.Axis.Y), axisCycle.cycle(var2, var3, var4, Direction.Axis.Z));
   }

   public abstract boolean isFull(int var1, int var2, int var3);

   public abstract void setFull(int var1, int var2, int var3, boolean var4, boolean var5);

   public boolean isEmpty() {
      for(Direction.Axis var4 : AXIS_VALUES) {
         if(this.firstFull(var4) >= this.lastFull(var4)) {
            return true;
         }
      }

      return false;
   }

   public abstract int firstFull(Direction.Axis var1);

   public abstract int lastFull(Direction.Axis var1);

   public int firstFull(Direction.Axis direction$Axis, int var2, int var3) {
      int var4 = this.getSize(direction$Axis);
      if(var2 >= 0 && var3 >= 0) {
         Direction.Axis var5 = AxisCycle.FORWARD.cycle(direction$Axis);
         Direction.Axis var6 = AxisCycle.BACKWARD.cycle(direction$Axis);
         if(var2 < this.getSize(var5) && var3 < this.getSize(var6)) {
            AxisCycle var7 = AxisCycle.between(Direction.Axis.X, direction$Axis);

            for(int var8 = 0; var8 < var4; ++var8) {
               if(this.isFull(var7, var8, var2, var3)) {
                  return var8;
               }
            }

            return var4;
         } else {
            return var4;
         }
      } else {
         return var4;
      }
   }

   public int lastFull(Direction.Axis direction$Axis, int var2, int var3) {
      if(var2 >= 0 && var3 >= 0) {
         Direction.Axis direction$Axis = AxisCycle.FORWARD.cycle(direction$Axis);
         Direction.Axis var5 = AxisCycle.BACKWARD.cycle(direction$Axis);
         if(var2 < this.getSize(direction$Axis) && var3 < this.getSize(var5)) {
            int var6 = this.getSize(direction$Axis);
            AxisCycle var7 = AxisCycle.between(Direction.Axis.X, direction$Axis);

            for(int var8 = var6 - 1; var8 >= 0; --var8) {
               if(this.isFull(var7, var8, var2, var3)) {
                  return var8 + 1;
               }
            }

            return 0;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public int getSize(Direction.Axis direction$Axis) {
      return direction$Axis.choose(this.xSize, this.ySize, this.zSize);
   }

   public int getXSize() {
      return this.getSize(Direction.Axis.X);
   }

   public int getYSize() {
      return this.getSize(Direction.Axis.Y);
   }

   public int getZSize() {
      return this.getSize(Direction.Axis.Z);
   }

   public void forAllEdges(DiscreteVoxelShape.IntLineConsumer discreteVoxelShape$IntLineConsumer, boolean var2) {
      this.forAllAxisEdges(discreteVoxelShape$IntLineConsumer, AxisCycle.NONE, var2);
      this.forAllAxisEdges(discreteVoxelShape$IntLineConsumer, AxisCycle.FORWARD, var2);
      this.forAllAxisEdges(discreteVoxelShape$IntLineConsumer, AxisCycle.BACKWARD, var2);
   }

   private void forAllAxisEdges(DiscreteVoxelShape.IntLineConsumer discreteVoxelShape$IntLineConsumer, AxisCycle axisCycle, boolean var3) {
      AxisCycle var5 = axisCycle.inverse();
      int var6 = this.getSize(var5.cycle(Direction.Axis.X));
      int var7 = this.getSize(var5.cycle(Direction.Axis.Y));
      int var8 = this.getSize(var5.cycle(Direction.Axis.Z));

      for(int var9 = 0; var9 <= var6; ++var9) {
         for(int var10 = 0; var10 <= var7; ++var10) {
            int var4 = -1;

            for(int var11 = 0; var11 <= var8; ++var11) {
               int var12 = 0;
               int var13 = 0;

               for(int var14 = 0; var14 <= 1; ++var14) {
                  for(int var15 = 0; var15 <= 1; ++var15) {
                     if(this.isFullWide(var5, var9 + var14 - 1, var10 + var15 - 1, var11)) {
                        ++var12;
                        var13 ^= var14 ^ var15;
                     }
                  }
               }

               if(var12 == 1 || var12 == 3 || var12 == 2 && (var13 & 1) == 0) {
                  if(var3) {
                     if(var4 == -1) {
                        var4 = var11;
                     }
                  } else {
                     discreteVoxelShape$IntLineConsumer.consume(var5.cycle(var9, var10, var11, Direction.Axis.X), var5.cycle(var9, var10, var11, Direction.Axis.Y), var5.cycle(var9, var10, var11, Direction.Axis.Z), var5.cycle(var9, var10, var11 + 1, Direction.Axis.X), var5.cycle(var9, var10, var11 + 1, Direction.Axis.Y), var5.cycle(var9, var10, var11 + 1, Direction.Axis.Z));
                  }
               } else if(var4 != -1) {
                  discreteVoxelShape$IntLineConsumer.consume(var5.cycle(var9, var10, var4, Direction.Axis.X), var5.cycle(var9, var10, var4, Direction.Axis.Y), var5.cycle(var9, var10, var4, Direction.Axis.Z), var5.cycle(var9, var10, var11, Direction.Axis.X), var5.cycle(var9, var10, var11, Direction.Axis.Y), var5.cycle(var9, var10, var11, Direction.Axis.Z));
                  var4 = -1;
               }
            }
         }
      }

   }

   protected boolean isZStripFull(int var1, int var2, int var3, int var4) {
      for(int var5 = var1; var5 < var2; ++var5) {
         if(!this.isFullWide(var3, var4, var5)) {
            return false;
         }
      }

      return true;
   }

   protected void setZStrip(int var1, int var2, int var3, int var4, boolean var5) {
      for(int var6 = var1; var6 < var2; ++var6) {
         this.setFull(var3, var4, var6, false, var5);
      }

   }

   protected boolean isXZRectangleFull(int var1, int var2, int var3, int var4, int var5) {
      for(int var6 = var1; var6 < var2; ++var6) {
         if(!this.isZStripFull(var3, var4, var6, var5)) {
            return false;
         }
      }

      return true;
   }

   public void forAllBoxes(DiscreteVoxelShape.IntLineConsumer discreteVoxelShape$IntLineConsumer, boolean var2) {
      DiscreteVoxelShape var3 = new BitSetDiscreteVoxelShape(this);

      for(int var4 = 0; var4 <= this.xSize; ++var4) {
         for(int var5 = 0; var5 <= this.ySize; ++var5) {
            int var6 = -1;

            for(int var7 = 0; var7 <= this.zSize; ++var7) {
               if(var3.isFullWide(var4, var5, var7)) {
                  if(var2) {
                     if(var6 == -1) {
                        var6 = var7;
                     }
                  } else {
                     discreteVoxelShape$IntLineConsumer.consume(var4, var5, var7, var4 + 1, var5 + 1, var7 + 1);
                  }
               } else if(var6 != -1) {
                  int var8 = var4;
                  int var9 = var4;
                  int var10 = var5;
                  int var11 = var5;
                  var3.setZStrip(var6, var7, var4, var5, false);

                  while(((DiscreteVoxelShape)var3).isZStripFull(var6, var7, var8 - 1, var10)) {
                     var3.setZStrip(var6, var7, var8 - 1, var10, false);
                     --var8;
                  }

                  while(((DiscreteVoxelShape)var3).isZStripFull(var6, var7, var9 + 1, var10)) {
                     var3.setZStrip(var6, var7, var9 + 1, var10, false);
                     ++var9;
                  }

                  while(((DiscreteVoxelShape)var3).isXZRectangleFull(var8, var9 + 1, var6, var7, var10 - 1)) {
                     for(int var12 = var8; var12 <= var9; ++var12) {
                        var3.setZStrip(var6, var7, var12, var10 - 1, false);
                     }

                     --var10;
                  }

                  while(((DiscreteVoxelShape)var3).isXZRectangleFull(var8, var9 + 1, var6, var7, var11 + 1)) {
                     for(int var12 = var8; var12 <= var9; ++var12) {
                        var3.setZStrip(var6, var7, var12, var11 + 1, false);
                     }

                     ++var11;
                  }

                  discreteVoxelShape$IntLineConsumer.consume(var8, var10, var6, var9 + 1, var11 + 1, var7);
                  var6 = -1;
               }
            }
         }
      }

   }

   public void forAllFaces(DiscreteVoxelShape.IntFaceConsumer discreteVoxelShape$IntFaceConsumer) {
      this.forAllAxisFaces(discreteVoxelShape$IntFaceConsumer, AxisCycle.NONE);
      this.forAllAxisFaces(discreteVoxelShape$IntFaceConsumer, AxisCycle.FORWARD);
      this.forAllAxisFaces(discreteVoxelShape$IntFaceConsumer, AxisCycle.BACKWARD);
   }

   private void forAllAxisFaces(DiscreteVoxelShape.IntFaceConsumer discreteVoxelShape$IntFaceConsumer, AxisCycle axisCycle) {
      AxisCycle axisCycle = axisCycle.inverse();
      Direction.Axis var4 = axisCycle.cycle(Direction.Axis.Z);
      int var5 = this.getSize(axisCycle.cycle(Direction.Axis.X));
      int var6 = this.getSize(axisCycle.cycle(Direction.Axis.Y));
      int var7 = this.getSize(var4);
      Direction var8 = Direction.fromAxisAndDirection(var4, Direction.AxisDirection.NEGATIVE);
      Direction var9 = Direction.fromAxisAndDirection(var4, Direction.AxisDirection.POSITIVE);

      for(int var10 = 0; var10 < var5; ++var10) {
         for(int var11 = 0; var11 < var6; ++var11) {
            boolean var12 = false;

            for(int var13 = 0; var13 <= var7; ++var13) {
               boolean var14 = var13 != var7 && this.isFull(axisCycle, var10, var11, var13);
               if(!var12 && var14) {
                  discreteVoxelShape$IntFaceConsumer.consume(var8, axisCycle.cycle(var10, var11, var13, Direction.Axis.X), axisCycle.cycle(var10, var11, var13, Direction.Axis.Y), axisCycle.cycle(var10, var11, var13, Direction.Axis.Z));
               }

               if(var12 && !var14) {
                  discreteVoxelShape$IntFaceConsumer.consume(var9, axisCycle.cycle(var10, var11, var13 - 1, Direction.Axis.X), axisCycle.cycle(var10, var11, var13 - 1, Direction.Axis.Y), axisCycle.cycle(var10, var11, var13 - 1, Direction.Axis.Z));
               }

               var12 = var14;
            }
         }
      }

   }

   public interface IntFaceConsumer {
      void consume(Direction var1, int var2, int var3, int var4);
   }

   public interface IntLineConsumer {
      void consume(int var1, int var2, int var3, int var4, int var5, int var6);
   }
}
