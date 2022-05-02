package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

@ClientJarOnly
public class HumanoidMobRenderer extends MobRenderer {
   private static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation("textures/entity/steve.png");

   public HumanoidMobRenderer(EntityRenderDispatcher entityRenderDispatcher, HumanoidModel humanoidModel, float var3) {
      super(entityRenderDispatcher, humanoidModel, var3);
      this.addLayer(new CustomHeadLayer(this));
      this.addLayer(new ElytraLayer(this));
      this.addLayer(new ItemInHandLayer(this));
   }

   protected ResourceLocation getTextureLocation(Mob mob) {
      return DEFAULT_LOCATION;
   }
}
