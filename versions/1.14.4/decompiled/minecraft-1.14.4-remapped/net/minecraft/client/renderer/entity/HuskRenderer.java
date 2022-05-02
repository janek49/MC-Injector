package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

@ClientJarOnly
public class HuskRenderer extends ZombieRenderer {
   private static final ResourceLocation HUSK_LOCATION = new ResourceLocation("textures/entity/zombie/husk.png");

   public HuskRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
   }

   protected void scale(Zombie zombie, float var2) {
      float var3 = 1.0625F;
      GlStateManager.scalef(1.0625F, 1.0625F, 1.0625F);
      super.scale(zombie, var2);
   }

   protected ResourceLocation getTextureLocation(Zombie zombie) {
      return HUSK_LOCATION;
   }
}
