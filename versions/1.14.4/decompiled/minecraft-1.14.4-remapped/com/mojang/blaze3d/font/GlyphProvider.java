package com.mojang.blaze3d.font;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.font.RawGlyph;
import java.io.Closeable;
import javax.annotation.Nullable;

@ClientJarOnly
public interface GlyphProvider extends Closeable {
   default void close() {
   }

   @Nullable
   default RawGlyph getGlyph(char c) {
      return null;
   }
}
