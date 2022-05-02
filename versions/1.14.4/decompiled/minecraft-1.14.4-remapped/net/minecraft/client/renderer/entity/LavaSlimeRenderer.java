package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.LavaSlimeModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.MagmaCube;

@ClientJarOnly
public class LavaSlimeRenderer extends MobRenderer {
   private static final ResourceLocation MAGMACUBE_LOCATION = new ResourceLocation("textures/entity/slime/magmacube.png");

   public LavaSlimeRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new LavaSlimeModel(), 0.25F);
   }

   protected ResourceLocation getTextureLocation(MagmaCube magmaCube) {
      return MAGMACUBE_LOCATION;
   }

   protected void scale(MagmaCube magmaCube, float var2) {
      int var3 = magmaCube.getSize();
      float var4 = Mth.lerp(var2, magmaCube.oSquish, magmaCube.squish) / ((float)var3 * 0.5F + 1.0F);
      float var5 = 1.0F / (var4 + 1.0F);
      GlStateManager.scalef(var5 * (float)var3, 1.0F / var5 * (float)var3, var5 * (float)var3);
   }
}
