package net.minecraft.core;

import com.google.common.base.MoreObjects;
import javax.annotation.concurrent.Immutable;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;

@Immutable
public class Vec3i implements Comparable {
   public static final Vec3i ZERO = new Vec3i(0, 0, 0);
   private final int x;
   private final int y;
   private final int z;

   public Vec3i(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vec3i(double var1, double var3, double var5) {
      this(Mth.floor(var1), Mth.floor(var3), Mth.floor(var5));
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof Vec3i)) {
         return false;
      } else {
         Vec3i var2 = (Vec3i)object;
         return this.getX() != var2.getX()?false:(this.getY() != var2.getY()?false:this.getZ() == var2.getZ());
      }
   }

   public int hashCode() {
      return (this.getY() + this.getZ() * 31) * 31 + this.getX();
   }

   public int compareTo(Vec3i vec3i) {
      return this.getY() == vec3i.getY()?(this.getZ() == vec3i.getZ()?this.getX() - vec3i.getX():this.getZ() - vec3i.getZ()):this.getY() - vec3i.getY();
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public Vec3i cross(Vec3i vec3i) {
      return new Vec3i(this.getY() * vec3i.getZ() - this.getZ() * vec3i.getY(), this.getZ() * vec3i.getX() - this.getX() * vec3i.getZ(), this.getX() * vec3i.getY() - this.getY() * vec3i.getX());
   }

   public boolean closerThan(Vec3i vec3i, double var2) {
      return this.distSqr((double)vec3i.x, (double)vec3i.y, (double)vec3i.z, false) < var2 * var2;
   }

   public boolean closerThan(Position position, double var2) {
      return this.distSqr(position.x(), position.y(), position.z(), true) < var2 * var2;
   }

   public double distSqr(Vec3i vec3i) {
      return this.distSqr((double)vec3i.getX(), (double)vec3i.getY(), (double)vec3i.getZ(), true);
   }

   public double distSqr(Position position, boolean var2) {
      return this.distSqr(position.x(), position.y(), position.z(), var2);
   }

   public double distSqr(double var1, double var3, double var5, boolean var7) {
      double var8 = var7?0.5D:0.0D;
      double var10 = (double)this.getX() + var8 - var1;
      double var12 = (double)this.getY() + var8 - var3;
      double var14 = (double)this.getZ() + var8 - var5;
      return var10 * var10 + var12 * var12 + var14 * var14;
   }

   public int distManhattan(Vec3i vec3i) {
      float var2 = (float)Math.abs(vec3i.getX() - this.x);
      float var3 = (float)Math.abs(vec3i.getY() - this.y);
      float var4 = (float)Math.abs(vec3i.getZ() - this.z);
      return (int)(var2 + var3 + var4);
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((Vec3i)var1);
   }
}
