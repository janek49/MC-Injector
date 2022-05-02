package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.ChunkRenderList;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.level.BlockLayer;

@ClientJarOnly
public class OffsettedRenderList extends ChunkRenderList {
   public void render(BlockLayer blockLayer) {
      if(this.ready) {
         for(RenderChunk var3 : this.chunks) {
            ListedRenderChunk var4 = (ListedRenderChunk)var3;
            GlStateManager.pushMatrix();
            this.translateToRelativeChunkPosition(var3);
            GlStateManager.callList(var4.getGlListId(blockLayer, var4.getCompiledChunk()));
            GlStateManager.popMatrix();
         }

         GlStateManager.clearCurrentColor();
         this.chunks.clear();
      }
   }
}
