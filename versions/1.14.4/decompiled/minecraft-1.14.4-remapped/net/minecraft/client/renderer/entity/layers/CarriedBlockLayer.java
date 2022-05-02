package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.block.state.BlockState;

@ClientJarOnly
public class CarriedBlockLayer extends RenderLayer {
   public CarriedBlockLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(EnderMan enderMan, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      BlockState var9 = enderMan.getCarriedBlock();
      if(var9 != null) {
         GlStateManager.enableRescaleNormal();
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 0.6875F, -0.75F);
         GlStateManager.rotatef(20.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(45.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(0.25F, 0.1875F, 0.25F);
         float var10 = 0.5F;
         GlStateManager.scalef(-0.5F, -0.5F, 0.5F);
         int var11 = enderMan.getLightColor();
         int var12 = var11 % 65536;
         int var13 = var11 / 65536;
         GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)var12, (float)var13);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
         Minecraft.getInstance().getBlockRenderer().renderSingleBlock(var9, 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
      }
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
