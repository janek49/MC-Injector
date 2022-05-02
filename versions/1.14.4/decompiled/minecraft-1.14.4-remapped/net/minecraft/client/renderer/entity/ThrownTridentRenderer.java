package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownTrident;

@ClientJarOnly
public class ThrownTridentRenderer extends EntityRenderer {
   public static final ResourceLocation TRIDENT_LOCATION = new ResourceLocation("textures/entity/trident.png");
   private final TridentModel model = new TridentModel();

   public ThrownTridentRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
   }

   public void render(ThrownTrident thrownTrident, double var2, double var4, double var6, float var8, float var9) {
      this.bindTexture(thrownTrident);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.disableLighting();
      GlStateManager.translatef((float)var2, (float)var4, (float)var6);
      GlStateManager.rotatef(Mth.lerp(var9, thrownTrident.yRotO, thrownTrident.yRot) - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(Mth.lerp(var9, thrownTrident.xRotO, thrownTrident.xRot) + 90.0F, 0.0F, 0.0F, 1.0F);
      this.model.render();
      GlStateManager.popMatrix();
      this.renderLeash(thrownTrident, var2, var4, var6, var8, var9);
      super.render(thrownTrident, var2, var4, var6, var8, var9);
      GlStateManager.enableLighting();
   }

   protected ResourceLocation getTextureLocation(ThrownTrident thrownTrident) {
      return TRIDENT_LOCATION;
   }

   protected void renderLeash(ThrownTrident thrownTrident, double var2, double var4, double var6, float var8, float var9) {
      Entity var10 = thrownTrident.getOwner();
      if(var10 != null && thrownTrident.isNoPhysics()) {
         Tesselator var11 = Tesselator.getInstance();
         BufferBuilder var12 = var11.getBuilder();
         double var13 = (double)(Mth.lerp(var9 * 0.5F, var10.yRot, var10.yRotO) * 0.017453292F);
         double var15 = Math.cos(var13);
         double var17 = Math.sin(var13);
         double var19 = Mth.lerp((double)var9, var10.xo, var10.x);
         double var21 = Mth.lerp((double)var9, var10.yo + (double)var10.getEyeHeight() * 0.8D, var10.y + (double)var10.getEyeHeight() * 0.8D);
         double var23 = Mth.lerp((double)var9, var10.zo, var10.z);
         double var25 = var15 - var17;
         double var27 = var17 + var15;
         double var29 = Mth.lerp((double)var9, thrownTrident.xo, thrownTrident.x);
         double var31 = Mth.lerp((double)var9, thrownTrident.yo, thrownTrident.y);
         double var33 = Mth.lerp((double)var9, thrownTrident.zo, thrownTrident.z);
         double var35 = (double)((float)(var19 - var29));
         double var37 = (double)((float)(var21 - var31));
         double var39 = (double)((float)(var23 - var33));
         double var41 = Math.sqrt(var35 * var35 + var37 * var37 + var39 * var39);
         int var43 = thrownTrident.getId() + thrownTrident.tickCount;
         double var44 = (double)((float)var43 + var9) * -0.1D;
         double var46 = Math.min(0.5D, var41 / 30.0D);
         GlStateManager.disableTexture();
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 255.0F, 255.0F);
         var12.begin(5, DefaultVertexFormat.POSITION_COLOR);
         int var48 = 37;
         int var49 = 7 - var43 % 7;
         double var50 = 0.1D;

         for(int var52 = 0; var52 <= 37; ++var52) {
            double var53 = (double)var52 / 37.0D;
            float var55 = 1.0F - (float)((var52 + var49) % 7) / 7.0F;
            double var56 = var53 * 2.0D - 1.0D;
            var56 = (1.0D - var56 * var56) * var46;
            double var58 = var2 + var35 * var53 + Math.sin(var53 * 3.141592653589793D * 8.0D + var44) * var25 * var56;
            double var60 = var4 + var37 * var53 + Math.cos(var53 * 3.141592653589793D * 8.0D + var44) * 0.02D + (0.1D + var56) * 1.0D;
            double var62 = var6 + var39 * var53 + Math.sin(var53 * 3.141592653589793D * 8.0D + var44) * var27 * var56;
            float var64 = 0.87F * var55 + 0.3F * (1.0F - var55);
            float var65 = 0.91F * var55 + 0.6F * (1.0F - var55);
            float var66 = 0.85F * var55 + 0.5F * (1.0F - var55);
            var12.vertex(var58, var60, var62).color(var64, var65, var66, 1.0F).endVertex();
            var12.vertex(var58 + 0.1D * var56, var60 + 0.1D * var56, var62).color(var64, var65, var66, 1.0F).endVertex();
            if(var52 > thrownTrident.clientSideReturnTridentTickCount * 2) {
               break;
            }
         }

         var11.end();
         var12.begin(5, DefaultVertexFormat.POSITION_COLOR);

         for(int var52 = 0; var52 <= 37; ++var52) {
            double var53 = (double)var52 / 37.0D;
            float var55 = 1.0F - (float)((var52 + var49) % 7) / 7.0F;
            double var56 = var53 * 2.0D - 1.0D;
            var56 = (1.0D - var56 * var56) * var46;
            double var58 = var2 + var35 * var53 + Math.sin(var53 * 3.141592653589793D * 8.0D + var44) * var25 * var56;
            double var60 = var4 + var37 * var53 + Math.cos(var53 * 3.141592653589793D * 8.0D + var44) * 0.01D + (0.1D + var56) * 1.0D;
            double var62 = var6 + var39 * var53 + Math.sin(var53 * 3.141592653589793D * 8.0D + var44) * var27 * var56;
            float var64 = 0.87F * var55 + 0.3F * (1.0F - var55);
            float var65 = 0.91F * var55 + 0.6F * (1.0F - var55);
            float var66 = 0.85F * var55 + 0.5F * (1.0F - var55);
            var12.vertex(var58, var60, var62).color(var64, var65, var66, 1.0F).endVertex();
            var12.vertex(var58 + 0.1D * var56, var60, var62 + 0.1D * var56).color(var64, var65, var66, 1.0F).endVertex();
            if(var52 > thrownTrident.clientSideReturnTridentTickCount * 2) {
               break;
            }
         }

         var11.end();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture();
         GlStateManager.enableCull();
      }
   }
}
