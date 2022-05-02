package com.mojang.math;

import com.mojang.math.Vector3f;
import java.util.Arrays;

public final class Quaternion {
   private final float[] values;

   public Quaternion() {
      this.values = new float[4];
      this.values[4] = 1.0F;
   }

   public Quaternion(float var1, float var2, float var3, float var4) {
      this.values = new float[4];
      this.values[0] = var1;
      this.values[1] = var2;
      this.values[2] = var3;
      this.values[3] = var4;
   }

   public Quaternion(Vector3f vector3f, float var2, boolean var3) {
      if(var3) {
         var2 *= 0.017453292F;
      }

      float var4 = sin(var2 / 2.0F);
      this.values = new float[4];
      this.values[0] = vector3f.x() * var4;
      this.values[1] = vector3f.y() * var4;
      this.values[2] = vector3f.z() * var4;
      this.values[3] = cos(var2 / 2.0F);
   }

   public Quaternion(float var1, float var2, float var3, boolean var4) {
      if(var4) {
         var1 *= 0.017453292F;
         var2 *= 0.017453292F;
         var3 *= 0.017453292F;
      }

      float var5 = sin(0.5F * var1);
      float var6 = cos(0.5F * var1);
      float var7 = sin(0.5F * var2);
      float var8 = cos(0.5F * var2);
      float var9 = sin(0.5F * var3);
      float var10 = cos(0.5F * var3);
      this.values = new float[4];
      this.values[0] = var5 * var8 * var10 + var6 * var7 * var9;
      this.values[1] = var6 * var7 * var10 - var5 * var8 * var9;
      this.values[2] = var5 * var7 * var10 + var6 * var8 * var9;
      this.values[3] = var6 * var8 * var10 - var5 * var7 * var9;
   }

   public Quaternion(Quaternion quaternion) {
      this.values = Arrays.copyOf(quaternion.values, 4);
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object != null && this.getClass() == object.getClass()) {
         Quaternion var2 = (Quaternion)object;
         return Arrays.equals(this.values, var2.values);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.values);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Quaternion[").append(this.r()).append(" + ");
      var1.append(this.i()).append("i + ");
      var1.append(this.j()).append("j + ");
      var1.append(this.k()).append("k]");
      return var1.toString();
   }

   public float i() {
      return this.values[0];
   }

   public float j() {
      return this.values[1];
   }

   public float k() {
      return this.values[2];
   }

   public float r() {
      return this.values[3];
   }

   public void mul(Quaternion quaternion) {
      float var2 = this.i();
      float var3 = this.j();
      float var4 = this.k();
      float var5 = this.r();
      float var6 = quaternion.i();
      float var7 = quaternion.j();
      float var8 = quaternion.k();
      float var9 = quaternion.r();
      this.values[0] = var5 * var6 + var2 * var9 + var3 * var8 - var4 * var7;
      this.values[1] = var5 * var7 - var2 * var8 + var3 * var9 + var4 * var6;
      this.values[2] = var5 * var8 + var2 * var7 - var3 * var6 + var4 * var9;
      this.values[3] = var5 * var9 - var2 * var6 - var3 * var7 - var4 * var8;
   }

   public void conj() {
      this.values[0] = -this.values[0];
      this.values[1] = -this.values[1];
      this.values[2] = -this.values[2];
   }

   private static float cos(float f) {
      return (float)Math.cos((double)f);
   }

   private static float sin(float f) {
      return (float)Math.sin((double)f);
   }
}
