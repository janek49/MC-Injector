package net.minecraft.world.phys;

import java.util.EnumSet;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class Vec3 implements Position {
   public static final Vec3 ZERO = new Vec3(0.0D, 0.0D, 0.0D);
   public final double x;
   public final double y;
   public final double z;

   public Vec3(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vec3(Vec3i vec3i) {
      this((double)vec3i.getX(), (double)vec3i.getY(), (double)vec3i.getZ());
   }

   public Vec3 vectorTo(Vec3 vec3) {
      return new Vec3(vec3.x - this.x, vec3.y - this.y, vec3.z - this.z);
   }

   public Vec3 normalize() {
      double var1 = (double)Mth.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      return var1 < 1.0E-4D?ZERO:new Vec3(this.x / var1, this.y / var1, this.z / var1);
   }

   public double dot(Vec3 vec3) {
      return this.x * vec3.x + this.y * vec3.y + this.z * vec3.z;
   }

   public Vec3 cross(Vec3 vec3) {
      return new Vec3(this.y * vec3.z - this.z * vec3.y, this.z * vec3.x - this.x * vec3.z, this.x * vec3.y - this.y * vec3.x);
   }

   public Vec3 subtract(Vec3 vec3) {
      return this.subtract(vec3.x, vec3.y, vec3.z);
   }

   public Vec3 subtract(double var1, double var3, double var5) {
      return this.add(-var1, -var3, -var5);
   }

   public Vec3 add(Vec3 vec3) {
      return this.add(vec3.x, vec3.y, vec3.z);
   }

   public Vec3 add(double var1, double var3, double var5) {
      return new Vec3(this.x + var1, this.y + var3, this.z + var5);
   }

   public double distanceTo(Vec3 vec3) {
      double var2 = vec3.x - this.x;
      double var4 = vec3.y - this.y;
      double var6 = vec3.z - this.z;
      return (double)Mth.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
   }

   public double distanceToSqr(Vec3 vec3) {
      double var2 = vec3.x - this.x;
      double var4 = vec3.y - this.y;
      double var6 = vec3.z - this.z;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public double distanceToSqr(double var1, double var3, double var5) {
      double var7 = var1 - this.x;
      double var9 = var3 - this.y;
      double var11 = var5 - this.z;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public Vec3 scale(double d) {
      return this.multiply(d, d, d);
   }

   public Vec3 reverse() {
      return this.scale(-1.0D);
   }

   public Vec3 multiply(Vec3 vec3) {
      return this.multiply(vec3.x, vec3.y, vec3.z);
   }

   public Vec3 multiply(double var1, double var3, double var5) {
      return new Vec3(this.x * var1, this.y * var3, this.z * var5);
   }

   public double length() {
      return (double)Mth.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
   }

   public double lengthSqr() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof Vec3)) {
         return false;
      } else {
         Vec3 var2 = (Vec3)object;
         return Double.compare(var2.x, this.x) != 0?false:(Double.compare(var2.y, this.y) != 0?false:Double.compare(var2.z, this.z) == 0);
      }
   }

   public int hashCode() {
      long var2 = Double.doubleToLongBits(this.x);
      int var1 = (int)(var2 ^ var2 >>> 32);
      var2 = Double.doubleToLongBits(this.y);
      var1 = 31 * var1 + (int)(var2 ^ var2 >>> 32);
      var2 = Double.doubleToLongBits(this.z);
      var1 = 31 * var1 + (int)(var2 ^ var2 >>> 32);
      return var1;
   }

   public String toString() {
      return "(" + this.x + ", " + this.y + ", " + this.z + ")";
   }

   public Vec3 xRot(float f) {
      float var2 = Mth.cos(f);
      float var3 = Mth.sin(f);
      double var4 = this.x;
      double var6 = this.y * (double)var2 + this.z * (double)var3;
      double var8 = this.z * (double)var2 - this.y * (double)var3;
      return new Vec3(var4, var6, var8);
   }

   public Vec3 yRot(float f) {
      float var2 = Mth.cos(f);
      float var3 = Mth.sin(f);
      double var4 = this.x * (double)var2 + this.z * (double)var3;
      double var6 = this.y;
      double var8 = this.z * (double)var2 - this.x * (double)var3;
      return new Vec3(var4, var6, var8);
   }

   public static Vec3 directionFromRotation(Vec2 vec2) {
      return directionFromRotation(vec2.x, vec2.y);
   }

   public static Vec3 directionFromRotation(float var0, float var1) {
      float var2 = Mth.cos(-var1 * 0.017453292F - 3.1415927F);
      float var3 = Mth.sin(-var1 * 0.017453292F - 3.1415927F);
      float var4 = -Mth.cos(-var0 * 0.017453292F);
      float var5 = Mth.sin(-var0 * 0.017453292F);
      return new Vec3((double)(var3 * var4), (double)var5, (double)(var2 * var4));
   }

   public Vec3 align(EnumSet enumSet) {
      double var2 = enumSet.contains(Direction.Axis.X)?(double)Mth.floor(this.x):this.x;
      double var4 = enumSet.contains(Direction.Axis.Y)?(double)Mth.floor(this.y):this.y;
      double var6 = enumSet.contains(Direction.Axis.Z)?(double)Mth.floor(this.z):this.z;
      return new Vec3(var2, var4, var6);
   }

   public double get(Direction.Axis direction$Axis) {
      return direction$Axis.choose(this.x, this.y, this.z);
   }

   public final double x() {
      return this.x;
   }

   public final double y() {
      return this.y;
   }

   public final double z() {
      return this.z;
   }
}
