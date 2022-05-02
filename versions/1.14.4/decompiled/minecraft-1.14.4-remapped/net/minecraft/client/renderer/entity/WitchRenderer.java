package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WitchItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Witch;

@ClientJarOnly
public class WitchRenderer extends MobRenderer {
   private static final ResourceLocation WITCH_LOCATION = new ResourceLocation("textures/entity/witch.png");

   public WitchRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new WitchModel(0.0F), 0.5F);
      this.addLayer(new WitchItemLayer(this));
   }

   public void render(Witch witch, double var2, double var4, double var6, float var8, float var9) {
      ((WitchModel)this.model).setHoldingItem(!witch.getMainHandItem().isEmpty());
      super.render((Mob)witch, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(Witch witch) {
      return WITCH_LOCATION;
   }

   protected void scale(Witch witch, float var2) {
      float var3 = 0.9375F;
      GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
   }
}
