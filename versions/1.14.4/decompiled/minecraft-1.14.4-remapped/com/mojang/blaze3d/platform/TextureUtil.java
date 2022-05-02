package com.mojang.blaze3d.platform;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

@ClientJarOnly
public class TextureUtil {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final int MIN_MIPMAP_LEVEL = 0;
   private static final int DEFAULT_IMAGE_BUFFER_SIZE = 8192;

   public static int generateTextureId() {
      return GlStateManager.genTexture();
   }

   public static void releaseTextureId(int i) {
      GlStateManager.deleteTexture(i);
   }

   public static void prepareImage(int var0, int var1, int var2) {
      prepareImage(NativeImage.InternalGlFormat.RGBA, var0, 0, var1, var2);
   }

   public static void prepareImage(NativeImage.InternalGlFormat nativeImage$InternalGlFormat, int var1, int var2, int var3) {
      prepareImage(nativeImage$InternalGlFormat, var1, 0, var2, var3);
   }

   public static void prepareImage(int var0, int var1, int var2, int var3) {
      prepareImage(NativeImage.InternalGlFormat.RGBA, var0, var1, var2, var3);
   }

   public static void prepareImage(NativeImage.InternalGlFormat nativeImage$InternalGlFormat, int var1, int var2, int var3, int var4) {
      bind(var1);
      if(var2 >= 0) {
         GlStateManager.texParameter(3553, '脽', var2);
         GlStateManager.texParameter(3553, '脺', 0);
         GlStateManager.texParameter(3553, '脻', var2);
         GlStateManager.texParameter(3553, '蔁', 0.0F);
      }

      for(int var5 = 0; var5 <= var2; ++var5) {
         GlStateManager.texImage2D(3553, var5, nativeImage$InternalGlFormat.glFormat(), var3 >> var5, var4 >> var5, 0, 6408, 5121, (IntBuffer)null);
      }

   }

   private static void bind(int i) {
      GlStateManager.bindTexture(i);
   }

   public static ByteBuffer readResource(InputStream inputStream) throws IOException {
      ByteBuffer byteBuffer;
      if(inputStream instanceof FileInputStream) {
         FileInputStream var2 = (FileInputStream)inputStream;
         FileChannel var3 = var2.getChannel();
         byteBuffer = MemoryUtil.memAlloc((int)var3.size() + 1);

         while(true) {
            if(var3.read(byteBuffer) != -1) {
               continue;
            }
         }
      } else {
         byteBuffer = MemoryUtil.memAlloc(8192);
         ReadableByteChannel var2 = Channels.newChannel(inputStream);

         while(var2.read(byteBuffer) != -1) {
            if(byteBuffer.remaining() == 0) {
               byteBuffer = MemoryUtil.memRealloc(byteBuffer, byteBuffer.capacity() * 2);
            }
         }
      }

      return byteBuffer;
   }

   public static String readResourceAsString(InputStream inputStream) {
      ByteBuffer var1 = null;

      try {
         var1 = readResource(inputStream);
         int var2 = var1.position();
         var1.rewind();
         String var3 = MemoryUtil.memASCII(var1, var2);
         return var3;
      } catch (IOException var7) {
         ;
      } finally {
         if(var1 != null) {
            MemoryUtil.memFree(var1);
         }

      }

      return null;
   }

   public static void writeAsPNG(String string, int var1, int var2, int var3, int var4) {
      bind(var1);

      for(int var5 = 0; var5 <= var2; ++var5) {
         String var6 = string + "_" + var5 + ".png";
         int var7 = var3 >> var5;
         int var8 = var4 >> var5;

         try {
            NativeImage var9 = new NativeImage(var7, var8, false);
            Throwable var10 = null;

            try {
               var9.downloadTexture(var5, false);
               var9.writeToFile(var6);
               LOGGER.debug("Exported png to: {}", (new File(var6)).getAbsolutePath());
            } catch (Throwable var20) {
               var10 = var20;
               throw var20;
            } finally {
               if(var9 != null) {
                  if(var10 != null) {
                     try {
                        var9.close();
                     } catch (Throwable var19) {
                        var10.addSuppressed(var19);
                     }
                  } else {
                     var9.close();
                  }
               }

            }
         } catch (IOException var22) {
            LOGGER.debug("Unable to write: ", var22);
         }
      }

   }

   public static void initTexture(IntBuffer intBuffer, int var1, int var2) {
      GL11.glPixelStorei(3312, 0);
      GL11.glPixelStorei(3313, 0);
      GL11.glPixelStorei(3314, 0);
      GL11.glPixelStorei(3315, 0);
      GL11.glPixelStorei(3316, 0);
      GL11.glPixelStorei(3317, 4);
      GL11.glTexImage2D(3553, 0, 6408, var1, var2, 0, '胡', '荧', intBuffer);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10241, 9729);
   }
}
