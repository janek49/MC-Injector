package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.AbstractArrow;

@ClientJarOnly
public abstract class ArrowRenderer extends EntityRenderer {
   public ArrowRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
   }

   public void render(AbstractArrow abstractArrow, double var2, double var4, double var6, float var8, float var9) {
      this.bindTexture(abstractArrow);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.disableLighting();
      GlStateManager.translatef((float)var2, (float)var4, (float)var6);
      GlStateManager.rotatef(Mth.lerp(var9, abstractArrow.yRotO, abstractArrow.yRot) - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(Mth.lerp(var9, abstractArrow.xRotO, abstractArrow.xRot), 0.0F, 0.0F, 1.0F);
      Tesselator var10 = Tesselator.getInstance();
      BufferBuilder var11 = var10.getBuilder();
      int var12 = 0;
      float var13 = 0.0F;
      float var14 = 0.5F;
      float var15 = 0.0F;
      float var16 = 0.15625F;
      float var17 = 0.0F;
      float var18 = 0.15625F;
      float var19 = 0.15625F;
      float var20 = 0.3125F;
      float var21 = 0.05625F;
      GlStateManager.enableRescaleNormal();
      float var22 = (float)abstractArrow.shakeTime - var9;
      if(var22 > 0.0F) {
         float var23 = -Mth.sin(var22 * 3.0F) * var22;
         GlStateManager.rotatef(var23, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.rotatef(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scalef(0.05625F, 0.05625F, 0.05625F);
      GlStateManager.translatef(-4.0F, 0.0F, 0.0F);
      if(this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(abstractArrow));
      }

      GlStateManager.normal3f(0.05625F, 0.0F, 0.0F);
      var11.begin(7, DefaultVertexFormat.POSITION_TEX);
      var11.vertex(-7.0D, -2.0D, -2.0D).uv(0.0D, 0.15625D).endVertex();
      var11.vertex(-7.0D, -2.0D, 2.0D).uv(0.15625D, 0.15625D).endVertex();
      var11.vertex(-7.0D, 2.0D, 2.0D).uv(0.15625D, 0.3125D).endVertex();
      var11.vertex(-7.0D, 2.0D, -2.0D).uv(0.0D, 0.3125D).endVertex();
      var10.end();
      GlStateManager.normal3f(-0.05625F, 0.0F, 0.0F);
      var11.begin(7, DefaultVertexFormat.POSITION_TEX);
      var11.vertex(-7.0D, 2.0D, -2.0D).uv(0.0D, 0.15625D).endVertex();
      var11.vertex(-7.0D, 2.0D, 2.0D).uv(0.15625D, 0.15625D).endVertex();
      var11.vertex(-7.0D, -2.0D, 2.0D).uv(0.15625D, 0.3125D).endVertex();
      var11.vertex(-7.0D, -2.0D, -2.0D).uv(0.0D, 0.3125D).endVertex();
      var10.end();

      for(int var23 = 0; var23 < 4; ++var23) {
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.normal3f(0.0F, 0.0F, 0.05625F);
         var11.begin(7, DefaultVertexFormat.POSITION_TEX);
         var11.vertex(-8.0D, -2.0D, 0.0D).uv(0.0D, 0.0D).endVertex();
         var11.vertex(8.0D, -2.0D, 0.0D).uv(0.5D, 0.0D).endVertex();
         var11.vertex(8.0D, 2.0D, 0.0D).uv(0.5D, 0.15625D).endVertex();
         var11.vertex(-8.0D, 2.0D, 0.0D).uv(0.0D, 0.15625D).endVertex();
         var10.end();
      }

      if(this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.disableRescaleNormal();
      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
      super.render(abstractArrow, var2, var4, var6, var8, var9);
   }
}
