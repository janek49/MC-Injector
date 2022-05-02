package net.minecraft.client.resources.metadata.texture;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSectionSerializer;

@ClientJarOnly
public class TextureMetadataSection {
   public static final TextureMetadataSectionSerializer SERIALIZER = new TextureMetadataSectionSerializer();
   private final boolean blur;
   private final boolean clamp;

   public TextureMetadataSection(boolean blur, boolean clamp) {
      this.blur = blur;
      this.clamp = clamp;
   }

   public boolean isBlur() {
      return this.blur;
   }

   public boolean isClamp() {
      return this.clamp;
   }
}
