package net.minecraft.client.renderer.texture;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.client.renderer.texture.Tickable;

@ClientJarOnly
public interface TickableTextureObject extends TextureObject, Tickable {
}
