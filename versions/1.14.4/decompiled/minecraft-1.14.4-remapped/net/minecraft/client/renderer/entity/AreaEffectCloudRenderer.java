package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AreaEffectCloud;

@ClientJarOnly
public class AreaEffectCloudRenderer extends EntityRenderer {
   public AreaEffectCloudRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
   }

   @Nullable
   protected ResourceLocation getTextureLocation(AreaEffectCloud areaEffectCloud) {
      return null;
   }
}
