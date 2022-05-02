package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.animal.TropicalFish;

@ClientJarOnly
public class TropicalFishPatternLayer extends RenderLayer {
   private final TropicalFishModelA modelA = new TropicalFishModelA(0.008F);
   private final TropicalFishModelB modelB = new TropicalFishModelB(0.008F);

   public TropicalFishPatternLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(TropicalFish tropicalFish, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if(!tropicalFish.isInvisible()) {
         EntityModel<TropicalFish> var9 = (EntityModel)(tropicalFish.getBaseVariant() == 0?this.modelA:this.modelB);
         this.bindTexture(tropicalFish.getPatternTextureLocation());
         float[] vars10 = tropicalFish.getPatternColor();
         GlStateManager.color3f(vars10[0], vars10[1], vars10[2]);
         this.getParentModel().copyPropertiesTo(var9);
         var9.prepareMobModel(tropicalFish, var2, var3, var4);
         var9.render(tropicalFish, var2, var3, var5, var6, var7, var8);
      }
   }

   public boolean colorsOnDamage() {
      return true;
   }
}
