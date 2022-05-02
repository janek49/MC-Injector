package net.minecraft.world.phys.shapes;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.List;
import java.util.function.IntPredicate;
import javax.annotation.Nullable;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.OffsetDoubleList;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.SliceShape;

public abstract class VoxelShape {
   protected final DiscreteVoxelShape shape;
   @Nullable
   private VoxelShape[] faces;

   VoxelShape(DiscreteVoxelShape shape) {
      this.shape = shape;
   }

   public double min(Direction.Axis direction$Axis) {
      int var2 = this.shape.firstFull(direction$Axis);
      return var2 >= this.shape.getSize(direction$Axis)?Double.POSITIVE_INFINITY:this.get(direction$Axis, var2);
   }

   public double max(Direction.Axis direction$Axis) {
      int var2 = this.shape.lastFull(direction$Axis);
      return var2 <= 0?Double.NEGATIVE_INFINITY:this.get(direction$Axis, var2);
   }

   public AABB bounds() {
      if(this.isEmpty()) {
         throw new UnsupportedOperationException("No bounds for empty shape.");
      } else {
         return new AABB(this.min(Direction.Axis.X), this.min(Direction.Axis.Y), this.min(Direction.Axis.Z), this.max(Direction.Axis.X), this.max(Direction.Axis.Y), this.max(Direction.Axis.Z));
      }
   }

   protected double get(Direction.Axis direction$Axis, int var2) {
      return this.getCoords(direction$Axis).getDouble(var2);
   }

   protected abstract DoubleList getCoords(Direction.Axis var1);

   public boolean isEmpty() {
      return this.shape.isEmpty();
   }

   public VoxelShape move(double var1, double var3, double var5) {
      return (VoxelShape)(this.isEmpty()?Shapes.empty():new ArrayVoxelShape(this.shape, new OffsetDoubleList(this.getCoords(Direction.Axis.X), var1), new OffsetDoubleList(this.getCoords(Direction.Axis.Y), var3), new OffsetDoubleList(this.getCoords(Direction.Axis.Z), var5)));
   }

   public VoxelShape optimize() {
      VoxelShape[] vars1 = new VoxelShape[]{Shapes.empty()};
      this.forAllBoxes((var1, var3, var5, var7, var9, var11) -> {
         vars1[0] = Shapes.joinUnoptimized(vars1[0], Shapes.box(var1, var3, var5, var7, var9, var11), BooleanOp.OR);
      });
      return vars1[0];
   }

   public void forAllEdges(Shapes.DoubleLineConsumer shapes$DoubleLineConsumer) {
      this.shape.forAllEdges((var2, var3, var4, var5, var6, var7) -> {
         shapes$DoubleLineConsumer.consume(this.get(Direction.Axis.X, var2), this.get(Direction.Axis.Y, var3), this.get(Direction.Axis.Z, var4), this.get(Direction.Axis.X, var5), this.get(Direction.Axis.Y, var6), this.get(Direction.Axis.Z, var7));
      }, true);
   }

   public void forAllBoxes(Shapes.DoubleLineConsumer shapes$DoubleLineConsumer) {
      DoubleList var2 = this.getCoords(Direction.Axis.X);
      DoubleList var3 = this.getCoords(Direction.Axis.Y);
      DoubleList var4 = this.getCoords(Direction.Axis.Z);
      this.shape.forAllBoxes((var4x, var5, var6, var7, var8, var9) -> {
         shapes$DoubleLineConsumer.consume(var2.getDouble(var4x), var3.getDouble(var5), var4.getDouble(var6), var2.getDouble(var7), var3.getDouble(var8), var4.getDouble(var9));
      }, true);
   }

   public List toAabbs() {
      List<AABB> list = Lists.newArrayList();
      this.forAllBoxes((var1, var3, var5, var7, var9, var11) -> {
         list.add(new AABB(var1, var3, var5, var7, var9, var11));
      });
      return list;
   }

   public double min(Direction.Axis direction$Axis, double var2, double var4) {
      Direction.Axis direction$Axis = AxisCycle.FORWARD.cycle(direction$Axis);
      Direction.Axis var7 = AxisCycle.BACKWARD.cycle(direction$Axis);
      int var8 = this.findIndex(direction$Axis, var2);
      int var9 = this.findIndex(var7, var4);
      int var10 = this.shape.firstFull(direction$Axis, var8, var9);
      return var10 >= this.shape.getSize(direction$Axis)?Double.POSITIVE_INFINITY:this.get(direction$Axis, var10);
   }

   public double max(Direction.Axis direction$Axis, double var2, double var4) {
      Direction.Axis direction$Axis = AxisCycle.FORWARD.cycle(direction$Axis);
      Direction.Axis var7 = AxisCycle.BACKWARD.cycle(direction$Axis);
      int var8 = this.findIndex(direction$Axis, var2);
      int var9 = this.findIndex(var7, var4);
      int var10 = this.shape.lastFull(direction$Axis, var8, var9);
      return var10 <= 0?Double.NEGATIVE_INFINITY:this.get(direction$Axis, var10);
   }

