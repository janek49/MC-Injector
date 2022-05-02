package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Ghast;

@ClientJarOnly
public class GhastRenderer extends MobRenderer {
   private static final ResourceLocation GHAST_LOCATION = new ResourceLocation("textures/entity/ghast/ghast.png");
   private static final ResourceLocation GHAST_SHOOTING_LOCATION = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

   public GhastRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new GhastModel(), 1.5F);
   }

   protected ResourceLocation getTextureLocation(Ghast ghast) {
      return ghast.isCharging()?GHAST_SHOOTING_LOCATION:GHAST_LOCATION;
   }

   protected void scale(Ghast ghast, float var2) {
      float var3 = 1.0F;
      float var4 = 4.5F;
      float var5 = 4.5F;
      GlStateManager.scalef(4.5F, 4.5F, 4.5F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
