package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.PhantomEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Phantom;

@ClientJarOnly
public class PhantomRenderer extends MobRenderer {
   private static final ResourceLocation PHANTOM_LOCATION = new ResourceLocation("textures/entity/phantom.png");

   public PhantomRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new PhantomModel(), 0.75F);
      this.addLayer(new PhantomEyesLayer(this));
   }

   protected ResourceLocation getTextureLocation(Phantom phantom) {
      return PHANTOM_LOCATION;
   }

   protected void scale(Phantom phantom, float var2) {
      int var3 = phantom.getPhantomSize();
      float var4 = 1.0F + 0.15F * (float)var3;
      GlStateManager.scalef(var4, var4, var4);
      GlStateManager.translatef(0.0F, 1.3125F, 0.1875F);
   }

   protected void setupRotations(Phantom phantom, float var2, float var3, float var4) {
      super.setupRotations(phantom, var2, var3, var4);
      GlStateManager.rotatef(phantom.xRot, 1.0F, 0.0F, 0.0F);
   }
}
