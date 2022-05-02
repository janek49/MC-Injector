package com.mojang.blaze3d.vertex;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexBuffer;

@ClientJarOnly
public class VertexBufferUploader extends BufferUploader {
   private VertexBuffer buffer;

   public void end(BufferBuilder bufferBuilder) {
      bufferBuilder.clear();
      this.buffer.upload(bufferBuilder.getBuffer());
   }

   public void setBuffer(VertexBuffer buffer) {
      this.buffer = buffer;
   }
}
