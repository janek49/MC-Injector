package com.mojang.blaze3d.vertex;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.nio.ByteBuffer;
import java.util.List;

@ClientJarOnly
public class BufferUploader {
   public void end(BufferBuilder bufferBuilder) {
      if(bufferBuilder.getVertexCount() > 0) {
         VertexFormat var2 = bufferBuilder.getVertexFormat();
         int var3 = var2.getVertexSize();
         ByteBuffer var4 = bufferBuilder.getBuffer();
         List<VertexFormatElement> var5 = var2.getElements();

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            VertexFormatElement var7 = (VertexFormatElement)var5.get(var6);
            VertexFormatElement.Usage var8 = var7.getUsage();
            int var9 = var7.getType().getGlType();
            int var10 = var7.getIndex();
            var4.position(var2.getOffset(var6));
            switch(var8) {
            case POSITION:
               GlStateManager.vertexPointer(var7.getCount(), var9, var3, var4);
               GlStateManager.enableClientState('聴');
               break;
            case UV:
               GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + var10);
               GlStateManager.texCoordPointer(var7.getCount(), var9, var3, var4);
               GlStateManager.enableClientState('聸');
               GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
               break;
            case COLOR:
               GlStateManager.colorPointer(var7.getCount(), var9, var3, var4);
               GlStateManager.enableClientState('聶');
               break;
            case NORMAL:
               GlStateManager.normalPointer(var9, var3, var4);
               GlStateManager.enableClientState('聵');
            }
         }

         GlStateManager.drawArrays(bufferBuilder.getDrawMode(), 0, bufferBuilder.getVertexCount());
         int var6 = 0;

         for(int var7 = var5.size(); var6 < var7; ++var6) {
            VertexFormatElement var8 = (VertexFormatElement)var5.get(var6);
            VertexFormatElement.Usage var9 = var8.getUsage();
            int var10 = var8.getIndex();
            switch(var9) {
            case POSITION:
               GlStateManager.disableClientState('聴');
               break;
            case UV:
               GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + var10);
               GlStateManager.disableClientState('聸');
               GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
               break;
            case COLOR:
               GlStateManager.disableClientState('聶');
               GlStateManager.clearCurrentColor();
               break;
            case NORMAL:
               GlStateManager.disableClientState('聵');
            }
         }
      }

      bufferBuilder.clear();
   }
}
