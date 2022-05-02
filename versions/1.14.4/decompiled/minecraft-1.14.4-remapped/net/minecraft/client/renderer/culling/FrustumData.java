package net.minecraft.client.renderer.culling;

import com.fox2code.repacker.ClientJarOnly;

@ClientJarOnly
public class FrustumData {
   public final float[][] frustumData = new float[6][4];
   public final float[] projectionMatrix = new float[16];
   public final float[] modelViewMatrix = new float[16];
   public final float[] clip = new float[16];

   private double discriminant(float[] floats, double var2, double var4, double var6) {
      return (double)floats[0] * var2 + (double)floats[1] * var4 + (double)floats[2] * var6 + (double)floats[3];
   }

   public boolean cubeInFrustum(double var1, double var3, double var5, double var7, double var9, double var11) {
      for(int var13 = 0; var13 < 6; ++var13) {
         float[] vars14 = this.frustumData[var13];
         if(this.discriminant(vars14, var1, var3, var5) <= 0.0D && this.discriminant(vars14, var7, var3, var5) <= 0.0D && this.discriminant(vars14, var1, var9, var5) <= 0.0D && this.discriminant(vars14, var7, var9, var5) <= 0.0D && this.discriminant(vars14, var1, var3, var11) <= 0.0D && this.discriminant(vars14, var7, var3, var11) <= 0.0D && this.discriminant(vars14, var1, var9, var11) <= 0.0D && this.discriminant(vars14, var7, var9, var11) <= 0.0D) {
            return false;
         }
      }

      return true;
   }
}
