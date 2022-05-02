package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

@ClientJarOnly
public abstract class AbstractZombieRenderer extends HumanoidMobRenderer {
   private static final ResourceLocation ZOMBIE_LOCATION = new ResourceLocation("textures/entity/zombie/zombie.png");

   protected AbstractZombieRenderer(EntityRenderDispatcher entityRenderDispatcher, ZombieModel var2, ZombieModel var3, ZombieModel var4) {
      super(entityRenderDispatcher, var2, 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, var3, var4));
   }

   protected ResourceLocation getTextureLocation(Zombie zombie) {
      return ZOMBIE_LOCATION;
   }

   protected void setupRotations(Zombie zombie, float var2, float var3, float var4) {
      if(zombie.isUnderWaterConverting()) {
         var3 += (float)(Math.cos((double)zombie.tickCount * 3.25D) * 3.141592653589793D * 0.25D);
      }

      super.setupRotations(zombie, var2, var3, var4);
   }
}
