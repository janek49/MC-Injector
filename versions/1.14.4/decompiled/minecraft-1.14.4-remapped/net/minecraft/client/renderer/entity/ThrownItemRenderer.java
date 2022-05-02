package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;

@ClientJarOnly
public class ThrownItemRenderer extends EntityRenderer {
   private final ItemRenderer itemRenderer;
   private final float scale;

   public ThrownItemRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer, float scale) {
      super(entityRenderDispatcher);
      this.itemRenderer = itemRenderer;
      this.scale = scale;
   }

   public ThrownItemRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
      this(entityRenderDispatcher, itemRenderer, 1.0F);
   }

   public void render(Entity entity, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var2, (float)var4, (float)var6);
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(this.scale, this.scale, this.scale);
      GlStateManager.rotatef(-this.entityRenderDispatcher.playerRotY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef((float)(this.entityRenderDispatcher.options.thirdPersonView == 2?-1:1) * this.entityRenderDispatcher.playerRotX, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
      this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
      if(this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity));
      }

      this.itemRenderer.renderStatic(((ItemSupplier)entity).getItem(), ItemTransforms.TransformType.GROUND);
      if(this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.render(entity, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(Entity entity) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
