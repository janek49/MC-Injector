package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Random;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

@ClientJarOnly
public interface SpriteSet {
   TextureAtlasSprite get(int var1, int var2);

   TextureAtlasSprite get(Random var1);
}
