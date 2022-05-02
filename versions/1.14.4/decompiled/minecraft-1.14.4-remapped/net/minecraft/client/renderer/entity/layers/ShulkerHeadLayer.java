package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;

@ClientJarOnly
public class ShulkerHeadLayer extends RenderLayer {
   public ShulkerHeadLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(Shulker shulker, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      GlStateManager.pushMatrix();
      switch(shulker.getAttachFace()) {
      case DOWN:
      default:
         break;
      case EAST:
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(1.0F, -1.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
         break;
      case WEST:
         GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(-1.0F, -1.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
         break;
      case NORTH:
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(0.0F, -1.0F, -1.0F);
         break;
      case SOUTH:
         GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(0.0F, -1.0F, 1.0F);
         break;
      case UP:
         GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(0.0F, -2.0F, 0.0F);
      }

      ModelPart var9 = ((ShulkerModel)this.getParentModel()).getHead();
      var9.yRot = var6 * 0.017453292F;
      var9.xRot = var7 * 0.017453292F;
      DyeColor var10 = shulker.getColor();
      if(var10 == null) {
         this.bindTexture(ShulkerRenderer.DEFAULT_TEXTURE_LOCATION);
      } else {
         this.bindTexture(ShulkerRenderer.TEXTURE_LOCATION[var10.getId()]);
      }

      var9.render(var8);
      GlStateManager.popMatrix();
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
