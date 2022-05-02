package net.minecraft.client.renderer.chunk;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.MemoryTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class ListedRenderChunk extends RenderChunk {
   private final int listId = MemoryTracker.genLists(BlockLayer.values().length);

   public ListedRenderChunk(Level level, LevelRenderer levelRenderer) {
      super(level, levelRenderer);
   }

   public int getGlListId(BlockLayer blockLayer, CompiledChunk compiledChunk) {
      return !compiledChunk.isEmpty(blockLayer)?this.listId + blockLayer.ordinal():-1;
   }

   public void releaseBuffers() {
      super.releaseBuffers();
      MemoryTracker.releaseLists(this.listId, BlockLayer.values().length);
   }
}
