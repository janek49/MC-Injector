package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@ClientJarOnly
public abstract class SingleQuadParticle extends Particle {
   protected float quadSize;

   protected SingleQuadParticle(Level level, double var2, double var4, double var6) {
      super(level, var2, var4, var6);
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
   }

   protected SingleQuadParticle(Level level, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(level, var2, var4, var6, var8, var10, var12);
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
   }

   public void render(BufferBuilder bufferBuilder, Camera camera, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = this.getQuadSize(var3);
      float var10 = this.getU0();
      float var11 = this.getU1();
      float var12 = this.getV0();
      float var13 = this.getV1();
      float var14 = (float)(Mth.lerp((double)var3, this.xo, this.x) - xOff);
      float var15 = (float)(Mth.lerp((double)var3, this.yo, this.y) - yOff);
      float var16 = (float)(Mth.lerp((double)var3, this.zo, this.z) - zOff);
      int var17 = this.getLightColor(var3);
      int var18 = var17 >> 16 & '\uffff';
      int var19 = var17 & '\uffff';
      Vec3[] vars20 = new Vec3[]{new Vec3((double)(-var4 * var9 - var7 * var9), (double)(-var5 * var9), (double)(-var6 * var9 - var8 * var9)), new Vec3((double)(-var4 * var9 + var7 * var9), (double)(var5 * var9), (double)(-var6 * var9 + var8 * var9)), new Vec3((double)(var4 * var9 + var7 * var9), (double)(var5 * var9), (double)(var6 * var9 + var8 * var9)), new Vec3((double)(var4 * var9 - var7 * var9), (double)(-var5 * var9), (double)(var6 * var9 - var8 * var9))};
      if(this.roll != 0.0F) {
         float var21 = Mth.lerp(var3, this.oRoll, this.roll);
         float var22 = Mth.cos(var21 * 0.5F);
         float var23 = (float)((double)Mth.sin(var21 * 0.5F) * camera.getLookVector().x);
         float var24 = (float)((double)Mth.sin(var21 * 0.5F) * camera.getLookVector().y);
         float var25 = (float)((double)Mth.sin(var21 * 0.5F) * camera.getLookVector().z);
         Vec3 var26 = new Vec3((double)var23, (double)var24, (double)var25);

         for(int var27 = 0; var27 < 4; ++var27) {
            vars20[var27] = var26.scale(2.0D * vars20[var27].dot(var26)).add(vars20[var27].scale((double)(var22 * var22) - var26.dot(var26))).add(var26.cross(vars20[var27]).scale((double)(2.0F * var22)));
         }
      }

      bufferBuilder.vertex((double)var14 + vars20[0].x, (double)var15 + vars20[0].y, (double)var16 + vars20[0].z).uv((double)var11, (double)var13).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var18, var19).endVertex();
      bufferBuilder.vertex((double)var14 + vars20[1].x, (double)var15 + vars20[1].y, (double)var16 + vars20[1].z).uv((double)var11, (double)var12).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var18, var19).endVertex();
      bufferBuilder.vertex((double)var14 + vars20[2].x, (double)var15 + vars20[2].y, (double)var16 + vars20[2].z).uv((double)var10, (double)var12).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var18, var19).endVertex();
      bufferBuilder.vertex((double)var14 + vars20[3].x, (double)var15 + vars20[3].y, (double)var16 + vars20[3].z).uv((double)var10, (double)var13).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var18, var19).endVertex();
   }

   public float getQuadSize(float f) {
      return this.quadSize;
   }

   public Particle scale(float f) {
      this.quadSize *= f;
      return super.scale(f);
   }

   protected abstract float getU0();

   protected abstract float getU1();

   protected abstract float getV0();

   protected abstract float getV1();
}
