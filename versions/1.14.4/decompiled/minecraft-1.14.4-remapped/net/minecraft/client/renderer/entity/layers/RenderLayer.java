package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

@ClientJarOnly
public abstract class RenderLayer {
   private final RenderLayerParent renderer;

   public RenderLayer(RenderLayerParent renderer) {
      this.renderer = renderer;
   }

   public EntityModel getParentModel() {
      return this.renderer.getModel();
   }

   public void bindTexture(ResourceLocation resourceLocation) {
      this.renderer.bindTexture(resourceLocation);
   }

   public void setLightColor(Entity lightColor) {
      this.renderer.setLightColor(lightColor);
   }

   public abstract void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8);

   public abstract boolean colorsOnDamage();
}
