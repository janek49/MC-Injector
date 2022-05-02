package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;

@ClientJarOnly
public class ChickenRenderer extends MobRenderer {
   private static final ResourceLocation CHICKEN_LOCATION = new ResourceLocation("textures/entity/chicken.png");

   public ChickenRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new ChickenModel(), 0.3F);
   }

   protected ResourceLocation getTextureLocation(Chicken chicken) {
      return CHICKEN_LOCATION;
   }

   protected float getBob(Chicken chicken, float var2) {
      float var3 = Mth.lerp(var2, chicken.oFlap, chicken.flap);
      float var4 = Mth.lerp(var2, chicken.oFlapSpeed, chicken.flapSpeed);
      return (Mth.sin(var3) + 1.0F) * var4;
   }

   // $FF: synthetic method
   protected float getBob(LivingEntity var1, float var2) {
      return this.getBob((Chicken)var1, var2);
   }
}
