package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Spider;

@ClientJarOnly
public class SpiderRenderer extends MobRenderer {
   private static final ResourceLocation SPIDER_LOCATION = new ResourceLocation("textures/entity/spider/spider.png");

   public SpiderRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new SpiderModel(), 0.8F);
      this.addLayer(new SpiderEyesLayer(this));
   }

   protected float getFlipDegrees(Spider spider) {
      return 180.0F;
   }

   protected ResourceLocation getTextureLocation(Spider spider) {
      return SPIDER_LOCATION;
   }
}
