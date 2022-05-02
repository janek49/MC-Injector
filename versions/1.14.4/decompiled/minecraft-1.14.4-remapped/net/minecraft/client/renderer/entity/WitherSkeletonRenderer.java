package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

@ClientJarOnly
public class WitherSkeletonRenderer extends SkeletonRenderer {
   private static final ResourceLocation WITHER_SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");

   public WitherSkeletonRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
   }

   protected ResourceLocation getTextureLocation(AbstractSkeleton abstractSkeleton) {
      return WITHER_SKELETON_LOCATION;
   }

   protected void scale(AbstractSkeleton abstractSkeleton, float var2) {
      GlStateManager.scalef(1.2F, 1.2F, 1.2F);
   }
}
