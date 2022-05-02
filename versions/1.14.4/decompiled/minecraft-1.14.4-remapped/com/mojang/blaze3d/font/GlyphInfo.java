package com.mojang.blaze3d.font;

import com.fox2code.repacker.ClientJarOnly;

@ClientJarOnly
public interface GlyphInfo {
   float getAdvance();

   default float getAdvance(boolean b) {
      return this.getAdvance() + (b?this.getBoldOffset():0.0F);
   }

   default float getBearingX() {
      return 0.0F;
   }

   default float getBoldOffset() {
      return 1.0F;
   }

   default float getShadowOffset() {
      return 1.0F;
   }
}
