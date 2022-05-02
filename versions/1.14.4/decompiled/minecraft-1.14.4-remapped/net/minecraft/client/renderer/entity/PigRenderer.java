package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.PigSaddleLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;

@ClientJarOnly
public class PigRenderer extends MobRenderer {
   private static final ResourceLocation PIG_LOCATION = new ResourceLocation("textures/entity/pig/pig.png");

   public PigRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new PigModel(), 0.7F);
      this.addLayer(new PigSaddleLayer(this));
   }

   protected ResourceLocation getTextureLocation(Pig pig) {
      return PIG_LOCATION;
   }
}
