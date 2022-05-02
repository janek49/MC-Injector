package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;

@ClientJarOnly
public class PigSaddleLayer extends RenderLayer {
   private static final ResourceLocation SADDLE_LOCATION = new ResourceLocation("textures/entity/pig/pig_saddle.png");
   private final PigModel model = new PigModel(0.5F);

   public PigSaddleLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(Pig pig, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if(pig.hasSaddle()) {
         this.bindTexture(SADDLE_LOCATION);
         ((PigModel)this.getParentModel()).copyPropertiesTo(this.model);
         this.model.render(pig, var2, var3, var5, var6, var7, var8);
      }
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
