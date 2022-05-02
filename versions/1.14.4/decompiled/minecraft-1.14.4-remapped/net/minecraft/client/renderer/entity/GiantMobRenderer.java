package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.GiantZombieModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Giant;

@ClientJarOnly
public class GiantMobRenderer extends MobRenderer {
   private static final ResourceLocation ZOMBIE_LOCATION = new ResourceLocation("textures/entity/zombie/zombie.png");
   private final float scale;

   public GiantMobRenderer(EntityRenderDispatcher entityRenderDispatcher, float scale) {
      super(entityRenderDispatcher, new GiantZombieModel(), 0.5F * scale);
      this.scale = scale;
      this.addLayer(new ItemInHandLayer(this));
      this.addLayer(new HumanoidArmorLayer(this, new GiantZombieModel(0.5F, true), new GiantZombieModel(1.0F, true)));
   }

   protected void scale(Giant giant, float var2) {
      GlStateManager.scalef(this.scale, this.scale, this.scale);
   }

   protected ResourceLocation getTextureLocation(Giant giant) {
      return ZOMBIE_LOCATION;
   }
}
