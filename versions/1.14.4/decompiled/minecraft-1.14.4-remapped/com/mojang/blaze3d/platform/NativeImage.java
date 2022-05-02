package com.mojang.blaze3d.platform;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.DebugMemoryUntracker;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@ClientJarOnly
public final class NativeImage implements AutoCloseable {
   private static final Set OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
   private final NativeImage.Format format;
   private final int width;
   private final int height;
   private final boolean useStbFree;
   private long pixels;
   private final int size;

   public NativeImage(int var1, int var2, boolean var3) {
      this(NativeImage.Format.RGBA, var1, var2, var3);
   }

   public NativeImage(NativeImage.Format format, int width, int height, boolean var4) {
      this.format = format;
      this.width = width;
      this.height = height;
      this.size = width * height * format.components();
      this.useStbFree = false;
      if(var4) {
         this.pixels = MemoryUtil.nmemCalloc(1L, (long)this.size);
      } else {
         this.pixels = MemoryUtil.nmemAlloc((long)this.size);
      }

   }

   private NativeImage(NativeImage.Format format, int width, int height, boolean useStbFree, long pixels) {
      this.format = format;
      this.width = width;
      this.height = height;
      this.useStbFree = useStbFree;
      this.pixels = pixels;
      this.size = width * height * format.components();
   }

   public String toString() {
      return "NativeImage[" + this.format + " " + this.width + "x" + this.height + "@" + this.pixels + (this.useStbFree?"S":"N") + "]";
   }

   public static NativeImage read(InputStream inputStream) throws IOException {
      return read(NativeImage.Format.RGBA, inputStream);
   }

   public static NativeImage read(@Nullable NativeImage.Format nativeImage$Format, InputStream inputStream) throws IOException {
      ByteBuffer var2 = null;

      NativeImage var3;
      try {
         var2 = TextureUtil.readResource(inputStream);
         var2.rewind();
         var3 = read(nativeImage$Format, var2);
      } finally {
         MemoryUtil.memFree(var2);
         IOUtils.closeQuietly(inputStream);
      }

      return var3;
   }

   public static NativeImage read(ByteBuffer byteBuffer) throws IOException {
      return read(NativeImage.Format.RGBA, byteBuffer);
   }

