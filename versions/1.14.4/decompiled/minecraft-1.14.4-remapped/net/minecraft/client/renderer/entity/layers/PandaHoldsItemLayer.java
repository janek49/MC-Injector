package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.item.ItemStack;

@ClientJarOnly
public class PandaHoldsItemLayer extends RenderLayer {
   public PandaHoldsItemLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(Panda panda, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      ItemStack var9 = panda.getItemBySlot(EquipmentSlot.MAINHAND);
      if(panda.isSitting() && !var9.isEmpty() && !panda.isScared()) {
         float var10 = -0.6F;
         float var11 = 1.4F;
         if(panda.isEating()) {
            var10 -= 0.2F * Mth.sin(var5 * 0.6F) + 0.2F;
            var11 -= 0.09F * Mth.sin(var5 * 0.6F);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.1F, var11, var10);
         Minecraft.getInstance().getItemRenderer().renderWithMobState(var9, panda, ItemTransforms.TransformType.GROUND, false);
         GlStateManager.popMatrix();
      }
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
