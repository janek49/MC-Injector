package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;

@ClientJarOnly
public class WolfCollarLayer extends RenderLayer {
   private static final ResourceLocation WOLF_COLLAR_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_collar.png");

   public WolfCollarLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(Wolf wolf, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if(wolf.isTame() && !wolf.isInvisible()) {
         this.bindTexture(WOLF_COLLAR_LOCATION);
         float[] vars9 = wolf.getCollarColor().getTextureDiffuseColors();
         GlStateManager.color3f(vars9[0], vars9[1], vars9[2]);
         ((WolfModel)this.getParentModel()).render(wolf, var2, var3, var5, var6, var7, var8);
      }
   }

   public boolean colorsOnDamage() {
      return true;
   }
}
