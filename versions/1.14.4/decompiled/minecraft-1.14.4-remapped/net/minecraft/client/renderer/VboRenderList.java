package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.ChunkRenderList;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.level.BlockLayer;

@ClientJarOnly
public class VboRenderList extends ChunkRenderList {
   public void render(BlockLayer blockLayer) {
      if(this.ready) {
         for(RenderChunk var3 : this.chunks) {
            VertexBuffer var4 = var3.getBuffer(blockLayer.ordinal());
            GlStateManager.pushMatrix();
            this.translateToRelativeChunkPosition(var3);
            var4.bind();
            this.applyVertexDeclaration();
            var4.draw(7);
            GlStateManager.popMatrix();
         }

         VertexBuffer.unbind();
         GlStateManager.clearCurrentColor();
         this.chunks.clear();
      }
   }

   private void applyVertexDeclaration() {
      GlStateManager.vertexPointer(3, 5126, 28, 0);
      GlStateManager.colorPointer(4, 5121, 28, 12);
      GlStateManager.texCoordPointer(2, 5126, 28, 16);
      GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
      GlStateManager.texCoordPointer(2, 5122, 28, 24);
      GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
   }
}
