package com.mojang.blaze3d.shaders;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Locale;

@ClientJarOnly
public class BlendMode {
   private static BlendMode lastApplied;
   private final int srcColorFactor;
   private final int srcAlphaFactor;
   private final int dstColorFactor;
   private final int dstAlphaFactor;
   private final int blendFunc;
   private final boolean separateBlend;
   private final boolean opaque;

   private BlendMode(boolean separateBlend, boolean opaque, int srcColorFactor, int dstColorFactor, int srcAlphaFactor, int dstAlphaFactor, int blendFunc) {
      this.separateBlend = separateBlend;
      this.srcColorFactor = srcColorFactor;
      this.dstColorFactor = dstColorFactor;
      this.srcAlphaFactor = srcAlphaFactor;
      this.dstAlphaFactor = dstAlphaFactor;
      this.opaque = opaque;
      this.blendFunc = blendFunc;
   }

   public BlendMode() {
      this(false, true, 1, 0, 1, 0, '耆');
   }

   public BlendMode(int var1, int var2, int var3) {
      this(false, false, var1, var2, var1, var2, var3);
   }

   public BlendMode(int var1, int var2, int var3, int var4, int var5) {
      this(true, false, var1, var2, var3, var4, var5);
   }

   public void apply() {
      if(!this.equals(lastApplied)) {
         if(lastApplied == null || this.opaque != lastApplied.isOpaque()) {
            lastApplied = this;
            if(this.opaque) {
               GlStateManager.disableBlend();
               return;
            }

            GlStateManager.enableBlend();
         }

         GlStateManager.blendEquation(this.blendFunc);
         if(this.separateBlend) {
            GlStateManager.blendFuncSeparate(this.srcColorFactor, this.dstColorFactor, this.srcAlphaFactor, this.dstAlphaFactor);
         } else {
            GlStateManager.blendFunc(this.srcColorFactor, this.dstColorFactor);
         }

      }
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof BlendMode)) {
         return false;
      } else {
         BlendMode var2 = (BlendMode)object;
         return this.blendFunc != var2.blendFunc?false:(this.dstAlphaFactor != var2.dstAlphaFactor?false:(this.dstColorFactor != var2.dstColorFactor?false:(this.opaque != var2.opaque?false:(this.separateBlend != var2.separateBlend?false:(this.srcAlphaFactor != var2.srcAlphaFactor?false:this.srcColorFactor == var2.srcColorFactor)))));
      }
   }

   public int hashCode() {
      int var1 = this.srcColorFactor;
      var1 = 31 * var1 + this.srcAlphaFactor;
      var1 = 31 * var1 + this.dstColorFactor;
      var1 = 31 * var1 + this.dstAlphaFactor;
      var1 = 31 * var1 + this.blendFunc;
      var1 = 31 * var1 + (this.separateBlend?1:0);
      var1 = 31 * var1 + (this.opaque?1:0);
      return var1;
   }

   public boolean isOpaque() {
      return this.opaque;
   }

   public static int stringToBlendFunc(String string) {
      String string = string.trim().toLowerCase(Locale.ROOT);
      return "add".equals(string)?'耆':("subtract".equals(string)?'耊':("reversesubtract".equals(string)?'耋':("reverse_subtract".equals(string)?'耋':("min".equals(string)?'耇':("max".equals(string)?'耈':'耆')))));
   }

   public static int stringToBlendFactor(String string) {
      String string = string.trim().toLowerCase(Locale.ROOT);
      string = string.replaceAll("_", "");
      string = string.replaceAll("one", "1");
      string = string.replaceAll("zero", "0");
      string = string.replaceAll("minus", "-");
      return "0".equals(string)?0:("1".equals(string)?1:("srccolor".equals(string)?768:("1-srccolor".equals(string)?769:("dstcolor".equals(string)?774:("1-dstcolor".equals(string)?775:("srcalpha".equals(string)?770:("1-srcalpha".equals(string)?771:("dstalpha".equals(string)?772:("1-dstalpha".equals(string)?773:-1)))))))));
   }
}
