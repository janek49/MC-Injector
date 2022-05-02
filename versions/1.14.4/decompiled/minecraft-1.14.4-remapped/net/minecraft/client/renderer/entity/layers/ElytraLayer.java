package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.Consumer;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.AbstractArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@ClientJarOnly
public class ElytraLayer extends RenderLayer {
   private static final ResourceLocation WINGS_LOCATION = new ResourceLocation("textures/entity/elytra.png");
   private final ElytraModel elytraModel = new ElytraModel();

   public ElytraLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(LivingEntity livingEntity, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      ItemStack var9 = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
      if(var9.getItem() == Items.ELYTRA) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         if(livingEntity instanceof AbstractClientPlayer) {
            AbstractClientPlayer var10 = (AbstractClientPlayer)livingEntity;
            if(var10.isElytraLoaded() && var10.getElytraTextureLocation() != null) {
               this.bindTexture(var10.getElytraTextureLocation());
            } else if(var10.isCapeLoaded() && var10.getCloakTextureLocation() != null && var10.isModelPartShown(PlayerModelPart.CAPE)) {
               this.bindTexture(var10.getCloakTextureLocation());
            } else {
               this.bindTexture(WINGS_LOCATION);
            }
         } else {
            this.bindTexture(WINGS_LOCATION);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 0.0F, 0.125F);
         this.elytraModel.setupAnim(livingEntity, var2, var3, var5, var6, var7, var8);
         this.elytraModel.render(livingEntity, var2, var3, var5, var6, var7, var8);
         if(var9.isEnchanted()) {
            AbstractArmorLayer.renderFoil(this::bindTexture, livingEntity, this.elytraModel, var2, var3, var4, var5, var6, var7, var8);
         }

         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
      }
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
