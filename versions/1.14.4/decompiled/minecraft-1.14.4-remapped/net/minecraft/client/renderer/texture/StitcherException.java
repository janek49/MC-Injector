package net.minecraft.client.renderer.texture;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Collection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

@ClientJarOnly
public class StitcherException extends RuntimeException {
   private final Collection allSprites;

   public StitcherException(TextureAtlasSprite textureAtlasSprite, Collection allSprites) {
      super(String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", new Object[]{textureAtlasSprite.getName(), Integer.valueOf(textureAtlasSprite.getWidth()), Integer.valueOf(textureAtlasSprite.getHeight())}));
      this.allSprites = allSprites;
   }

   public Collection getAllSprites() {
      return this.allSprites;
   }
}
