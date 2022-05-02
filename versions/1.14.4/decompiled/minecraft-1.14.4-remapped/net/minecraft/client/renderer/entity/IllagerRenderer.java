package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.monster.AbstractIllager;

@ClientJarOnly
public abstract class IllagerRenderer extends MobRenderer {
   protected IllagerRenderer(EntityRenderDispatcher entityRenderDispatcher, IllagerModel illagerModel, float var3) {
      super(entityRenderDispatcher, illagerModel, var3);
      this.addLayer(new CustomHeadLayer(this));
   }

   public IllagerRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new IllagerModel(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new CustomHeadLayer(this));
   }

   protected void scale(AbstractIllager abstractIllager, float var2) {
      float var3 = 0.9375F;
      GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
   }
}
