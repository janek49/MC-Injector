package com.mojang.math;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.Arrays;

@ClientJarOnly
public class Vector4f {
   private final float[] values;

   public Vector4f() {
      this.values = new float[4];
   }

   public Vector4f(float var1, float var2, float var3, float var4) {
      this.values = new float[]{var1, var2, var3, var4};
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object != null && this.getClass() == object.getClass()) {
         Vector4f var2 = (Vector4f)object;
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

   public float w() {
      return this.values[3];
   }

   public void mul(Vector3f vector3f) {
      this.values[0] *= vector3f.x();
      this.values[1] *= vector3f.y();
      this.values[2] *= vector3f.z();
   }

   public void set(float var1, float var2, float var3, float var4) {
      this.values[0] = var1;
      this.values[1] = var2;
      this.values[2] = var3;
      this.values[3] = var4;
   }

   public void transform(Quaternion quaternion) {
      Quaternion quaternion = new Quaternion(quaternion);
      quaternion.mul(new Quaternion(this.x(), this.y(), this.z(), 0.0F));
      Quaternion var3 = new Quaternion(quaternion);
      var3.conj();
      quaternion.mul(var3);
      this.set(quaternion.i(), quaternion.j(), quaternion.k(), this.w());
   }
}
