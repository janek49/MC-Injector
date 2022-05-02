package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

@ClientJarOnly
public interface ParticleRenderType {
   ParticleRenderType TERRAIN_SHEET = new ParticleRenderType() {
      public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
         Lighting.turnOff();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
         textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
         bufferBuilder.begin(7, DefaultVertexFormat.PARTICLE);
      }

      public void end(Tesselator tesselator) {
         tesselator.end();
      }

      public String toString() {
         return "TERRAIN_SHEET";
      }
   };
   ParticleRenderType PARTICLE_SHEET_OPAQUE = new ParticleRenderType() {
      public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
         Lighting.turnOff();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
         textureManager.bind(TextureAtlas.LOCATION_PARTICLES);
         bufferBuilder.begin(7, DefaultVertexFormat.PARTICLE);
      }

      public void end(Tesselator tesselator) {
         tesselator.end();
      }

      public String toString() {
         return "PARTICLE_SHEET_OPAQUE";
      }
   };
   ParticleRenderType PARTICLE_SHEET_TRANSLUCENT = new ParticleRenderType() {
      public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
         Lighting.turnOff();
         GlStateManager.depthMask(false);
         textureManager.bind(TextureAtlas.LOCATION_PARTICLES);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         GlStateManager.alphaFunc(516, 0.003921569F);
         bufferBuilder.begin(7, DefaultVertexFormat.PARTICLE);
      }

      public void end(Tesselator tesselator) {
         tesselator.end();
      }

      public String toString() {
         return "PARTICLE_SHEET_TRANSLUCENT";
      }
   };
   ParticleRenderType PARTICLE_SHEET_LIT = new ParticleRenderType() {
      public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
         textureManager.bind(TextureAtlas.LOCATION_PARTICLES);
         Lighting.turnOff();
         bufferBuilder.begin(7, DefaultVertexFormat.PARTICLE);
      }

      public void end(Tesselator tesselator) {
         tesselator.end();
      }

      public String toString() {
         return "PARTICLE_SHEET_LIT";
      }
   };
   ParticleRenderType CUSTOM = new ParticleRenderType() {
      public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
         GlStateManager.depthMask(true);
         GlStateManager.disableBlend();
      }

      public void end(Tesselator tesselator) {
      }

      public String toString() {
         return "CUSTOM";
      }
   };
   ParticleRenderType NO_RENDER = new ParticleRenderType() {
      public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
      }

      public void end(Tesselator tesselator) {
      }

      public String toString() {
         return "NO_RENDER";
      }
   };

   void begin(BufferBuilder var1, TextureManager var2);

   void end(Tesselator var1);
}
