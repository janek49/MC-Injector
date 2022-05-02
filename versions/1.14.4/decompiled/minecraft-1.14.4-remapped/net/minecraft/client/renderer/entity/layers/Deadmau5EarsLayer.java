package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;

@ClientJarOnly
public class Deadmau5EarsLayer extends RenderLayer {
   public Deadmau5EarsLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(AbstractClientPlayer abstractClientPlayer, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if("deadmau5".equals(abstractClientPlayer.getName().getString()) && abstractClientPlayer.isSkinLoaded() && !abstractClientPlayer.isInvisible()) {
         this.bindTexture(abstractClientPlayer.getSkinTextureLocation());

         for(int var9 = 0; var9 < 2; ++var9) {
            float var10 = Mth.lerp(var4, abstractClientPlayer.yRotO, abstractClientPlayer.yRot) - Mth.lerp(var4, abstractClientPlayer.yBodyRotO, abstractClientPlayer.yBodyRot);
            float var11 = Mth.lerp(var4, abstractClientPlayer.xRotO, abstractClientPlayer.xRot);
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(var10, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(var11, 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(0.375F * (float)(var9 * 2 - 1), 0.0F, 0.0F);
            GlStateManager.translatef(0.0F, -0.375F, 0.0F);
            GlStateManager.rotatef(-var11, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(-var10, 0.0F, 1.0F, 0.0F);
            float var12 = 1.3333334F;
            GlStateManager.scalef(1.3333334F, 1.3333334F, 1.3333334F);
            ((PlayerModel)this.getParentModel()).renderEars(0.0625F);
            GlStateManager.popMatrix();
         }

      }
   }

   public boolean colorsOnDamage() {
      return true;
   }
}
