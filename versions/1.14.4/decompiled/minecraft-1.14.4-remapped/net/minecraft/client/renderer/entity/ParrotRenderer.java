package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Parrot;

@ClientJarOnly
public class ParrotRenderer extends MobRenderer {
   public static final ResourceLocation[] PARROT_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/parrot/parrot_red_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_green.png"), new ResourceLocation("textures/entity/parrot/parrot_yellow_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_grey.png")};

   public ParrotRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new ParrotModel(), 0.3F);
   }

   protected ResourceLocation getTextureLocation(Parrot parrot) {
      return PARROT_LOCATIONS[parrot.getVariant()];
   }

   public float getBob(Parrot parrot, float var2) {
      float var3 = Mth.lerp(var2, parrot.oFlap, parrot.flap);
      float var4 = Mth.lerp(var2, parrot.oFlapSpeed, parrot.flapSpeed);
      return (Mth.sin(var3) + 1.0F) * var4;
   }

   // $FF: synthetic method
   public float getBob(LivingEntity var1, float var2) {
      return this.getBob((Parrot)var1, var2);
   }
}
