package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

@ClientJarOnly
public class PhantomEyesLayer extends RenderLayer {
   private static final ResourceLocation PHANTOM_EYES_LOCATION = new ResourceLocation("textures/entity/phantom_eyes.png");

   public PhantomEyesLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(Entity lightColor, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.bindTexture(PHANTOM_EYES_LOCATION);
      GlStateManager.enableBlend();
      GlStateManager.disableAlphaTest();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(!lightColor.isInvisible());
      int var9 = '\uf0f0';
      int var10 = '\uf0f0';
      int var11 = 0;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 61680.0F, 0.0F);
      GlStateManager.enableLighting();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GameRenderer var12 = Minecraft.getInstance().gameRenderer;
      var12.resetFogColor(true);
      ((PhantomModel)this.getParentModel()).render(lightColor, var2, var3, var5, var6, var7, var8);
      var12.resetFogColor(false);
      this.setLightColor(lightColor);
      GlStateManager.depthMask(true);
      GlStateManager.disableBlend();
      GlStateManager.enableAlphaTest();
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
