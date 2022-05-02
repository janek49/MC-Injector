package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.LlamaSpit;

@ClientJarOnly
public class LlamaSpitRenderer extends EntityRenderer {
   private static final ResourceLocation LLAMA_SPIT_LOCATION = new ResourceLocation("textures/entity/llama/spit.png");
   private final LlamaSpitModel model = new LlamaSpitModel();

   public LlamaSpitRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
   }

   public void render(LlamaSpit llamaSpit, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var2, (float)var4 + 0.15F, (float)var6);
      GlStateManager.rotatef(Mth.lerp(var9, llamaSpit.yRotO, llamaSpit.yRot) - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(Mth.lerp(var9, llamaSpit.xRotO, llamaSpit.xRot), 0.0F, 0.0F, 1.0F);
      this.bindTexture(llamaSpit);
      if(this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(llamaSpit));
      }

      this.model.render(llamaSpit, var9, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      if(this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      super.render(llamaSpit, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(LlamaSpit llamaSpit) {
      return LLAMA_SPIT_LOCATION;
   }
}
