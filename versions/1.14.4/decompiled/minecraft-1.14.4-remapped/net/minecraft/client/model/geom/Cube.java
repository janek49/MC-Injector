package net.minecraft.client.model.geom;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.model.Polygon;
import net.minecraft.client.model.Vertex;
import net.minecraft.client.model.geom.ModelPart;

@ClientJarOnly
public class Cube {
   private final Vertex[] vertices;
   private final Polygon[] polygons;
   public final float minX;
   public final float minY;
   public final float minZ;
   public final float maxX;
   public final float maxY;
   public final float maxZ;
   public String id;

   public Cube(ModelPart modelPart, int var2, int var3, float var4, float var5, float var6, int var7, int var8, int var9, float var10) {
      this(modelPart, var2, var3, var4, var5, var6, var7, var8, var9, var10, modelPart.mirror);
   }

   public Cube(ModelPart modelPart, int var2, int var3, float minX, float minY, float minZ, int var7, int var8, int var9, float var10, boolean var11) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = minX + (float)var7;
      this.maxY = minY + (float)var8;
      this.maxZ = minZ + (float)var9;
      this.vertices = new Vertex[8];
      this.polygons = new Polygon[6];
      float var12 = minX + (float)var7;
      float var13 = minY + (float)var8;
      float var14 = minZ + (float)var9;
      minX = minX - var10;
      minY = minY - var10;
      minZ = minZ - var10;
      var12 = var12 + var10;
      var13 = var13 + var10;
      var14 = var14 + var10;
      if(var11) {
         float var15 = var12;
         var12 = minX;
         minX = var15;
      }

      Vertex var15 = new Vertex(minX, minY, minZ, 0.0F, 0.0F);
      Vertex var16 = new Vertex(var12, minY, minZ, 0.0F, 8.0F);
      Vertex var17 = new Vertex(var12, var13, minZ, 8.0F, 8.0F);
      Vertex var18 = new Vertex(minX, var13, minZ, 8.0F, 0.0F);
      Vertex var19 = new Vertex(minX, minY, var14, 0.0F, 0.0F);
      Vertex var20 = new Vertex(var12, minY, var14, 0.0F, 8.0F);
      Vertex var21 = new Vertex(var12, var13, var14, 8.0F, 8.0F);
      Vertex var22 = new Vertex(minX, var13, var14, 8.0F, 0.0F);
      this.vertices[0] = var15;
      this.vertices[1] = var16;
      this.vertices[2] = var17;
      this.vertices[3] = var18;
      this.vertices[4] = var19;
      this.vertices[5] = var20;
      this.vertices[6] = var21;
      this.vertices[7] = var22;
      this.polygons[0] = new Polygon(new Vertex[]{var20, var16, var17, var21}, var2 + var9 + var7, var3 + var9, var2 + var9 + var7 + var9, var3 + var9 + var8, modelPart.xTexSize, modelPart.yTexSize);
      this.polygons[1] = new Polygon(new Vertex[]{var15, var19, var22, var18}, var2, var3 + var9, var2 + var9, var3 + var9 + var8, modelPart.xTexSize, modelPart.yTexSize);
      this.polygons[2] = new Polygon(new Vertex[]{var20, var19, var15, var16}, var2 + var9, var3, var2 + var9 + var7, var3 + var9, modelPart.xTexSize, modelPart.yTexSize);
      this.polygons[3] = new Polygon(new Vertex[]{var17, var18, var22, var21}, var2 + var9 + var7, var3 + var9, var2 + var9 + var7 + var7, var3, modelPart.xTexSize, modelPart.yTexSize);
      this.polygons[4] = new Polygon(new Vertex[]{var16, var15, var18, var17}, var2 + var9, var3 + var9, var2 + var9 + var7, var3 + var9 + var8, modelPart.xTexSize, modelPart.yTexSize);
      this.polygons[5] = new Polygon(new Vertex[]{var19, var20, var21, var22}, var2 + var9 + var7 + var9, var3 + var9, var2 + var9 + var7 + var9 + var7, var3 + var9 + var8, modelPart.xTexSize, modelPart.yTexSize);
      if(var11) {
         for(Polygon var26 : this.polygons) {
            var26.mirror();
         }
      }

   }

   public void compile(BufferBuilder bufferBuilder, float var2) {
      for(Polygon var6 : this.polygons) {
         var6.render(bufferBuilder, var2);
      }

   }

   public Cube setId(String id) {
      this.id = id;
      return this;
   }
}
