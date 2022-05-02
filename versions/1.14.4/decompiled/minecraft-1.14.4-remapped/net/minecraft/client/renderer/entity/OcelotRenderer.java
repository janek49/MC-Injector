package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Ocelot;

@ClientJarOnly
public class OcelotRenderer extends MobRenderer {
   private static final ResourceLocation CAT_OCELOT_LOCATION = new ResourceLocation("textures/entity/cat/ocelot.png");

   public OcelotRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new OcelotModel(0.0F), 0.4F);
   }

   protected ResourceLocation getTextureLocation(Ocelot ocelot) {
      return CAT_OCELOT_LOCATION;
   }
}
