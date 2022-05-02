package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.PolarBearModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.PolarBear;

@ClientJarOnly
public class PolarBearRenderer extends MobRenderer {
   private static final ResourceLocation BEAR_LOCATION = new ResourceLocation("textures/entity/bear/polarbear.png");

   public PolarBearRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new PolarBearModel(), 0.9F);
   }

   protected ResourceLocation getTextureLocation(PolarBear polarBear) {
      return BEAR_LOCATION;
   }

   protected void scale(PolarBear polarBear, float var2) {
      GlStateManager.scalef(1.2F, 1.2F, 1.2F);
      super.scale(polarBear, var2);
   }
}
