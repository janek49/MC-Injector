package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LlamaDecorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Llama;

@ClientJarOnly
public class LlamaRenderer extends MobRenderer {
   private static final ResourceLocation[] LLAMA_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/llama/creamy.png"), new ResourceLocation("textures/entity/llama/white.png"), new ResourceLocation("textures/entity/llama/brown.png"), new ResourceLocation("textures/entity/llama/gray.png")};

   public LlamaRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new LlamaModel(0.0F), 0.7F);
      this.addLayer(new LlamaDecorLayer(this));
   }

   protected ResourceLocation getTextureLocation(Llama llama) {
      return LLAMA_LOCATIONS[llama.getVariant()];
   }
}
