package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.HttpTextureProcessor;

@ClientJarOnly
public class MobSkinTextureProcessor implements HttpTextureProcessor {
   public NativeImage process(NativeImage nativeImage) {
      boolean var2 = nativeImage.getHeight() == 32;
      if(var2) {
         NativeImage var3 = new NativeImage(64, 64, true);
         var3.copyFrom(nativeImage);
         nativeImage.close();
         nativeImage = var3;
         var3.fillRect(0, 32, 64, 32, 0);
         var3.copyRect(4, 16, 16, 32, 4, 4, true, false);
         var3.copyRect(8, 16, 16, 32, 4, 4, true, false);
         var3.copyRect(0, 20, 24, 32, 4, 12, true, false);
         var3.copyRect(4, 20, 16, 32, 4, 12, true, false);
         var3.copyRect(8, 20, 8, 32, 4, 12, true, false);
         var3.copyRect(12, 20, 16, 32, 4, 12, true, false);
         var3.copyRect(44, 16, -8, 32, 4, 4, true, false);
         var3.copyRect(48, 16, -8, 32, 4, 4, true, false);
         var3.copyRect(40, 20, 0, 32, 4, 12, true, false);
         var3.copyRect(44, 20, -8, 32, 4, 12, true, false);
         var3.copyRect(48, 20, -16, 32, 4, 12, true, false);
         var3.copyRect(52, 20, -8, 32, 4, 12, true, false);
      }

      setNoAlpha(nativeImage, 0, 0, 32, 16);
      if(var2) {
         doLegacyTransparencyHack(nativeImage, 32, 0, 64, 32);
      }

      setNoAlpha(nativeImage, 0, 16, 64, 32);
      setNoAlpha(nativeImage, 16, 48, 48, 64);
      return nativeImage;
   }

   public void onTextureDownloaded() {
   }

   private static void doLegacyTransparencyHack(NativeImage nativeImage, int var1, int var2, int var3, int var4) {
      for(int var5 = var1; var5 < var3; ++var5) {
         for(int var6 = var2; var6 < var4; ++var6) {
            int var7 = nativeImage.getPixelRGBA(var5, var6);
            if((var7 >> 24 & 255) < 128) {
               return;
            }
         }
      }

      for(int var5 = var1; var5 < var3; ++var5) {
         for(int var6 = var2; var6 < var4; ++var6) {
            nativeImage.setPixelRGBA(var5, var6, nativeImage.getPixelRGBA(var5, var6) & 16777215);
         }
      }

   }

   private static void setNoAlpha(NativeImage nativeImage, int var1, int var2, int var3, int var4) {
      for(int var5 = var1; var5 < var3; ++var5) {
         for(int var6 = var2; var6 < var4; ++var6) {
            nativeImage.setPixelRGBA(var5, var6, nativeImage.getPixelRGBA(var5, var6) | -16777216);
         }
      }

   }
}
