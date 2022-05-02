package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.model.Vertex;
import net.minecraft.world.phys.Vec3;

@ClientJarOnly
public class Polygon {
   public Vertex[] vertices;
   public final int vertexCount;
   private boolean flipNormal;

   public Polygon(Vertex[] vertices) {
      this.vertices = vertices;
      this.vertexCount = vertices.length;
   }

   public Polygon(Vertex[] vertexs, int var2, int var3, int var4, int var5, float var6, float var7) {
      this(vertexs);
      float var8 = 0.0F / var6;
      float var9 = 0.0F / var7;
      vertexs[0] = vertexs[0].remap((float)var4 / var6 - var8, (float)var3 / var7 + var9);
      vertexs[1] = vertexs[1].remap((float)var2 / var6 + var8, (float)var3 / var7 + var9);
      vertexs[2] = vertexs[2].remap((float)var2 / var6 + var8, (float)var5 / var7 - var9);
      vertexs[3] = vertexs[3].remap((float)var4 / var6 - var8, (float)var5 / var7 - var9);
   }

   public void mirror() {
      Vertex[] vars1 = new Vertex[this.vertices.length];

      for(int var2 = 0; var2 < this.vertices.length; ++var2) {
         vars1[var2] = this.vertices[this.vertices.length - var2 - 1];
      }

      this.vertices = vars1;
   }

   public void render(BufferBuilder bufferBuilder, float var2) {
      Vec3 var3 = this.vertices[1].pos.vectorTo(this.vertices[0].pos);
      Vec3 var4 = this.vertices[1].pos.vectorTo(this.vertices[2].pos);
      Vec3 var5 = var4.cross(var3).normalize();
      float var6 = (float)var5.x;
      float var7 = (float)var5.y;
      float var8 = (float)var5.z;
      if(this.flipNormal) {
         var6 = -var6;
         var7 = -var7;
         var8 = -var8;
      }

      bufferBuilder.begin(7, DefaultVertexFormat.ENTITY);

      for(int var9 = 0; var9 < 4; ++var9) {
         Vertex var10 = this.vertices[var9];
         bufferBuilder.vertex(var10.pos.x * (double)var2, var10.pos.y * (double)var2, var10.pos.z * (double)var2).uv((double)var10.u, (double)var10.v).normal(var6, var7, var8).endVertex();
      }

      Tesselator.getInstance().end();
   }
}
