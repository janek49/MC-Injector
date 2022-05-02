package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SnowGolemHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.SnowGolem;

@ClientJarOnly
public class SnowGolemRenderer extends MobRenderer {
   private static final ResourceLocation SNOW_GOLEM_LOCATION = new ResourceLocation("textures/entity/snow_golem.png");

   public SnowGolemRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new SnowGolemModel(), 0.5F);
      this.addLayer(new SnowGolemHeadLayer(this));
   }

   protected ResourceLocation getTextureLocation(SnowGolem snowGolem) {
      return SNOW_GOLEM_LOCATION;
   }
}
