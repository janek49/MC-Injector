package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;

@ClientJarOnly
public class SilverfishRenderer extends MobRenderer {
   private static final ResourceLocation SILVERFISH_LOCATION = new ResourceLocation("textures/entity/silverfish.png");

   public SilverfishRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new SilverfishModel(), 0.3F);
   }

   protected float getFlipDegrees(Silverfish silverfish) {
      return 180.0F;
   }

   protected ResourceLocation getTextureLocation(Silverfish silverfish) {
      return SILVERFISH_LOCATION;
   }

   // $FF: synthetic method
   protected float getFlipDegrees(LivingEntity var1) {
      return this.getFlipDegrees((Silverfish)var1);
   }

   // $FF: synthetic method
   protected ResourceLocation getTextureLocation(Entity var1) {
      return this.getTextureLocation((Silverfish)var1);
   }
}