   public static NativeImage read(@Nullable NativeImage.Format nativeImage$Format, ByteBuffer byteBuffer) throws IOException {
      if(nativeImage$Format != null && !nativeImage$Format.supportedByStb()) {
         throw new UnsupportedOperationException("Don\'t know how to read format " + nativeImage$Format);
      } else if(MemoryUtil.memAddress(byteBuffer) == 0L) {
         throw new IllegalArgumentException("Invalid buffer");
      } else {
         MemoryStack var2 = MemoryStack.stackPush();
         Throwable var3 = null;

         NativeImage var8;
         try {
            IntBuffer var4 = var2.mallocInt(1);
            IntBuffer var5 = var2.mallocInt(1);
            IntBuffer var6 = var2.mallocInt(1);
            ByteBuffer var7 = STBImage.stbi_load_from_memory(byteBuffer, var4, var5, var6, nativeImage$Format == null?0:nativeImage$Format.components);
            if(var7 == null) {
               throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }

            var8 = new NativeImage(nativeImage$Format == null?NativeImage.Format.getStbFormat(var6.get(0)):nativeImage$Format, var4.get(0), var5.get(0), true, MemoryUtil.memAddress(var7));
         } catch (Throwable var17) {
            var3 = var17;
            throw var17;
         } finally {
            if(var2 != null) {
               if(var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var16) {
                     var3.addSuppressed(var16);
                  }
               } else {
                  var2.close();
               }
            }

         }

         return var8;
      }
   }

   private static void setClamp(boolean clamp) {
      if(clamp) {
         GlStateManager.texParameter(3553, 10242, 10496);
         GlStateManager.texParameter(3553, 10243, 10496);
      } else {
         GlStateManager.texParameter(3553, 10242, 10497);
         GlStateManager.texParameter(3553, 10243, 10497);
      }

   }

   private static void setFilter(boolean var0, boolean var1) {
      if(var0) {
         GlStateManager.texParameter(3553, 10241, var1?9987:9729);
         GlStateManager.texParameter(3553, 10240, 9729);
      } else {
         GlStateManager.texParameter(3553, 10241, var1?9986:9728);
         GlStateManager.texParameter(3553, 10240, 9728);
      }

   }

   private void checkAllocated() {
      if(this.pixels == 0L) {
         throw new IllegalStateException("Image is not allocated.");
      }
   }

   public void close() {
      if(this.pixels != 0L) {
         if(this.useStbFree) {
            STBImage.nstbi_image_free(this.pixels);
         } else {
            MemoryUtil.nmemFree(this.pixels);
         }
      }

      this.pixels = 0L;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public NativeImage.Format format() {
      return this.format;
   }

   public int getPixelRGBA(int var1, int var2) {
      if(this.format != NativeImage.Format.RGBA) {
         throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
      } else if(var1 <= this.width && var2 <= this.height) {
         this.checkAllocated();
         return MemoryUtil.memIntBuffer(this.pixels, this.size).get(var1 + var2 * this.width);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", new Object[]{Integer.valueOf(var1), Integer.valueOf(var2), Integer.valueOf(this.width), Integer.valueOf(this.height)}));
      }
   }

   public void setPixelRGBA(int var1, int var2, int var3) {
      if(this.format != NativeImage.Format.RGBA) {
         throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
      } else if(var1 <= this.width && var2 <= this.height) {
         this.checkAllocated();
         MemoryUtil.memIntBuffer(this.pixels, this.size).put(var1 + var2 * this.width, var3);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", new Object[]{Integer.valueOf(var1), Integer.valueOf(var2), Integer.valueOf(this.width), Integer.valueOf(this.height)}));
      }
   }

   public byte getLuminanceOrAlpha(int var1, int var2) {
      if(!this.format.hasLuminanceOrAlpha()) {
         throw new IllegalArgumentException(String.format("no luminance or alpha in %s", new Object[]{this.format}));
      } else if(var1 <= this.width && var2 <= this.height) {
         return MemoryUtil.memByteBuffer(this.pixels, this.size).get((var1 + var2 * this.width) * this.format.components() + this.format.luminanceOrAlphaOffset() / 8);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", new Object[]{Integer.valueOf(var1), Integer.valueOf(var2), Integer.valueOf(this.width), Integer.valueOf(this.height)}));
      }
   }

   public void blendPixel(int var1, int var2, int var3) {
      if(this.format != NativeImage.Format.RGBA) {
         throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
      } else {
         int var4 = this.getPixelRGBA(var1, var2);
         float var5 = (float)(var3 >> 24 & 255) / 255.0F;
         float var6 = (float)(var3 >> 16 & 255) / 255.0F;
         float var7 = (float)(var3 >> 8 & 255) / 255.0F;
         float var8 = (float)(var3 >> 0 & 255) / 255.0F;
         float var9 = (float)(var4 >> 24 & 255) / 255.0F;
         float var10 = (float)(var4 >> 16 & 255) / 255.0F;
         float var11 = (float)(var4 >> 8 & 255) / 255.0F;
         float var12 = (float)(var4 >> 0 & 255) / 255.0F;
         float var14 = 1.0F - var5;
         float var15 = var5 * var5 + var9 * var14;
         float var16 = var6 * var5 + var10 * var14;
         float var17 = var7 * var5 + var11 * var14;
         float var18 = var8 * var5 + var12 * var14;
         if(var15 > 1.0F) {
            var15 = 1.0F;
         }

         if(var16 > 1.0F) {
            var16 = 1.0F;
         }

         if(var17 > 1.0F) {
            var17 = 1.0F;
         }

         if(var18 > 1.0F) {
            var18 = 1.0F;
         }

         int var19 = (int)(var15 * 255.0F);
         int var20 = (int)(var16 * 255.0F);
         int var21 = (int)(var17 * 255.0F);
         int var22 = (int)(var18 * 255.0F);
         this.setPixelRGBA(var1, var2, var19 << 24 | var20 << 16 | var21 << 8 | var22 << 0);
      }
   }

   @Deprecated
   public int[] makePixelArray() {
      if(this.format != NativeImage.Format.RGBA) {
         throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
      } else {
         this.checkAllocated();
         int[] ints = new int[this.getWidth() * this.getHeight()];

         for(int var2 = 0; var2 < this.getHeight(); ++var2) {
            for(int var3 = 0; var3 < this.getWidth(); ++var3) {
               int var4 = this.getPixelRGBA(var3, var2);
               int var5 = var4 >> 24 & 255;
               int var6 = var4 >> 16 & 255;
               int var7 = var4 >> 8 & 255;
               int var8 = var4 >> 0 & 255;
               int var9 = var5 << 24 | var8 << 16 | var7 << 8 | var6;
               ints[var3 + var2 * this.getWidth()] = var9;
            }
         }

         return ints;
      }
   }

   public void upload(int var1, int var2, int var3, boolean var4) {
      this.upload(var1, var2, var3, 0, 0, this.width, this.height, var4);
   }

   public void upload(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      this.upload(var1, var2, var3, var4, var5, var6, var7, false, false, var8);
   }

   public void upload(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9, boolean var10) {
      this.checkAllocated();
      setFilter(var8, var10);
      setClamp(var9);
      if(var6 == this.getWidth()) {
         GlStateManager.pixelStore(3314, 0);
      } else {
         GlStateManager.pixelStore(3314, this.getWidth());
      }

      GlStateManager.pixelStore(3316, var4);
      GlStateManager.pixelStore(3315, var5);
      this.format.setUnpackPixelStoreState();
      GlStateManager.texSubImage2D(3553, var1, var2, var3, var6, var7, this.format.glFormat(), 5121, this.pixels);
   }

   public void downloadTexture(int var1, boolean var2) {
      this.checkAllocated();
      this.format.setPackPixelStoreState();
      GlStateManager.getTexImage(3553, var1, this.format.glFormat(), 5121, this.pixels);
      if(var2 && this.format.hasAlpha()) {
         for(int var3 = 0; var3 < this.getHeight(); ++var3) {
            for(int var4 = 0; var4 < this.getWidth(); ++var4) {
               this.setPixelRGBA(var4, var3, this.getPixelRGBA(var4, var3) | 255 << this.format.alphaOffset());
            }
         }
      }

   }

   public void downloadFrameBuffer(boolean b) {
      this.checkAllocated();
      this.format.setPackPixelStoreState();
      if(b) {
         GlStateManager.pixelTransfer(3357, Float.MAX_VALUE);
      }

      GlStateManager.readPixels(0, 0, this.width, this.height, this.format.glFormat(), 5121, this.pixels);
      if(b) {
         GlStateManager.pixelTransfer(3357, 0.0F);
      }

   }

   public void writeToFile(String string) throws IOException {
      this.writeToFile(FileSystems.getDefault().getPath(string, new String[0]));
   }

   public void writeToFile(File file) throws IOException {
      this.writeToFile(file.toPath());
   }

   public void copyFromFont(STBTTFontinfo sTBTTFontinfo, int var2, int var3, int var4, float var5, float var6, float var7, float var8, int var9, int var10) {
      if(var9 >= 0 && var9 + var3 <= this.getWidth() && var10 >= 0 && var10 + var4 <= this.getHeight()) {
         if(this.format.components() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
         } else {
            STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(sTBTTFontinfo.address(), this.pixels + (long)var9 + (long)(var10 * this.getWidth()), var3, var4, this.getWidth(), var5, var6, var7, var8, var2);
         }
      } else {
         throw new IllegalArgumentException(String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", new Object[]{Integer.valueOf(var9), Integer.valueOf(var10), Integer.valueOf(var3), Integer.valueOf(var4), Integer.valueOf(this.getWidth()), Integer.valueOf(this.getHeight())}));
      }
   }

   public void writeToFile(Path path) throws IOException {
      if(!this.format.supportedByStb()) {
         throw new UnsupportedOperationException("Don\'t know how to write format " + this.format);
      } else {
         this.checkAllocated();
         WritableByteChannel var2 = Files.newByteChannel(path, OPEN_OPTIONS, new FileAttribute[0]);
         Throwable var3 = null;

         try {
            NativeImage.WriteCallback var4 = new NativeImage.WriteCallback(var2);

            try {
               if(!STBImageWrite.stbi_write_png_to_func(var4, 0L, this.getWidth(), this.getHeight(), this.format.components(), MemoryUtil.memByteBuffer(this.pixels, this.size), 0)) {
                  throw new IOException("Could not write image to the PNG file \"" + path.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
               }
            } finally {
               var4.free();
            }

            var4.throwIfException();
         } catch (Throwable var19) {
            var3 = var19;
            throw var19;
         } finally {
            if(var2 != null) {
               if(var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var17) {
                     var3.addSuppressed(var17);
                  }
               } else {
                  var2.close();
               }
            }

         }

      }
   }

   public void copyFrom(NativeImage nativeImage) {
      if(nativeImage.format() != this.format) {
         throw new UnsupportedOperationException("Image formats don\'t match.");
      } else {
         int var2 = this.format.components();
         this.checkAllocated();
         nativeImage.checkAllocated();
         if(this.width == nativeImage.width) {
            MemoryUtil.memCopy(nativeImage.pixels, this.pixels, (long)Math.min(this.size, nativeImage.size));
         } else {
            int var3 = Math.min(this.getWidth(), nativeImage.getWidth());
            int var4 = Math.min(this.getHeight(), nativeImage.getHeight());

            for(int var5 = 0; var5 < var4; ++var5) {
               int var6 = var5 * nativeImage.getWidth() * var2;
               int var7 = var5 * this.getWidth() * var2;
               MemoryUtil.memCopy(nativeImage.pixels + (long)var6, this.pixels + (long)var7, (long)var3);
            }
         }

      }
   }

   public void fillRect(int var1, int var2, int var3, int var4, int var5) {
      for(int var6 = var2; var6 < var2 + var4; ++var6) {
         for(int var7 = var1; var7 < var1 + var3; ++var7) {
            this.setPixelRGBA(var7, var6, var5);
         }
      }

   }

   public void copyRect(int var1, int var2, int var3, int var4, int var5, int var6, boolean var7, boolean var8) {
      for(int var9 = 0; var9 < var6; ++var9) {
         for(int var10 = 0; var10 < var5; ++var10) {
            int var11 = var7?var5 - 1 - var10:var10;
            int var12 = var8?var6 - 1 - var9:var9;
            int var13 = this.getPixelRGBA(var1 + var10, var2 + var9);
            this.setPixelRGBA(var1 + var3 + var11, var2 + var4 + var12, var13);
         }
      }

   }

   public void flipY() {
      this.checkAllocated();
      MemoryStack var1 = MemoryStack.stackPush();
      Throwable var2 = null;

      try {
         int var3 = this.format.components();
         int var4 = this.getWidth() * var3;
         long var5 = var1.nmalloc(var4);

         for(int var7 = 0; var7 < this.getHeight() / 2; ++var7) {
            int var8 = var7 * this.getWidth() * var3;
            int var9 = (this.getHeight() - 1 - var7) * this.getWidth() * var3;
            MemoryUtil.memCopy(this.pixels + (long)var8, var5, (long)var4);
            MemoryUtil.memCopy(this.pixels + (long)var9, this.pixels + (long)var8, (long)var4);
            MemoryUtil.memCopy(var5, this.pixels + (long)var9, (long)var4);
         }
      } catch (Throwable var17) {
         var2 = var17;
         throw var17;
      } finally {
         if(var1 != null) {
            if(var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var16) {
                  var2.addSuppressed(var16);
               }
            } else {
               var1.close();
            }
         }

      }

   }

   public void resizeSubRectTo(int var1, int var2, int var3, int var4, NativeImage nativeImage) {
      this.checkAllocated();
      if(nativeImage.format() != this.format) {
         throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
      } else {
         int var6 = this.format.components();
         STBImageResize.nstbir_resize_uint8(this.pixels + (long)((var1 + var2 * this.getWidth()) * var6), var3, var4, this.getWidth() * var6, nativeImage.pixels, nativeImage.getWidth(), nativeImage.getHeight(), 0, var6);
      }
   }

   public void untrack() {
      DebugMemoryUntracker.untrack(this.pixels);
   }

   public static NativeImage fromBase64(String base64) throws IOException {
      MemoryStack var1 = MemoryStack.stackPush();
      Throwable var2 = null;

      NativeImage var6;
      try {
         ByteBuffer var3 = var1.UTF8(base64.replaceAll("\n", ""), false);
         ByteBuffer var4 = Base64.getDecoder().decode(var3);
         ByteBuffer var5 = var1.malloc(var4.remaining());
         var5.put(var4);
         var5.rewind();
         var6 = read(var5);
      } catch (Throwable var15) {
         var2 = var15;
         throw var15;
      } finally {
         if(var1 != null) {
            if(var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var14) {
                  var2.addSuppressed(var14);
               }
            } else {
               var1.close();
            }
         }

      }

      return var6;
   }

   @ClientJarOnly
   public static enum Format {
      RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
      RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
      LUMINANCE_ALPHA(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
      LUMINANCE(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);

      private final int components;
      private final int glFormat;
      private final boolean hasRed;
      private final boolean hasGreen;
      private final boolean hasBlue;
      private final boolean hasLuminance;
      private final boolean hasAlpha;
      private final int redOffset;
      private final int greenOffset;
      private final int blueOffset;
      private final int luminanceOffset;
      private final int alphaOffset;
      private final boolean supportedByStb;

      private Format(int components, int glFormat, boolean hasRed, boolean hasGreen, boolean hasBlue, boolean hasLuminance, boolean hasAlpha, int redOffset, int greenOffset, int blueOffset, int luminanceOffset, int alphaOffset, boolean supportedByStb) {
         this.components = components;
         this.glFormat = glFormat;
         this.hasRed = hasRed;
         this.hasGreen = hasGreen;
         this.hasBlue = hasBlue;
         this.hasLuminance = hasLuminance;
         this.hasAlpha = hasAlpha;
         this.redOffset = redOffset;
         this.greenOffset = greenOffset;
         this.blueOffset = blueOffset;
         this.luminanceOffset = luminanceOffset;
         this.alphaOffset = alphaOffset;
         this.supportedByStb = supportedByStb;
      }

      public int components() {
         return this.components;
      }

      public void setPackPixelStoreState() {
         GlStateManager.pixelStore(3333, this.components());
      }

      public void setUnpackPixelStoreState() {
         GlStateManager.pixelStore(3317, this.components());
      }

      public int glFormat() {
         return this.glFormat;
      }

      public boolean hasAlpha() {
         return this.hasAlpha;
      }

      public int alphaOffset() {
         return this.alphaOffset;
      }

      public boolean hasLuminanceOrAlpha() {
         return this.hasLuminance || this.hasAlpha;
      }

      public int luminanceOrAlphaOffset() {
         return this.hasLuminance?this.luminanceOffset:this.alphaOffset;
      }

      public boolean supportedByStb() {
         return this.supportedByStb;
      }

      private static NativeImage.Format getStbFormat(int i) {
         switch(i) {
         case 1:
            return LUMINANCE;
         case 2:
            return LUMINANCE_ALPHA;
         case 3:
            return RGB;
         case 4:
         default:
            return RGBA;
         }
      }
   }

   @ClientJarOnly
   public static enum InternalGlFormat {
      RGBA(6408),
      RGB(6407),
      LUMINANCE_ALPHA(6410),
      LUMINANCE(6409),
      INTENSITY('聉');

      private final int glFormat;

      private InternalGlFormat(int glFormat) {
         this.glFormat = glFormat;
      }

      public int glFormat() {
         return this.glFormat;
      }
   }

   @ClientJarOnly
   static class WriteCallback extends STBIWriteCallback {
      private final WritableByteChannel output;
      private IOException exception;

      private WriteCallback(WritableByteChannel output) {
         this.output = output;
      }

      public void invoke(long var1, long var3, int var5) {
         ByteBuffer var6 = getData(var3, var5);

         try {
            this.output.write(var6);
         } catch (IOException var8) {
            this.exception = var8;
         }

      }

      public void throwIfException() throws IOException {
         if(this.exception != null) {
            throw this.exception;
         }
      }
   }
}
