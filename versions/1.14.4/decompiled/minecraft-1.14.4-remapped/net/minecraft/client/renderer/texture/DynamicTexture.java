package net.minecraft.client.renderer.texture;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;

@ClientJarOnly
public class DynamicTexture extends AbstractTexture implements AutoCloseable {
   private NativeImage pixels;

   public DynamicTexture(NativeImage pixels) {
      this.pixels = pixels;
      TextureUtil.prepareImage(this.getId(), this.pixels.getWidth(), this.pixels.getHeight());
      this.upload();
   }

   public DynamicTexture(int var1, int var2, boolean var3) {
      this.pixels = new NativeImage(var1, var2, var3);
      TextureUtil.prepareImage(this.getId(), this.pixels.getWidth(), this.pixels.getHeight());
   }

   public void load(ResourceManager resourceManager) throws IOException {
   }

   public void upload() {
      this.bind();
      this.pixels.upload(0, 0, 0, false);
   }

   @Nullable
   public NativeImage getPixels() {
      return this.pixels;
   }

   public void setPixels(NativeImage pixels) throws Exception {
      this.pixels.close();
      this.pixels = pixels;
   }

   public void close() {
      this.pixels.close();
      this.releaseId();
      this.pixels = null;
   }
}
