package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Blaze;

@ClientJarOnly
public class BlazeRenderer extends MobRenderer {
   private static final ResourceLocation BLAZE_LOCATION = new ResourceLocation("textures/entity/blaze.png");

   public BlazeRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new BlazeModel(), 0.5F);
   }

   protected ResourceLocation getTextureLocation(Blaze blaze) {
      return BLAZE_LOCATION;
   }
}
