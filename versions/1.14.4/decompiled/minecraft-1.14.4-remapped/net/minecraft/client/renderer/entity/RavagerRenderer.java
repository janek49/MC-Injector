package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.RavagerModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Ravager;

@ClientJarOnly
public class RavagerRenderer extends MobRenderer {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/illager/ravager.png");

   public RavagerRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new RavagerModel(), 1.1F);
   }

   protected ResourceLocation getTextureLocation(Ravager ravager) {
      return TEXTURE_LOCATION;
   }
}
