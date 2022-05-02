package net.minecraft.client.gui.font.glyphs;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.vertex.BufferBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

@ClientJarOnly
public class EmptyGlyph extends BakedGlyph {
   public EmptyGlyph() {
      super(new ResourceLocation(""), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void render(TextureManager textureManager, boolean var2, float var3, float var4, BufferBuilder bufferBuilder, float var6, float var7, float var8, float var9) {
   }

   @Nullable
   public ResourceLocation getTexture() {
      return null;
   }
}
