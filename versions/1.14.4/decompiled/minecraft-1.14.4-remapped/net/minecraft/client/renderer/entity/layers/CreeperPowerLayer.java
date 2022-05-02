package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

@ClientJarOnly
public class CreeperPowerLayer extends RenderLayer {
   private static final ResourceLocation POWER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
   private final CreeperModel model = new CreeperModel(2.0F);

   public CreeperPowerLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(Creeper creeper, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if(creeper.isPowered()) {
         boolean var9 = creeper.isInvisible();
         GlStateManager.depthMask(!var9);
         this.bindTexture(POWER_LOCATION);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         float var10 = (float)creeper.tickCount + var4;
         GlStateManager.translatef(var10 * 0.01F, var10 * 0.01F, 0.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.enableBlend();
         float var11 = 0.5F;
         GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
         ((CreeperModel)this.getParentModel()).copyPropertiesTo(this.model);
         GameRenderer var12 = Minecraft.getInstance().gameRenderer;
         var12.resetFogColor(true);
         this.model.render(creeper, var2, var3, var5, var6, var7, var8);
         var12.resetFogColor(false);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
      }
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
