package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

@ClientJarOnly
public class SkeletonRenderer extends HumanoidMobRenderer {
   private static final ResourceLocation SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/skeleton.png");

   public SkeletonRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new SkeletonModel(), 0.5F);
      this.addLayer(new ItemInHandLayer(this));
      this.addLayer(new HumanoidArmorLayer(this, new SkeletonModel(0.5F, true), new SkeletonModel(1.0F, true)));
   }

   protected ResourceLocation getTextureLocation(AbstractSkeleton abstractSkeleton) {
      return SKELETON_LOCATION;
   }
}
