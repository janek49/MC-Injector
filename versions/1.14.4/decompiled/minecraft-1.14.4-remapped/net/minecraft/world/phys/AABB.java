package net.minecraft.world.phys;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class AABB {
   public final double minX;
   public final double minY;
   public final double minZ;
   public final double maxX;
   public final double maxY;
   public final double maxZ;

   public AABB(double var1, double var3, double var5, double var7, double var9, double var11) {
      this.minX = Math.min(var1, var7);
      this.minY = Math.min(var3, var9);
      this.minZ = Math.min(var5, var11);
      this.maxX = Math.max(var1, var7);
      this.maxY = Math.max(var3, var9);
      this.maxZ = Math.max(var5, var11);
   }

   public AABB(BlockPos blockPos) {
      this((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), (double)(blockPos.getX() + 1), (double)(blockPos.getY() + 1), (double)(blockPos.getZ() + 1));
   }

   public AABB(BlockPos var1, BlockPos var2) {
      this((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), (double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
   }

   public AABB(Vec3 var1, Vec3 var2) {
      this(var1.x, var1.y, var1.z, var2.x, var2.y, var2.z);
   }

   public static AABB of(BoundingBox boundingBox) {
      return new AABB((double)boundingBox.x0, (double)boundingBox.y0, (double)boundingBox.z0, (double)(boundingBox.x1 + 1), (double)(boundingBox.y1 + 1), (double)(boundingBox.z1 + 1));
   }

   public double min(Direction.Axis direction$Axis) {
      return direction$Axis.choose(this.minX, this.minY, this.minZ);
   }

   public double max(Direction.Axis direction$Axis) {
      return direction$Axis.choose(this.maxX, this.maxY, this.maxZ);
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof AABB)) {
         return false;
      } else {
         AABB var2 = (AABB)object;
         return Double.compare(var2.minX, this.minX) != 0?false:(Double.compare(var2.minY, this.minY) != 0?false:(Double.compare(var2.minZ, this.minZ) != 0?false:(Double.compare(var2.maxX, this.maxX) != 0?false:(Double.compare(var2.maxY, this.maxY) != 0?false:Double.compare(var2.maxZ, this.maxZ) == 0))));
      }
   }

   public int hashCode() {
      long var1 = Double.doubleToLongBits(this.minX);
      int var3 = (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.minY);
      var3 = 31 * var3 + (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.minZ);
      var3 = 31 * var3 + (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.maxX);
      var3 = 31 * var3 + (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.maxY);
      var3 = 31 * var3 + (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.maxZ);
      var3 = 31 * var3 + (int)(var1 ^ var1 >>> 32);
      return var3;
   }

   public AABB contract(double var1, double var3, double var5) {
      double var7 = this.minX;
      double var9 = this.minY;
      double var11 = this.minZ;
      double var13 = this.maxX;
      double var15 = this.maxY;
      double var17 = this.maxZ;
      if(var1 < 0.0D) {
         var7 -= var1;
      } else if(var1 > 0.0D) {
         var13 -= var1;
      }

      if(var3 < 0.0D) {
         var9 -= var3;
      } else if(var3 > 0.0D) {
         var15 -= var3;
      }

      if(var5 < 0.0D) {
         var11 -= var5;
      } else if(var5 > 0.0D) {
         var17 -= var5;
      }

      return new AABB(var7, var9, var11, var13, var15, var17);
   }

   public AABB expandTowards(Vec3 vec3) {
      return this.expandTowards(vec3.x, vec3.y, vec3.z);
   }

   public AABB expandTowards(double var1, double var3, double var5) {
      double var7 = this.minX;
      double var9 = this.minY;
      double var11 = this.minZ;
      double var13 = this.maxX;
      double var15 = this.maxY;
      double var17 = this.maxZ;
      if(var1 < 0.0D) {
         var7 += var1;
      } else if(var1 > 0.0D) {
         var13 += var1;
      }

      if(var3 < 0.0D) {
         var9 += var3;
      } else if(var3 > 0.0D) {
         var15 += var3;
      }

      if(var5 < 0.0D) {
         var11 += var5;
      } else if(var5 > 0.0D) {
         var17 += var5;
      }

      return new AABB(var7, var9, var11, var13, var15, var17);
   }

   public AABB inflate(double var1, double var3, double var5) {
      double var7 = this.minX - var1;
      double var9 = this.minY - var3;
      double var11 = this.minZ - var5;
      double var13 = this.maxX + var1;
      double var15 = this.maxY + var3;
      double var17 = this.maxZ + var5;
      return new AABB(var7, var9, var11, var13, var15, var17);
   }

   public AABB inflate(double d) {
      return this.inflate(d, d, d);
   }

   public AABB intersect(AABB aABB) {
      double var2 = Math.max(this.minX, aABB.minX);
      double var4 = Math.max(this.minY, aABB.minY);
      double var6 = Math.max(this.minZ, aABB.minZ);
      double var8 = Math.min(this.maxX, aABB.maxX);
      double var10 = Math.min(this.maxY, aABB.maxY);
      double var12 = Math.min(this.maxZ, aABB.maxZ);
      return new AABB(var2, var4, var6, var8, var10, var12);
   }

   public AABB minmax(AABB aABB) {
      double var2 = Math.min(this.minX, aABB.minX);
      double var4 = Math.min(this.minY, aABB.minY);
      double var6 = Math.min(this.minZ, aABB.minZ);
      double var8 = Math.max(this.maxX, aABB.maxX);
      double var10 = Math.max(this.maxY, aABB.maxY);
      double var12 = Math.max(this.maxZ, aABB.maxZ);
      return new AABB(var2, var4, var6, var8, var10, var12);
   }

   public AABB move(double var1, double var3, double var5) {
      return new AABB(this.minX + var1, this.minY + var3, this.minZ + var5, this.maxX + var1, this.maxY + var3, this.maxZ + var5);
   }

   public AABB move(BlockPos blockPos) {
      return new AABB(this.minX + (double)blockPos.getX(), this.minY + (double)blockPos.getY(), this.minZ + (double)blockPos.getZ(), this.maxX + (double)blockPos.getX(), this.maxY + (double)blockPos.getY(), this.maxZ + (double)blockPos.getZ());
   }

   public AABB move(Vec3 vec3) {
      return this.move(vec3.x, vec3.y, vec3.z);
   }

   public boolean intersects(AABB aABB) {
      return this.intersects(aABB.minX, aABB.minY, aABB.minZ, aABB.maxX, aABB.maxY, aABB.maxZ);
   }

   public boolean intersects(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.minX < var7 && this.maxX > var1 && this.minY < var9 && this.maxY > var3 && this.minZ < var11 && this.maxZ > var5;
   }

   public boolean intersects(Vec3 var1, Vec3 var2) {
      return this.intersects(Math.min(var1.x, var2.x), Math.min(var1.y, var2.y), Math.min(var1.z, var2.z), Math.max(var1.x, var2.x), Math.max(var1.y, var2.y), Math.max(var1.z, var2.z));
   }

   public boolean contains(Vec3 vec3) {
      return this.contains(vec3.x, vec3.y, vec3.z);
   }

   public boolean contains(double var1, double var3, double var5) {
      return var1 >= this.minX && var1 < this.maxX && var3 >= this.minY && var3 < this.maxY && var5 >= this.minZ && var5 < this.maxZ;
   }

   public double getSize() {
      double var1 = this.getXsize();
      double var3 = this.getYsize();
      double var5 = this.getZsize();
      return (var1 + var3 + var5) / 3.0D;
   }

   public double getXsize() {
      return this.maxX - this.minX;
   }

   public double getYsize() {
      return this.maxY - this.minY;
   }

   public double getZsize() {
      return this.maxZ - this.minZ;
   }

   public AABB deflate(double d) {
      return this.inflate(-d);
   }

   public Optional clip(Vec3 var1, Vec3 var2) {
      double[] vars3 = new double[]{1.0D};
      double var4 = var2.x - var1.x;
      double var6 = var2.y - var1.y;
      double var8 = var2.z - var1.z;
      Direction var10 = getDirection(this, var1, vars3, (Direction)null, var4, var6, var8);
      if(var10 == null) {
         return Optional.empty();
      } else {
         double var11 = vars3[0];
         return Optional.of(var1.add(var11 * var4, var11 * var6, var11 * var8));
      }
   }

   @Nullable
   public static BlockHitResult clip(Iterable iterable, Vec3 var1, Vec3 var2, BlockPos blockPos) {
      double[] vars4 = new double[]{1.0D};
      Direction var5 = null;
      double var6 = var2.x - var1.x;
      double var8 = var2.y - var1.y;
      double var10 = var2.z - var1.z;

      for(AABB var13 : iterable) {
         var5 = getDirection(var13.move(blockPos), var1, vars4, var5, var6, var8, var10);
      }

      if(var5 == null) {
         return null;
      } else {
         double var12 = vars4[0];
         return new BlockHitResult(var1.add(var12 * var6, var12 * var8, var12 * var10), var5, blockPos, false);
      }
   }

   @Nullable
   private static Direction getDirection(AABB aABB, Vec3 vec3, double[] doubles, @Nullable Direction var3, double var4, double var6, double var8) {
      if(var4 > 1.0E-7D) {
         var3 = clipPoint(doubles, var3, var4, var6, var8, aABB.minX, aABB.minY, aABB.maxY, aABB.minZ, aABB.maxZ, Direction.WEST, vec3.x, vec3.y, vec3.z);
      } else if(var4 < -1.0E-7D) {
         var3 = clipPoint(doubles, var3, var4, var6, var8, aABB.maxX, aABB.minY, aABB.maxY, aABB.minZ, aABB.maxZ, Direction.EAST, vec3.x, vec3.y, vec3.z);
      }

      if(var6 > 1.0E-7D) {
         var3 = clipPoint(doubles, var3, var6, var8, var4, aABB.minY, aABB.minZ, aABB.maxZ, aABB.minX, aABB.maxX, Direction.DOWN, vec3.y, vec3.z, vec3.x);
      } else if(var6 < -1.0E-7D) {
         var3 = clipPoint(doubles, var3, var6, var8, var4, aABB.maxY, aABB.minZ, aABB.maxZ, aABB.minX, aABB.maxX, Direction.UP, vec3.y, vec3.z, vec3.x);
      }

      if(var8 > 1.0E-7D) {
         var3 = clipPoint(doubles, var3, var8, var4, var6, aABB.minZ, aABB.minX, aABB.maxX, aABB.minY, aABB.maxY, Direction.NORTH, vec3.z, vec3.x, vec3.y);
      } else if(var8 < -1.0E-7D) {
         var3 = clipPoint(doubles, var3, var8, var4, var6, aABB.maxZ, aABB.minX, aABB.maxX, aABB.minY, aABB.maxY, Direction.SOUTH, vec3.z, vec3.x, vec3.y);
      }

      return var3;
   }

   @Nullable
   private static Direction clipPoint(double[] doubles, @Nullable Direction var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, Direction var18, double var19, double var21, double var23) {
      double var25 = (var8 - var19) / var2;
      double var27 = var21 + var25 * var4;
      double var29 = var23 + var25 * var6;
      if(0.0D < var25 && var25 < doubles[0] && var10 - 1.0E-7D < var27 && var27 < var12 + 1.0E-7D && var14 - 1.0E-7D < var29 && var29 < var16 + 1.0E-7D) {
         doubles[0] = var25;
         return var18;
      } else {
         return var1;
      }
   }

   public String toString() {
      return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
   }

   public boolean hasNaN() {
      return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
   }

   public Vec3 getCenter() {
      return new Vec3(Mth.lerp(0.5D, this.minX, this.maxX), Mth.lerp(0.5D, this.minY, this.maxY), Mth.lerp(0.5D, this.minZ, this.maxZ));
   }
}
