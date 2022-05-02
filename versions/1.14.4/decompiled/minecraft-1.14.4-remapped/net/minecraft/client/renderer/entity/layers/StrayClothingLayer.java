package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

@ClientJarOnly
public class StrayClothingLayer extends RenderLayer {
   private static final ResourceLocation STRAY_CLOTHES_LOCATION = new ResourceLocation("textures/entity/skeleton/stray_overlay.png");
   private final SkeletonModel layerModel = new SkeletonModel(0.25F, true);

   public StrayClothingLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(Mob mob, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.getParentModel().copyPropertiesTo(this.layerModel);
      this.layerModel.prepareMobModel(mob, var2, var3, var4);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.bindTexture(STRAY_CLOTHES_LOCATION);
      this.layerModel.render(mob, var2, var3, var5, var6, var7, var8);
   }

   public boolean colorsOnDamage() {
      return true;
   }
}
