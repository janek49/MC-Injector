package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.SpectralArrow;

@ClientJarOnly
public class SpectralArrowRenderer extends ArrowRenderer {
   public static final ResourceLocation SPECTRAL_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/spectral_arrow.png");

   public SpectralArrowRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
   }

   protected ResourceLocation getTextureLocation(SpectralArrow spectralArrow) {
      return SPECTRAL_ARROW_LOCATION;
   }
}
