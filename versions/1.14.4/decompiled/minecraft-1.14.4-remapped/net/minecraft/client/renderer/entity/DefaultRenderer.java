package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

@ClientJarOnly
public class DefaultRenderer extends EntityRenderer {
   public DefaultRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
   }

   public void render(Entity entity, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      render(entity.getBoundingBox(), var2 - entity.xOld, var4 - entity.yOld, var6 - entity.zOld);
      GlStateManager.popMatrix();
      super.render(entity, var2, var4, var6, var8, var9);
   }

   @Nullable
   protected ResourceLocation getTextureLocation(Entity entity) {
      return null;
   }
}
