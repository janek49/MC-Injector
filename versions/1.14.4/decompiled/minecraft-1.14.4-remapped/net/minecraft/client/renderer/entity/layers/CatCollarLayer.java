package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cat;

@ClientJarOnly
public class CatCollarLayer extends RenderLayer {
   private static final ResourceLocation CAT_COLLAR_LOCATION = new ResourceLocation("textures/entity/cat/cat_collar.png");
   private final CatModel catModel = new CatModel(0.01F);

   public CatCollarLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(Cat cat, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if(cat.isTame() && !cat.isInvisible()) {
         this.bindTexture(CAT_COLLAR_LOCATION);
         float[] vars9 = cat.getCollarColor().getTextureDiffuseColors();
         GlStateManager.color3f(vars9[0], vars9[1], vars9[2]);
         ((CatModel)this.getParentModel()).copyPropertiesTo(this.catModel);
         this.catModel.prepareMobModel(cat, var2, var3, var4);
         this.catModel.render(cat, var2, var3, var5, var6, var7, var8);
      }
   }

   public boolean colorsOnDamage() {
      return true;
   }
}
