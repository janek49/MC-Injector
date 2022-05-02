package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;

@ClientJarOnly
public class CowRenderer extends MobRenderer {
   private static final ResourceLocation COW_LOCATION = new ResourceLocation("textures/entity/cow/cow.png");

   public CowRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new CowModel(), 0.7F);
   }

   protected ResourceLocation getTextureLocation(Cow cow) {
      return COW_LOCATION;
   }
}
