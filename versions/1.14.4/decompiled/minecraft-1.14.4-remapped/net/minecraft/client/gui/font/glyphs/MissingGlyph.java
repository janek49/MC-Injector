package net.minecraft.client.gui.font.glyphs;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.font.RawGlyph;
import com.mojang.blaze3d.platform.NativeImage;
import java.util.function.Consumer;
import net.minecraft.Util;

@ClientJarOnly
public enum MissingGlyph implements RawGlyph {
   INSTANCE;

   private static final NativeImage IMAGE_DATA = (NativeImage)Util.make(new NativeImage(NativeImage.Format.RGBA, 5, 8, false), (nativeImage) -> {
      for(int var1 = 0; var1 < 8; ++var1) {
         for(int var2 = 0; var2 < 5; ++var2) {
            boolean var3 = var2 == 0 || var2 + 1 == 5 || var1 == 0 || var1 + 1 == 8;
            nativeImage.setPixelRGBA(var2, var1, var3?-1:0);
         }
      }

      nativeImage.untrack();
   });

   public int getPixelWidth() {
      return 5;
   }

   public int getPixelHeight() {
      return 8;
   }

   public float getAdvance() {
      return 6.0F;
   }

   public float getOversample() {
      return 1.0F;
   }

   public void upload(int var1, int var2) {
      IMAGE_DATA.upload(0, var1, var2, false);
   }

   public boolean isColored() {
      return true;
   }
}
