package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.CaveSpider;

@ClientJarOnly
public class CaveSpiderRenderer extends SpiderRenderer {
   private static final ResourceLocation CAVE_SPIDER_LOCATION = new ResourceLocation("textures/entity/spider/cave_spider.png");

   public CaveSpiderRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
      this.shadowRadius *= 0.7F;
   }

   protected void scale(CaveSpider caveSpider, float var2) {
      GlStateManager.scalef(0.7F, 0.7F, 0.7F);
   }

   protected ResourceLocation getTextureLocation(CaveSpider caveSpider) {
      return CAVE_SPIDER_LOCATION;
   }
}
