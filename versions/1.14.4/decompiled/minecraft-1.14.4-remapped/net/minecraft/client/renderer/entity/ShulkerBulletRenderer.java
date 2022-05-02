package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ShulkerBullet;

@ClientJarOnly
public class ShulkerBulletRenderer extends EntityRenderer {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/shulker/spark.png");
   private final ShulkerBulletModel model = new ShulkerBulletModel();

   public ShulkerBulletRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
   }

   private float rotlerp(float var1, float var2, float var3) {
      float var4;
      for(var4 = var2 - var1; var4 < -180.0F; var4 += 360.0F) {
         ;
      }

      while(var4 >= 180.0F) {
         var4 -= 360.0F;
      }

      return var1 + var3 * var4;
   }

   public void render(ShulkerBullet shulkerBullet, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      float var10 = this.rotlerp(shulkerBullet.yRotO, shulkerBullet.yRot, var9);
      float var11 = Mth.lerp(var9, shulkerBullet.xRotO, shulkerBullet.xRot);
      float var12 = (float)shulkerBullet.tickCount + var9;
      GlStateManager.translatef((float)var2, (float)var4 + 0.15F, (float)var6);
      GlStateManager.rotatef(Mth.sin(var12 * 0.1F) * 180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(Mth.cos(var12 * 0.1F) * 180.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(Mth.sin(var12 * 0.15F) * 360.0F, 0.0F, 0.0F, 1.0F);
      float var13 = 0.03125F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      this.bindTexture(shulkerBullet);
      this.model.render(shulkerBullet, 0.0F, 0.0F, 0.0F, var10, var11, 0.03125F);
      GlStateManager.enableBlend();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.scalef(1.5F, 1.5F, 1.5F);
      this.model.render(shulkerBullet, 0.0F, 0.0F, 0.0F, var10, var11, 0.03125F);
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
      super.render(shulkerBullet, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(ShulkerBullet shulkerBullet) {
      return TEXTURE_LOCATION;
   }
}
