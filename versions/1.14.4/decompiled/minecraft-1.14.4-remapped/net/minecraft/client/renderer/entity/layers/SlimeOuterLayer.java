package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;

@ClientJarOnly
public class SlimeOuterLayer extends RenderLayer {
   private final EntityModel model = new SlimeModel(0);

   public SlimeOuterLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(Entity entity, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if(!entity.isInvisible()) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableNormalize();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         ((SlimeModel)this.getParentModel()).copyPropertiesTo(this.model);
         this.model.render(entity, var2, var3, var5, var6, var7, var8);
         GlStateManager.disableBlend();
         GlStateManager.disableNormalize();
      }
   }

   public boolean colorsOnDamage() {
      return true;
   }
}
