package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@ClientJarOnly
public class CapeLayer extends RenderLayer {
   public CapeLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(AbstractClientPlayer abstractClientPlayer, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if(abstractClientPlayer.isCapeLoaded() && !abstractClientPlayer.isInvisible() && abstractClientPlayer.isModelPartShown(PlayerModelPart.CAPE) && abstractClientPlayer.getCloakTextureLocation() != null) {
         ItemStack var9 = abstractClientPlayer.getItemBySlot(EquipmentSlot.CHEST);
         if(var9.getItem() != Items.ELYTRA) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.bindTexture(abstractClientPlayer.getCloakTextureLocation());
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0F, 0.0F, 0.125F);
            double var10 = Mth.lerp((double)var4, abstractClientPlayer.xCloakO, abstractClientPlayer.xCloak) - Mth.lerp((double)var4, abstractClientPlayer.xo, abstractClientPlayer.x);
            double var12 = Mth.lerp((double)var4, abstractClientPlayer.yCloakO, abstractClientPlayer.yCloak) - Mth.lerp((double)var4, abstractClientPlayer.yo, abstractClientPlayer.y);
            double var14 = Mth.lerp((double)var4, abstractClientPlayer.zCloakO, abstractClientPlayer.zCloak) - Mth.lerp((double)var4, abstractClientPlayer.zo, abstractClientPlayer.z);
            float var16 = abstractClientPlayer.yBodyRotO + (abstractClientPlayer.yBodyRot - abstractClientPlayer.yBodyRotO);
            double var17 = (double)Mth.sin(var16 * 0.017453292F);
            double var19 = (double)(-Mth.cos(var16 * 0.017453292F));
            float var21 = (float)var12 * 10.0F;
            var21 = Mth.clamp(var21, -6.0F, 32.0F);
            float var22 = (float)(var10 * var17 + var14 * var19) * 100.0F;
            var22 = Mth.clamp(var22, 0.0F, 150.0F);
            float var23 = (float)(var10 * var19 - var14 * var17) * 100.0F;
            var23 = Mth.clamp(var23, -20.0F, 20.0F);
            if(var22 < 0.0F) {
               var22 = 0.0F;
            }

            float var24 = Mth.lerp(var4, abstractClientPlayer.oBob, abstractClientPlayer.bob);
            var21 = var21 + Mth.sin(Mth.lerp(var4, abstractClientPlayer.walkDistO, abstractClientPlayer.walkDist) * 6.0F) * 32.0F * var24;
            if(abstractClientPlayer.isVisuallySneaking()) {
               var21 += 25.0F;
            }

            GlStateManager.rotatef(6.0F + var22 / 2.0F + var21, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(var23 / 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(-var23 / 2.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            ((PlayerModel)this.getParentModel()).renderCloak(0.0625F);
            GlStateManager.popMatrix();
         }
      }
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
