package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Slime;

@ClientJarOnly
public class SlimeRenderer extends MobRenderer {
   private static final ResourceLocation SLIME_LOCATION = new ResourceLocation("textures/entity/slime/slime.png");

   public SlimeRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new SlimeModel(16), 0.25F);
      this.addLayer(new SlimeOuterLayer(this));
   }

   public void render(Slime slime, double var2, double var4, double var6, float var8, float var9) {
      this.shadowRadius = 0.25F * (float)slime.getSize();
      super.render((Mob)slime, var2, var4, var6, var8, var9);
   }

   protected void scale(Slime slime, float var2) {
      float var3 = 0.999F;
      GlStateManager.scalef(0.999F, 0.999F, 0.999F);
      float var4 = (float)slime.getSize();
      float var5 = Mth.lerp(var2, slime.oSquish, slime.squish) / (var4 * 0.5F + 1.0F);
      float var6 = 1.0F / (var5 + 1.0F);
      GlStateManager.scalef(var6 * var4, 1.0F / var6 * var4, var6 * var4);
   }

   protected ResourceLocation getTextureLocation(Slime slime) {
      return SLIME_LOCATION;
   }
}
