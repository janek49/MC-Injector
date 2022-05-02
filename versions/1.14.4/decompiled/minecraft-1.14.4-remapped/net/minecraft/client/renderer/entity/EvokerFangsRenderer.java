package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.EvokerFangsModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.EvokerFangs;

@ClientJarOnly
public class EvokerFangsRenderer extends EntityRenderer {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/illager/evoker_fangs.png");
   private final EvokerFangsModel model = new EvokerFangsModel();

   public EvokerFangsRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
   }

   public void render(EvokerFangs evokerFangs, double var2, double var4, double var6, float var8, float var9) {
      float var10 = evokerFangs.getAnimationProgress(var9);
      if(var10 != 0.0F) {
         float var11 = 2.0F;
         if(var10 > 0.9F) {
            var11 = (float)((double)var11 * ((1.0D - (double)var10) / 0.10000000149011612D));
         }

         GlStateManager.pushMatrix();
         GlStateManager.disableCull();
         GlStateManager.enableAlphaTest();
         this.bindTexture(evokerFangs);
         GlStateManager.translatef((float)var2, (float)var4, (float)var6);
         GlStateManager.rotatef(90.0F - evokerFangs.yRot, 0.0F, 1.0F, 0.0F);
         GlStateManager.scalef(-var11, -var11, var11);
         float var12 = 0.03125F;
         GlStateManager.translatef(0.0F, -0.626F, 0.0F);
         this.model.render(evokerFangs, var10, 0.0F, 0.0F, evokerFangs.yRot, evokerFangs.xRot, 0.03125F);
         GlStateManager.popMatrix();
         GlStateManager.enableCull();
         super.render(evokerFangs, var2, var4, var6, var8, var9);
      }
   }

   protected ResourceLocation getTextureLocation(EvokerFangs evokerFangs) {
      return TEXTURE_LOCATION;
   }
}
