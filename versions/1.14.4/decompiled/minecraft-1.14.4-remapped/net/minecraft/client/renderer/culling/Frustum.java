package net.minecraft.client.renderer.culling;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import java.nio.FloatBuffer;
import net.minecraft.client.renderer.culling.FrustumData;
import net.minecraft.util.Mth;

@ClientJarOnly
public class Frustum extends FrustumData {
   private static final Frustum FRUSTUM = new Frustum();
   private final FloatBuffer _proj = MemoryTracker.createFloatBuffer(16);
   private final FloatBuffer _modl = MemoryTracker.createFloatBuffer(16);
   private final FloatBuffer _clip = MemoryTracker.createFloatBuffer(16);

   public static FrustumData getFrustum() {
      FRUSTUM.calculateFrustum();
      return FRUSTUM;
   }

   private void normalizePlane(float[] floats) {
      float var2 = Mth.sqrt(floats[0] * floats[0] + floats[1] * floats[1] + floats[2] * floats[2]);
      floats[0] /= var2;
      floats[1] /= var2;
      floats[2] /= var2;
      floats[3] /= var2;
   }

   public void calculateFrustum() {
      this._proj.clear();
      this._modl.clear();
      this._clip.clear();
      GlStateManager.getMatrix(2983, this._proj);
      GlStateManager.getMatrix(2982, this._modl);
      float[] vars1 = this.projectionMatrix;
      float[] vars2 = this.modelViewMatrix;
      this._proj.flip().limit(16);
      this._proj.get(vars1);
      this._modl.flip().limit(16);
      this._modl.get(vars2);
      this.clip[0] = vars2[0] * vars1[0] + vars2[1] * vars1[4] + vars2[2] * vars1[8] + vars2[3] * vars1[12];
      this.clip[1] = vars2[0] * vars1[1] + vars2[1] * vars1[5] + vars2[2] * vars1[9] + vars2[3] * vars1[13];
      this.clip[2] = vars2[0] * vars1[2] + vars2[1] * vars1[6] + vars2[2] * vars1[10] + vars2[3] * vars1[14];
      this.clip[3] = vars2[0] * vars1[3] + vars2[1] * vars1[7] + vars2[2] * vars1[11] + vars2[3] * vars1[15];
      this.clip[4] = vars2[4] * vars1[0] + vars2[5] * vars1[4] + vars2[6] * vars1[8] + vars2[7] * vars1[12];
      this.clip[5] = vars2[4] * vars1[1] + vars2[5] * vars1[5] + vars2[6] * vars1[9] + vars2[7] * vars1[13];
      this.clip[6] = vars2[4] * vars1[2] + vars2[5] * vars1[6] + vars2[6] * vars1[10] + vars2[7] * vars1[14];
      this.clip[7] = vars2[4] * vars1[3] + vars2[5] * vars1[7] + vars2[6] * vars1[11] + vars2[7] * vars1[15];
      this.clip[8] = vars2[8] * vars1[0] + vars2[9] * vars1[4] + vars2[10] * vars1[8] + vars2[11] * vars1[12];
      this.clip[9] = vars2[8] * vars1[1] + vars2[9] * vars1[5] + vars2[10] * vars1[9] + vars2[11] * vars1[13];
      this.clip[10] = vars2[8] * vars1[2] + vars2[9] * vars1[6] + vars2[10] * vars1[10] + vars2[11] * vars1[14];
      this.clip[11] = vars2[8] * vars1[3] + vars2[9] * vars1[7] + vars2[10] * vars1[11] + vars2[11] * vars1[15];
      this.clip[12] = vars2[12] * vars1[0] + vars2[13] * vars1[4] + vars2[14] * vars1[8] + vars2[15] * vars1[12];
      this.clip[13] = vars2[12] * vars1[1] + vars2[13] * vars1[5] + vars2[14] * vars1[9] + vars2[15] * vars1[13];
      this.clip[14] = vars2[12] * vars1[2] + vars2[13] * vars1[6] + vars2[14] * vars1[10] + vars2[15] * vars1[14];
      this.clip[15] = vars2[12] * vars1[3] + vars2[13] * vars1[7] + vars2[14] * vars1[11] + vars2[15] * vars1[15];
      float[] vars3 = this.frustumData[0];
      vars3[0] = this.clip[3] - this.clip[0];
      vars3[1] = this.clip[7] - this.clip[4];
      vars3[2] = this.clip[11] - this.clip[8];
      vars3[3] = this.clip[15] - this.clip[12];
      this.normalizePlane(vars3);
      float[] vars4 = this.frustumData[1];
      vars4[0] = this.clip[3] + this.clip[0];
      vars4[1] = this.clip[7] + this.clip[4];
      vars4[2] = this.clip[11] + this.clip[8];
      vars4[3] = this.clip[15] + this.clip[12];
      this.normalizePlane(vars4);
      float[] vars5 = this.frustumData[2];
      vars5[0] = this.clip[3] + this.clip[1];
      vars5[1] = this.clip[7] + this.clip[5];
      vars5[2] = this.clip[11] + this.clip[9];
      vars5[3] = this.clip[15] + this.clip[13];
      this.normalizePlane(vars5);
      float[] vars6 = this.frustumData[3];
      vars6[0] = this.clip[3] - this.clip[1];
      vars6[1] = this.clip[7] - this.clip[5];
      vars6[2] = this.clip[11] - this.clip[9];
      vars6[3] = this.clip[15] - this.clip[13];
      this.normalizePlane(vars6);
      float[] vars7 = this.frustumData[4];
      vars7[0] = this.clip[3] - this.clip[2];
      vars7[1] = this.clip[7] - this.clip[6];
      vars7[2] = this.clip[11] - this.clip[10];
      vars7[3] = this.clip[15] - this.clip[14];
      this.normalizePlane(vars7);
      float[] vars8 = this.frustumData[5];
      vars8[0] = this.clip[3] + this.clip[2];
      vars8[1] = this.clip[7] + this.clip[6];
      vars8[2] = this.clip[11] + this.clip[10];
      vars8[3] = this.clip[15] + this.clip[14];
      this.normalizePlane(vars8);
   }
}
