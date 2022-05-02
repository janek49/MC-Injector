package net.minecraft.client.renderer.texture;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

@ClientJarOnly
public interface TextureObject {
   void pushFilter(boolean var1, boolean var2);

   void popFilter();

   void load(ResourceManager var1) throws IOException;

   int getId();

   default void bind() {
      GlStateManager.bindTexture(this.getId());
   }

   default void reset(TextureManager textureManager, ResourceManager resourceManager, ResourceLocation resourceLocation, Executor executor) {
      textureManager.register(resourceLocation, this);
   }
}
