package com.mojang.math;

import com.mojang.math.Quaternion;
import java.util.Arrays;
import net.minecraft.world.phys.Vec3;

public final class Vector3f {
   private final float[] values;

   public Vector3f(Vector3f vector3f) {
      this.values = Arrays.copyOf(vector3f.values, 3);
   }

   public Vector3f() {
      this.values = new float[3];
   }

   public Vector3f(float var1, float var2, float var3) {
      this.values = new float[]{var1, var2, var3};
   }

   public Vector3f(Vec3 vec3) {
      this.values = new float[]{(float)vec3.x, (float)vec3.y, (float)vec3.z};
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object != null && this.getClass() == object.getClass()) {
         Vector3f var2 = (Vector3f)object;
         return Arrays.equals(this.values, var2.values);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.values);
   }

   public float x() {
      return this.values[0];
   }

   public float y() {
      return this.values[1];
   }

   public float z() {
      return this.values[2];
   }

   public void mul(float f) {
      for(int var2 = 0; var2 < 3; ++var2) {
         this.values[var2] *= f;
      }

   }

   private static float clamp(float var0, float var1, float var2) {
      return var0 < var1?var1:(var0 > var2?var2:var0);
   }

   public void clamp(float var1, float var2) {
      this.values[0] = clamp(this.values[0], var1, var2);
      this.values[1] = clamp(this.values[1], var1, var2);
      this.values[2] = clamp(this.values[2], var1, var2);
   }

   public void set(float var1, float var2, float var3) {
      this.values[0] = var1;
      this.values[1] = var2;
      this.values[2] = var3;
   }

   public void add(float var1, float var2, float var3) {
      this.values[0] += var1;
      this.values[1] += var2;
      this.values[2] += var3;
   }

   public void sub(Vector3f vector3f) {
      for(int var2 = 0; var2 < 3; ++var2) {
         this.values[var2] -= vector3f.values[var2];
      }

   }

   public float dot(Vector3f vector3f) {
      float var2 = 0.0F;

      for(int var3 = 0; var3 < 3; ++var3) {
         var2 += this.values[var3] * vector3f.values[var3];
      }

      return var2;
   }

   public void normalize() {
      float var1 = 0.0F;

      for(int var2 = 0; var2 < 3; ++var2) {
         var1 += this.values[var2] * this.values[var2];
      }

      for(int var2 = 0; var2 < 3; ++var2) {
         this.values[var2] /= var1;
      }

   }

   public void cross(Vector3f vector3f) {
      float var2 = this.values[0];
      float var3 = this.values[1];
      float var4 = this.values[2];
      float var5 = vector3f.x();
      float var6 = vector3f.y();
      float var7 = vector3f.z();
      this.values[0] = var3 * var7 - var4 * var6;
      this.values[1] = var4 * var5 - var2 * var7;
      this.values[2] = var2 * var6 - var3 * var5;
   }

   public void transform(Quaternion quaternion) {
      Quaternion quaternion = new Quaternion(quaternion);
      quaternion.mul(new Quaternion(this.x(), this.y(), this.z(), 0.0F));
      Quaternion var3 = new Quaternion(quaternion);
      var3.conj();
      quaternion.mul(var3);
      this.set(quaternion.i(), quaternion.j(), quaternion.k());
   }
}
