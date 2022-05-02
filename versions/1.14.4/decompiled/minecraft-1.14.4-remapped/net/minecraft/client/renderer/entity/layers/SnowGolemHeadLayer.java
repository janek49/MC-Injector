package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

@ClientJarOnly
public class SnowGolemHeadLayer extends RenderLayer {
   public SnowGolemHeadLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(SnowGolem snowGolem, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if(!snowGolem.isInvisible() && snowGolem.hasPumpkin()) {
         GlStateManager.pushMatrix();
         ((SnowGolemModel)this.getParentModel()).getHead().translateTo(0.0625F);
         float var9 = 0.625F;
         GlStateManager.translatef(0.0F, -0.34375F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.scalef(0.625F, -0.625F, -0.625F);
         Minecraft.getInstance().getItemInHandRenderer().renderItem(snowGolem, new ItemStack(Blocks.CARVED_PUMPKIN), ItemTransforms.TransformType.HEAD);
         GlStateManager.popMatrix();
      }
   }

   public boolean colorsOnDamage() {
      return true;
   }
}
