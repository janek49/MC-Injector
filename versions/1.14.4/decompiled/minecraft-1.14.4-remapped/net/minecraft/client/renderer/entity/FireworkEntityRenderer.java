package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;

@ClientJarOnly
public class FireworkEntityRenderer extends EntityRenderer {
   private final ItemRenderer itemRenderer;

   public FireworkEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
      super(entityRenderDispatcher);
      this.itemRenderer = itemRenderer;
   }

   public void render(FireworkRocketEntity fireworkRocketEntity, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var2, (float)var4, (float)var6);
      GlStateManager.enableRescaleNormal();
      GlStateManager.rotatef(-this.entityRenderDispatcher.playerRotY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef((float)(this.entityRenderDispatcher.options.thirdPersonView == 2?-1:1) * this.entityRenderDispatcher.playerRotX, 1.0F, 0.0F, 0.0F);
      if(fireworkRocketEntity.isShotAtAngle()) {
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
      } else {
         GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
      }

      this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
      if(this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(fireworkRocketEntity));
      }

      this.itemRenderer.renderStatic(fireworkRocketEntity.getItem(), ItemTransforms.TransformType.GROUND);
      if(this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.render(fireworkRocketEntity, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(FireworkRocketEntity fireworkRocketEntity) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
