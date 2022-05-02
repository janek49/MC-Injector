package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.VexModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vex;

@ClientJarOnly
public class VexRenderer extends HumanoidMobRenderer {
   private static final ResourceLocation VEX_LOCATION = new ResourceLocation("textures/entity/illager/vex.png");
   private static final ResourceLocation VEX_CHARGING_LOCATION = new ResourceLocation("textures/entity/illager/vex_charging.png");

   public VexRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new VexModel(), 0.3F);
   }

   protected ResourceLocation getTextureLocation(Vex vex) {
      return vex.isCharging()?VEX_CHARGING_LOCATION:VEX_LOCATION;
   }

   protected void scale(Vex vex, float var2) {
      GlStateManager.scalef(0.4F, 0.4F, 0.4F);
   }
}
