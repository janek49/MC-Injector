package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Wolf;

@ClientJarOnly
public class WolfRenderer extends MobRenderer {
   private static final ResourceLocation WOLF_LOCATION = new ResourceLocation("textures/entity/wolf/wolf.png");
   private static final ResourceLocation WOLF_TAME_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
   private static final ResourceLocation WOLF_ANGRY_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_angry.png");

   public WolfRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new WolfModel(), 0.5F);
      this.addLayer(new WolfCollarLayer(this));
   }

   protected float getBob(Wolf wolf, float var2) {
      return wolf.getTailAngle();
   }

   public void render(Wolf wolf, double var2, double var4, double var6, float var8, float var9) {
      if(wolf.isWet()) {
         float var10 = wolf.getBrightness() * wolf.getWetShade(var9);
         GlStateManager.color3f(var10, var10, var10);
      }

      super.render((Mob)wolf, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(Wolf wolf) {
      return wolf.isTame()?WOLF_TAME_LOCATION:(wolf.isAngry()?WOLF_ANGRY_LOCATION:WOLF_LOCATION);
   }

   // $FF: synthetic method
   protected float getBob(LivingEntity var1, float var2) {
      return this.getBob((Wolf)var1, var2);
   }
}
