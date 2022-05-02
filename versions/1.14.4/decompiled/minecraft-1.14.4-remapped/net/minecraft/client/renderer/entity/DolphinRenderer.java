package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.DolphinModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.DolphinCarryingItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Dolphin;

@ClientJarOnly
public class DolphinRenderer extends MobRenderer {
   private static final ResourceLocation DOLPHIN_LOCATION = new ResourceLocation("textures/entity/dolphin.png");

   public DolphinRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new DolphinModel(), 0.7F);
      this.addLayer(new DolphinCarryingItemLayer(this));
   }

   protected ResourceLocation getTextureLocation(Dolphin dolphin) {
      return DOLPHIN_LOCATION;
   }

   protected void scale(Dolphin dolphin, float var2) {
      float var3 = 1.0F;
      GlStateManager.scalef(1.0F, 1.0F, 1.0F);
   }

   protected void setupRotations(Dolphin dolphin, float var2, float var3, float var4) {
      super.setupRotations(dolphin, var2, var3, var4);
   }
}
