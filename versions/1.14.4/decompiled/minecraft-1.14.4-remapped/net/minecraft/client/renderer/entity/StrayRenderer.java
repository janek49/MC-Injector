package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.layers.StrayClothingLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

@ClientJarOnly
public class StrayRenderer extends SkeletonRenderer {
   private static final ResourceLocation STRAY_SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/stray.png");

   public StrayRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
      this.addLayer(new StrayClothingLayer(this));
   }

   protected ResourceLocation getTextureLocation(AbstractSkeleton abstractSkeleton) {
      return STRAY_SKELETON_LOCATION;
   }
}
