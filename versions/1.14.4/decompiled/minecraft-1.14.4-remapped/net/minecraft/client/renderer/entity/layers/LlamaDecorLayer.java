package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.item.DyeColor;

@ClientJarOnly
public class LlamaDecorLayer extends RenderLayer {
   private static final ResourceLocation[] TEXTURE_LOCATION = new ResourceLocation[]{new ResourceLocation("textures/entity/llama/decor/white.png"), new ResourceLocation("textures/entity/llama/decor/orange.png"), new ResourceLocation("textures/entity/llama/decor/magenta.png"), new ResourceLocation("textures/entity/llama/decor/light_blue.png"), new ResourceLocation("textures/entity/llama/decor/yellow.png"), new ResourceLocation("textures/entity/llama/decor/lime.png"), new ResourceLocation("textures/entity/llama/decor/pink.png"), new ResourceLocation("textures/entity/llama/decor/gray.png"), new ResourceLocation("textures/entity/llama/decor/light_gray.png"), new ResourceLocation("textures/entity/llama/decor/cyan.png"), new ResourceLocation("textures/entity/llama/decor/purple.png"), new ResourceLocation("textures/entity/llama/decor/blue.png"), new ResourceLocation("textures/entity/llama/decor/brown.png"), new ResourceLocation("textures/entity/llama/decor/green.png"), new ResourceLocation("textures/entity/llama/decor/red.png"), new ResourceLocation("textures/entity/llama/decor/black.png")};
   private static final ResourceLocation TRADER_LLAMA = new ResourceLocation("textures/entity/llama/decor/trader_llama.png");
   private final LlamaModel model = new LlamaModel(0.5F);

   public LlamaDecorLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(Llama llama, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      DyeColor var9 = llama.getSwag();
      if(var9 != null) {
         this.bindTexture(TEXTURE_LOCATION[var9.getId()]);
      } else {
         if(!llama.isTraderLlama()) {
            return;
         }

         this.bindTexture(TRADER_LLAMA);
      }

      ((LlamaModel)this.getParentModel()).copyPropertiesTo(this.model);
      this.model.render((AbstractChestedHorse)llama, var2, var3, var5, var6, var7, var8);
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
