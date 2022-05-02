package net.minecraft.client.renderer.culling;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.FrustumData;
import net.minecraft.world.phys.AABB;

@ClientJarOnly
public class FrustumCuller implements Culler {
   private final FrustumData frustum;
   private double xOff;
   private double yOff;
   private double zOff;

   public FrustumCuller() {
      this(Frustum.getFrustum());
   }

   public FrustumCuller(FrustumData frustum) {
      this.frustum = frustum;
   }

   public void prepare(double xOff, double yOff, double zOff) {
      this.xOff = xOff;
      this.yOff = yOff;
      this.zOff = zOff;
   }

   public boolean cubeInFrustum(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.frustum.cubeInFrustum(var1 - this.xOff, var3 - this.yOff, var5 - this.zOff, var7 - this.xOff, var9 - this.yOff, var11 - this.zOff);
   }

   public boolean isVisible(AABB aABB) {
      return this.cubeInFrustum(aABB.minX, aABB.minY, aABB.minZ, aABB.maxX, aABB.maxY, aABB.maxZ);
   }
}
