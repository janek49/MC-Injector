package net.minecraft.client.gui.font;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.RawGlyph;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.MissingGlyph;

@ClientJarOnly
public class AllMissingGlyphProvider implements GlyphProvider {
   @Nullable
   public RawGlyph getGlyph(char c) {
      return MissingGlyph.INSTANCE;
   }
}
