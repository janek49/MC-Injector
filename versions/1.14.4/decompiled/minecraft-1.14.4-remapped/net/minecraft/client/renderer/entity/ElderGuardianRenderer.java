package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.GuardianRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;

@ClientJarOnly
public class ElderGuardianRenderer extends GuardianRenderer {
   private static final ResourceLocation GUARDIAN_ELDER_LOCATION = new ResourceLocation("textures/entity/guardian_elder.png");

   public ElderGuardianRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, 1.2F);
   }

   protected void scale(Guardian guardian, float var2) {
      GlStateManager.scalef(ElderGuardian.ELDER_SIZE_SCALE, ElderGuardian.ELDER_SIZE_SCALE, ElderGuardian.ELDER_SIZE_SCALE);
   }

   protected ResourceLocation getTextureLocation(Guardian guardian) {
      return GUARDIAN_ELDER_LOCATION;
   }
}
