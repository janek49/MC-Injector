package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SheepFurLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;

@ClientJarOnly
public class SheepRenderer extends MobRenderer {
   private static final ResourceLocation SHEEP_LOCATION = new ResourceLocation("textures/entity/sheep/sheep.png");

   public SheepRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new SheepModel(), 0.7F);
      this.addLayer(new SheepFurLayer(this));
   }

   protected ResourceLocation getTextureLocation(Sheep sheep) {
      return SHEEP_LOCATION;
   }
}
