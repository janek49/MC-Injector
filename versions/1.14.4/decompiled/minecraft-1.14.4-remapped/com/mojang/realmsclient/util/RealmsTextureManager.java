package com.mojang.realmsclient.util;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.SkinProcessor;
import com.mojang.util.UUIDTypeAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsTextureManager {
   private static final Map textures = new HashMap();
   private static final Map skinFetchStatus = new HashMap();
   private static final Map fetchedSkins = new HashMap();
   private static final Logger LOGGER = LogManager.getLogger();

   public static void bindWorldTemplate(String var0, String var1) {
      if(var1 == null) {
         RealmsScreen.bind("textures/gui/presets/isles.png");
      } else {
         int var2 = getTextureId(var0, var1);
         GlStateManager.bindTexture(var2);
      }
   }

   public static void withBoundFace(String string, Runnable runnable) {
      GLX.withTextureRestore(() -> {
         bindFace(string);
         runnable.run();
      });
   }

   private static void bindDefaultFace(UUID uUID) {
      RealmsScreen.bind((uUID.hashCode() & 1) == 1?"minecraft:textures/entity/alex.png":"minecraft:textures/entity/steve.png");
   }

   private static void bindFace(final String string) {
      UUID var1 = UUIDTypeAdapter.fromString(string);
      if(textures.containsKey(string)) {
         GlStateManager.bindTexture(((RealmsTextureManager.RealmsTexture)textures.get(string)).textureId);
      } else if(skinFetchStatus.containsKey(string)) {
         if(!((Boolean)skinFetchStatus.get(string)).booleanValue()) {
            bindDefaultFace(var1);
         } else if(fetchedSkins.containsKey(string)) {
            int var2 = getTextureId(string, (String)fetchedSkins.get(string));
            GlStateManager.bindTexture(var2);
         } else {
            bindDefaultFace(var1);
         }

      } else {
         skinFetchStatus.put(string, Boolean.valueOf(false));
         bindDefaultFace(var1);
         Thread var2 = new Thread("Realms Texture Downloader") {
            public void run() {
               Map<Type, MinecraftProfileTexture> var1 = RealmsUtil.getTextures(string);
               if(var1.containsKey(Type.SKIN)) {
                  MinecraftProfileTexture var2 = (MinecraftProfileTexture)var1.get(Type.SKIN);
                  String var3 = var2.getUrl();
                  HttpURLConnection var4 = null;
                  RealmsTextureManager.LOGGER.debug("Downloading http texture from {}", var3);

                  try {
                     var4 = (HttpURLConnection)(new URL(var3)).openConnection(Realms.getProxy());
                     var4.setDoInput(true);
                     var4.setDoOutput(false);
                     var4.connect();
                     if(var4.getResponseCode() / 100 == 2) {
                        BufferedImage var5;
                        try {
                           var5 = ImageIO.read(var4.getInputStream());
                        } catch (Exception var17) {
                           RealmsTextureManager.skinFetchStatus.remove(string);
                           return;
                        } finally {
                           IOUtils.closeQuietly(var4.getInputStream());
                        }

                        var5 = (new SkinProcessor()).process(var5);
                        ByteArrayOutputStream var6 = new ByteArrayOutputStream();
                        ImageIO.write(var5, "png", var6);
                        RealmsTextureManager.fetchedSkins.put(string, DatatypeConverter.printBase64Binary(var6.toByteArray()));
                        RealmsTextureManager.skinFetchStatus.put(string, Boolean.valueOf(true));
                        return;
                     }

                     RealmsTextureManager.skinFetchStatus.remove(string);
                  } catch (Exception var19) {
                     RealmsTextureManager.LOGGER.error("Couldn\'t download http texture", var19);
                     RealmsTextureManager.skinFetchStatus.remove(string);
                     return;
                  } finally {
                     if(var4 != null) {
                        var4.disconnect();
                     }

                  }

               } else {
                  RealmsTextureManager.skinFetchStatus.put(string, Boolean.valueOf(true));
               }
            }
         };
         var2.setDaemon(true);
         var2.start();
      }
   }

   private static int getTextureId(String var0, String var1) {
      int var2;
      if(textures.containsKey(var0)) {
         RealmsTextureManager.RealmsTexture var3 = (RealmsTextureManager.RealmsTexture)textures.get(var0);
         if(var3.image.equals(var1)) {
            return var3.textureId;
         }

         GlStateManager.deleteTexture(var3.textureId);
         var2 = var3.textureId;
      } else {
         var2 = GlStateManager.genTexture();
      }

      IntBuffer var3 = null;
      int var4 = 0;
      int var5 = 0;

      try {
         InputStream var7 = new ByteArrayInputStream((new Base64()).decode(var1));

         BufferedImage var6;
         try {
            var6 = ImageIO.read(var7);
         } finally {
            IOUtils.closeQuietly(var7);
         }

         var4 = var6.getWidth();
         var5 = var6.getHeight();
         int[] vars8 = new int[var4 * var5];
         var6.getRGB(0, 0, var4, var5, vars8, 0, var4);
         var3 = ByteBuffer.allocateDirect(4 * var4 * var5).order(ByteOrder.nativeOrder()).asIntBuffer();
         var3.put(vars8);
         var3.flip();
      } catch (IOException var12) {
         var12.printStackTrace();
      }

      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
      GlStateManager.bindTexture(var2);
      TextureUtil.initTexture(var3, var4, var5);
      textures.put(var0, new RealmsTextureManager.RealmsTexture(var1, var2));
      return var2;
   }

   @ClientJarOnly
   public static class RealmsTexture {
      String image;
      int textureId;

      public RealmsTexture(String image, int textureId) {
         this.image = image;
         this.textureId = textureId;
      }
   }
}
