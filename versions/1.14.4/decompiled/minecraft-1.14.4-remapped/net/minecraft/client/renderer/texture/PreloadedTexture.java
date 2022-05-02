package net.minecraft.client.renderer.texture;

import com.fox2code.repacker.ClientJarOnly;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

@ClientJarOnly
public class PreloadedTexture extends SimpleTexture {
   private CompletableFuture future;

   public PreloadedTexture(ResourceManager resourceManager, ResourceLocation resourceLocation, Executor executor) {
      super(resourceLocation);
      this.future = CompletableFuture.supplyAsync(() -> {
         return SimpleTexture.TextureImage.load(resourceManager, resourceLocation);
      }, executor);
   }

   protected SimpleTexture.TextureImage getTextureImage(ResourceManager resourceManager) {
      if(this.future != null) {
         SimpleTexture.TextureImage simpleTexture$TextureImage = (SimpleTexture.TextureImage)this.future.join();
         this.future = null;
         return simpleTexture$TextureImage;
      } else {
         return SimpleTexture.TextureImage.load(resourceManager, this.location);
      }
   }

   public CompletableFuture getFuture() {
      return this.future == null?CompletableFuture.completedFuture((Object)null):this.future.thenApply((simpleTexture$TextureImage) -> {
         return null;
      });
   }

   public void reset(TextureManager textureManager, ResourceManager resourceManager, ResourceLocation resourceLocation, Executor executor) {
      this.future = CompletableFuture.supplyAsync(() -> {
         return SimpleTexture.TextureImage.load(resourceManager, this.location);
      }, Util.backgroundExecutor());
      this.future.thenRunAsync(() -> {
         textureManager.register((ResourceLocation)this.location, (TextureObject)this);
      }, executor);
   }
}
