package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.BatModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ambient.Bat;

@ClientJarOnly
public class BatRenderer extends MobRenderer {
   private static final ResourceLocation BAT_LOCATION = new ResourceLocation("textures/entity/bat.png");

   public BatRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new BatModel(), 0.25F);
   }

   protected ResourceLocation getTextureLocation(Bat bat) {
      return BAT_LOCATION;
   }

   protected void scale(Bat bat, float var2) {
      GlStateManager.scalef(0.35F, 0.35F, 0.35F);
   }

   protected void setupRotations(Bat bat, float var2, float var3, float var4) {
      if(bat.isResting()) {
         GlStateManager.translatef(0.0F, -0.1F, 0.0F);
      } else {
         GlStateManager.translatef(0.0F, Mth.cos(var2 * 0.3F) * 0.1F, 0.0F);
      }

      super.setupRotations(bat, var2, var3, var4);
   }
}
