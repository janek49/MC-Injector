package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.PillagerModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Pillager;

@ClientJarOnly
public class PillagerRenderer extends IllagerRenderer {
   private static final ResourceLocation PILLAGER = new ResourceLocation("textures/entity/illager/pillager.png");

   public PillagerRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new PillagerModel(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new ItemInHandLayer(this));
   }

   protected ResourceLocation getTextureLocation(Pillager pillager) {
      return PILLAGER;
   }
}
