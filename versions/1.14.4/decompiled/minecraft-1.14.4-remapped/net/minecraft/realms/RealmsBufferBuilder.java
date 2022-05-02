package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import net.minecraft.realms.RealmsVertexFormat;

@ClientJarOnly
public class RealmsBufferBuilder {
   private BufferBuilder b;

   public RealmsBufferBuilder(BufferBuilder b) {
      this.b = b;
   }

   public RealmsBufferBuilder from(BufferBuilder b) {
      this.b = b;
      return this;
   }

   public void sortQuads(float var1, float var2, float var3) {
      this.b.sortQuads(var1, var2, var3);
   }

   public void fixupQuadColor(int i) {
      this.b.fixupQuadColor(i);
   }

   public ByteBuffer getBuffer() {
      return this.b.getBuffer();
   }

   public void postNormal(float var1, float var2, float var3) {
      this.b.postNormal(var1, var2, var3);
   }

   public int getDrawMode() {
      return this.b.getDrawMode();
   }

   public void offset(double var1, double var3, double var5) {
      this.b.offset(var1, var3, var5);
   }

   public void restoreState(BufferBuilder.State bufferBuilder$State) {
      this.b.restoreState(bufferBuilder$State);
   }

   public void endVertex() {
      this.b.endVertex();
   }

   public RealmsBufferBuilder normal(float var1, float var2, float var3) {
      return this.from(this.b.normal(var1, var2, var3));
   }

   public void end() {
      this.b.end();
   }

   public void begin(int var1, VertexFormat vertexFormat) {
      this.b.begin(var1, vertexFormat);
   }

   public RealmsBufferBuilder color(int var1, int var2, int var3, int var4) {
      return this.from(this.b.color(var1, var2, var3, var4));
   }

   public void faceTex2(int var1, int var2, int var3, int var4) {
      this.b.faceTex2(var1, var2, var3, var4);
   }

   public void postProcessFacePosition(double var1, double var3, double var5) {
      this.b.postProcessFacePosition(var1, var3, var5);
   }

   public void fixupVertexColor(float var1, float var2, float var3, int var4) {
      this.b.fixupVertexColor(var1, var2, var3, var4);
   }

   public RealmsBufferBuilder color(float var1, float var2, float var3, float var4) {
      return this.from(this.b.color(var1, var2, var3, var4));
   }

   public RealmsVertexFormat getVertexFormat() {
      return new RealmsVertexFormat(this.b.getVertexFormat());
   }

   public void faceTint(float var1, float var2, float var3, int var4) {
      this.b.faceTint(var1, var2, var3, var4);
   }

   public RealmsBufferBuilder tex2(int var1, int var2) {
      return this.from(this.b.uv2(var1, var2));
   }

   public void putBulkData(int[] ints) {
      this.b.putBulkData(ints);
   }

   public RealmsBufferBuilder tex(double var1, double var3) {
      return this.from(this.b.uv(var1, var3));
   }

   public int getVertexCount() {
      return this.b.getVertexCount();
   }

   public void clear() {
      this.b.clear();
   }

   public RealmsBufferBuilder vertex(double var1, double var3, double var5) {
      return this.from(this.b.vertex(var1, var3, var5));
   }

   public void fixupQuadColor(float var1, float var2, float var3) {
      this.b.fixupQuadColor(var1, var2, var3);
   }

   public void noColor() {
      this.b.noColor();
   }
}
