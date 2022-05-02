package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.EndermiteModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Endermite;

@ClientJarOnly
public class EndermiteRenderer extends MobRenderer {
   private static final ResourceLocation ENDERMITE_LOCATION = new ResourceLocation("textures/entity/endermite.png");

   public EndermiteRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new EndermiteModel(), 0.3F);
   }

   protected float getFlipDegrees(Endermite endermite) {
      return 180.0F;
   }

   protected ResourceLocation getTextureLocation(Endermite endermite) {
      return ENDERMITE_LOCATION;
   }

   // $FF: synthetic method
   protected float getFlipDegrees(LivingEntity var1) {
      return this.getFlipDegrees((Endermite)var1);
   }

   // $FF: synthetic method
   protected ResourceLocation getTextureLocation(Entity var1) {
      return this.getTextureLocation((Endermite)var1);
   }
}
