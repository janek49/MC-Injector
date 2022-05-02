package net.minecraft.client.renderer.chunk;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.level.Level;

@ClientJarOnly
public interface RenderChunkFactory {
   RenderChunk create(Level var1, LevelRenderer var2);
}
