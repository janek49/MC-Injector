package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;

@ClientJarOnly
public class LeashKnotRenderer extends EntityRenderer {
   private static final ResourceLocation KNOT_LOCATION = new ResourceLocation("textures/entity/lead_knot.png");
   private final LeashKnotModel model = new LeashKnotModel();

   public LeashKnotRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
   }

   public void render(LeashFenceKnotEntity leashFenceKnotEntity, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      GlStateManager.translatef((float)var2, (float)var4, (float)var6);
      float var10 = 0.0625F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlphaTest();
      this.bindTexture(leashFenceKnotEntity);
      if(this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(leashFenceKnotEntity));
      }

      this.model.render(leashFenceKnotEntity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      if(this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      super.render(leashFenceKnotEntity, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(LeashFenceKnotEntity leashFenceKnotEntity) {
      return KNOT_LOCATION;
   }
}
