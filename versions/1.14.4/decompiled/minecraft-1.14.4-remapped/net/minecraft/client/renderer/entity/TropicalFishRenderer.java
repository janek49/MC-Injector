package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.TropicalFish;

@ClientJarOnly
public class TropicalFishRenderer extends MobRenderer {
   private final TropicalFishModelA modelA = new TropicalFishModelA();
   private final TropicalFishModelB modelB = new TropicalFishModelB();

   public TropicalFishRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new TropicalFishModelA(), 0.15F);
      this.addLayer(new TropicalFishPatternLayer(this));
   }

   @Nullable
   protected ResourceLocation getTextureLocation(TropicalFish tropicalFish) {
      return tropicalFish.getBaseTextureLocation();
   }

   public void render(TropicalFish tropicalFish, double var2, double var4, double var6, float var8, float var9) {
      this.model = (EntityModel)(tropicalFish.getBaseVariant() == 0?this.modelA:this.modelB);
      float[] vars10 = tropicalFish.getBaseColor();
      GlStateManager.color3f(vars10[0], vars10[1], vars10[2]);
      super.render((Mob)tropicalFish, var2, var4, var6, var8, var9);
   }

   protected void setupRotations(TropicalFish tropicalFish, float var2, float var3, float var4) {
      super.setupRotations(tropicalFish, var2, var3, var4);
      float var5 = 4.3F * Mth.sin(0.6F * var2);
      GlStateManager.rotatef(var5, 0.0F, 1.0F, 0.0F);
      if(!tropicalFish.isInWater()) {
         GlStateManager.translatef(0.2F, 0.1F, 0.0F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
      }

   }
}