   protected int findIndex(Direction.Axis direction$Axis, double var2) {
      return Mth.binarySearch(0, this.shape.getSize(direction$Axis) + 1, (var4) -> {
         return var4 < 0?false:(var4 > this.shape.getSize(direction$Axis)?true:var2 < this.get(direction$Axis, var4));
      }) - 1;
   }

   protected boolean isFullWide(double var1, double var3, double var5) {
      return this.shape.isFullWide(this.findIndex(Direction.Axis.X, var1), this.findIndex(Direction.Axis.Y, var3), this.findIndex(Direction.Axis.Z, var5));
   }

   @Nullable
   public BlockHitResult clip(Vec3 var1, Vec3 var2, BlockPos blockPos) {
      if(this.isEmpty()) {
         return null;
      } else {
         Vec3 var4 = var2.subtract(var1);
         if(var4.lengthSqr() < 1.0E-7D) {
            return null;
         } else {
            Vec3 var5 = var1.add(var4.scale(0.001D));
            return this.isFullWide(var5.x - (double)blockPos.getX(), var5.y - (double)blockPos.getY(), var5.z - (double)blockPos.getZ())?new BlockHitResult(var5, Direction.getNearest(var4.x, var4.y, var4.z).getOpposite(), blockPos, true):AABB.clip(this.toAabbs(), var1, var2, blockPos);
         }
      }
   }

   public VoxelShape getFaceShape(Direction direction) {
      if(!this.isEmpty() && this != Shapes.block()) {
         if(this.faces != null) {
            VoxelShape voxelShape = this.faces[direction.ordinal()];
            if(voxelShape != null) {
               return voxelShape;
            }
         } else {
            this.faces = new VoxelShape[6];
         }

         VoxelShape voxelShape = this.calculateFace(direction);
         this.faces[direction.ordinal()] = voxelShape;
         return voxelShape;
      } else {
         return this;
      }
   }

   private VoxelShape calculateFace(Direction direction) {
      Direction.Axis var2 = direction.getAxis();
      Direction.AxisDirection var3 = direction.getAxisDirection();
      DoubleList var4 = this.getCoords(var2);
      if(var4.size() == 2 && DoubleMath.fuzzyEquals(var4.getDouble(0), 0.0D, 1.0E-7D) && DoubleMath.fuzzyEquals(var4.getDouble(1), 1.0D, 1.0E-7D)) {
         return this;
      } else {
         int var5 = this.findIndex(var2, var3 == Direction.AxisDirection.POSITIVE?0.9999999D:1.0E-7D);
         return new SliceShape(this, var2, var5);
      }
   }

   public double collide(Direction.Axis direction$Axis, AABB aABB, double var3) {
      return this.collideX(AxisCycle.between(direction$Axis, Direction.Axis.X), aABB, var3);
   }

   protected double collideX(AxisCycle axisCycle, AABB aABB, double var3) {
      if(this.isEmpty()) {
         return var3;
      } else if(Math.abs(var3) < 1.0E-7D) {
         return 0.0D;
      } else {
         AxisCycle axisCycle = axisCycle.inverse();
         Direction.Axis var6 = axisCycle.cycle(Direction.Axis.X);
         Direction.Axis var7 = axisCycle.cycle(Direction.Axis.Y);
         Direction.Axis var8 = axisCycle.cycle(Direction.Axis.Z);
         double var9 = aABB.max(var6);
         double var11 = aABB.min(var6);
         int var13 = this.findIndex(var6, var11 + 1.0E-7D);
         int var14 = this.findIndex(var6, var9 - 1.0E-7D);
         int var15 = Math.max(0, this.findIndex(var7, aABB.min(var7) + 1.0E-7D));
         int var16 = Math.min(this.shape.getSize(var7), this.findIndex(var7, aABB.max(var7) - 1.0E-7D) + 1);
         int var17 = Math.max(0, this.findIndex(var8, aABB.min(var8) + 1.0E-7D));
         int var18 = Math.min(this.shape.getSize(var8), this.findIndex(var8, aABB.max(var8) - 1.0E-7D) + 1);
         int var19 = this.shape.getSize(var6);
         if(var3 > 0.0D) {
            for(int var20 = var14 + 1; var20 < var19; ++var20) {
               for(int var21 = var15; var21 < var16; ++var21) {
                  for(int var22 = var17; var22 < var18; ++var22) {
                     if(this.shape.isFullWide(axisCycle, var20, var21, var22)) {
                        double var23 = this.get(var6, var20) - var9;
                        if(var23 >= -1.0E-7D) {
                           var3 = Math.min(var3, var23);
                        }

                        return var3;
                     }
                  }
               }
            }
         } else if(var3 < 0.0D) {
            for(int var20 = var13 - 1; var20 >= 0; --var20) {
               for(int var21 = var15; var21 < var16; ++var21) {
                  for(int var22 = var17; var22 < var18; ++var22) {
                     if(this.shape.isFullWide(axisCycle, var20, var21, var22)) {
                        double var23 = this.get(var6, var20 + 1) - var11;
                        if(var23 <= 1.0E-7D) {
                           var3 = Math.max(var3, var23);
                        }

                        return var3;
                     }
                  }
               }
            }
         }

         return var3;
      }
   }

   public String toString() {
      return this.isEmpty()?"EMPTY":"VoxelShape[" + this.bounds() + "]";
   }
}
