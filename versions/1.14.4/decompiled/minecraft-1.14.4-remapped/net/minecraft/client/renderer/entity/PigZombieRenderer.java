package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.PigZombie;

@ClientJarOnly
public class PigZombieRenderer extends HumanoidMobRenderer {
   private static final ResourceLocation ZOMBIE_PIGMAN_LOCATION = new ResourceLocation("textures/entity/zombie_pigman.png");

   public PigZombieRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new ZombieModel(), 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, new ZombieModel(0.5F, true), new ZombieModel(1.0F, true)));
   }

   protected ResourceLocation getTextureLocation(PigZombie pigZombie) {
      return ZOMBIE_PIGMAN_LOCATION;
   }
}
