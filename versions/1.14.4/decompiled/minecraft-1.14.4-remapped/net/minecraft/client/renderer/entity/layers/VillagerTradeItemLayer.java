package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.block.Block;

@ClientJarOnly
public class VillagerTradeItemLayer extends RenderLayer {
   private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

   public VillagerTradeItemLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(LivingEntity livingEntity, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      ItemStack var9 = livingEntity.getItemBySlot(EquipmentSlot.MAINHAND);
      if(!var9.isEmpty()) {
         Item var10 = var9.getItem();
         Block var11 = Block.byItem(var10);
         GlStateManager.pushMatrix();
         boolean var12 = this.itemRenderer.isGui3d(var9) && var11.getRenderLayer() == BlockLayer.TRANSLUCENT;
         if(var12) {
            GlStateManager.depthMask(false);
         }

         GlStateManager.translatef(0.0F, 0.4F, -0.4F);
         GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
         this.itemRenderer.renderWithMobState(var9, livingEntity, ItemTransforms.TransformType.GROUND, false);
         if(var12) {
            GlStateManager.depthMask(true);
         }

         GlStateManager.popMatrix();
      }
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
