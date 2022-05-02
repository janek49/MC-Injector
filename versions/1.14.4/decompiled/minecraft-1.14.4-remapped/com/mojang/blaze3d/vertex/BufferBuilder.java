package com.mojang.blaze3d.vertex;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.primitives.Floats;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class BufferBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private ByteBuffer buffer;
   private IntBuffer intBuffer;
   private ShortBuffer shortBuffer;
   private FloatBuffer floatBuffer;
   private int vertices;
   private VertexFormatElement currentElement;
   private int elementIndex;
   private boolean noColor;
   private int mode;
   private double xo;
   private double yo;
   private double zo;
   private VertexFormat format;
   private boolean building;

   public BufferBuilder(int i) {
      this.buffer = MemoryTracker.createByteBuffer(i * 4);
      this.intBuffer = this.buffer.asIntBuffer();
      this.shortBuffer = this.buffer.asShortBuffer();
      this.floatBuffer = this.buffer.asFloatBuffer();
   }

   private void ensureCapacity(int i) {
      if(this.vertices * this.format.getVertexSize() + i > this.buffer.capacity()) {
         int var2 = this.buffer.capacity();
         int var3 = var2 + roundUp(i);
         LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", Integer.valueOf(var2), Integer.valueOf(var3));
         int var4 = this.intBuffer.position();
         ByteBuffer var5 = MemoryTracker.createByteBuffer(var3);
         this.buffer.position(0);
         var5.put(this.buffer);
         var5.rewind();
         this.buffer = var5;
         this.floatBuffer = this.buffer.asFloatBuffer().asReadOnlyBuffer();
         this.intBuffer = this.buffer.asIntBuffer();
         this.intBuffer.position(var4);
         this.shortBuffer = this.buffer.asShortBuffer();
         this.shortBuffer.position(var4 << 1);
      }
   }

   private static int roundUp(int i) {
      int var1 = 2097152;
      if(i == 0) {
         return var1;
      } else {
         if(i < 0) {
            var1 *= -1;
         }

         int var2 = i % var1;
         return var2 == 0?i:i + var1 - var2;
      }
   }

   public void sortQuads(float var1, float var2, float var3) {
      int var4 = this.vertices / 4;
      float[] vars5 = new float[var4];

      for(int var6 = 0; var6 < var4; ++var6) {
         vars5[var6] = getQuadDistanceFromPlayer(this.floatBuffer, (float)((double)var1 + this.xo), (float)((double)var2 + this.yo), (float)((double)var3 + this.zo), this.format.getIntegerSize(), var6 * this.format.getVertexSize());
      }

      Integer[] vars6 = new Integer[var4];

      for(int var7 = 0; var7 < vars6.length; ++var7) {
         vars6[var7] = Integer.valueOf(var7);
      }

      Arrays.sort(vars6, (var1, var2) -> {
         return Floats.compare(vars5[var2.intValue()], vars5[var1.intValue()]);
      });
      BitSet var7 = new BitSet();
      int var8 = this.format.getVertexSize();
      int[] vars9 = new int[var8];

      for(int var10 = var7.nextClearBit(0); var10 < vars6.length; var10 = var7.nextClearBit(var10 + 1)) {
         int var11 = vars6[var10].intValue();
         if(var11 != var10) {
            this.intBuffer.limit(var11 * var8 + var8);
            this.intBuffer.position(var11 * var8);
            this.intBuffer.get(vars9);
            int var12 = var11;

            for(int var13 = vars6[var11].intValue(); var12 != var10; var13 = vars6[var13].intValue()) {
               this.intBuffer.limit(var13 * var8 + var8);
               this.intBuffer.position(var13 * var8);
               IntBuffer var14 = this.intBuffer.slice();
               this.intBuffer.limit(var12 * var8 + var8);
               this.intBuffer.position(var12 * var8);
               this.intBuffer.put(var14);
               var7.set(var12);
               var12 = var13;
            }

            this.intBuffer.limit(var10 * var8 + var8);
            this.intBuffer.position(var10 * var8);
            this.intBuffer.put(vars9);
         }

         var7.set(var10);
      }

   }

   public BufferBuilder.State getState() {
      this.intBuffer.rewind();
      int var1 = this.getBufferIndex();
      this.intBuffer.limit(var1);
      int[] vars2 = new int[var1];
      this.intBuffer.get(vars2);
      this.intBuffer.limit(this.intBuffer.capacity());
      this.intBuffer.position(var1);
      return new BufferBuilder.State(vars2, new VertexFormat(this.format));
   }

   private int getBufferIndex() {
      return this.vertices * this.format.getIntegerSize();
   }

   private static float getQuadDistanceFromPlayer(FloatBuffer floatBuffer, float var1, float var2, float var3, int var4, int var5) {
      float var6 = floatBuffer.get(var5 + var4 * 0 + 0);
      float var7 = floatBuffer.get(var5 + var4 * 0 + 1);
      float var8 = floatBuffer.get(var5 + var4 * 0 + 2);
      float var9 = floatBuffer.get(var5 + var4 * 1 + 0);
      float var10 = floatBuffer.get(var5 + var4 * 1 + 1);
      float var11 = floatBuffer.get(var5 + var4 * 1 + 2);
      float var12 = floatBuffer.get(var5 + var4 * 2 + 0);
      float var13 = floatBuffer.get(var5 + var4 * 2 + 1);
      float var14 = floatBuffer.get(var5 + var4 * 2 + 2);
      float var15 = floatBuffer.get(var5 + var4 * 3 + 0);
      float var16 = floatBuffer.get(var5 + var4 * 3 + 1);
      float var17 = floatBuffer.get(var5 + var4 * 3 + 2);
      float var18 = (var6 + var9 + var12 + var15) * 0.25F - var1;
      float var19 = (var7 + var10 + var13 + var16) * 0.25F - var2;
      float var20 = (var8 + var11 + var14 + var17) * 0.25F - var3;
      return var18 * var18 + var19 * var19 + var20 * var20;
   }

   public void restoreState(BufferBuilder.State bufferBuilder$State) {
      this.intBuffer.clear();
      this.ensureCapacity(bufferBuilder$State.array().length * 4);
      this.intBuffer.put(bufferBuilder$State.array());
      this.vertices = bufferBuilder$State.vertices();
      this.format = new VertexFormat(bufferBuilder$State.getFormat());
   }

   public void clear() {
      this.vertices = 0;
      this.currentElement = null;
      this.elementIndex = 0;
   }

   public void begin(int mode, VertexFormat format) {
      if(this.building) {
         throw new IllegalStateException("Already building!");
      } else {
         this.building = true;
         this.clear();
         this.mode = mode;
         this.format = format;
         this.currentElement = format.getElement(this.elementIndex);
         this.noColor = false;
         this.buffer.limit(this.buffer.capacity());
      }
   }

   public BufferBuilder uv(double var1, double var3) {
      int var5 = this.vertices * this.format.getVertexSize() + this.format.getOffset(this.elementIndex);
      switch(this.currentElement.getType()) {
      case FLOAT:
         this.buffer.putFloat(var5, (float)var1);
         this.buffer.putFloat(var5 + 4, (float)var3);
         break;
      case UINT:
      case INT:
         this.buffer.putInt(var5, (int)var1);
         this.buffer.putInt(var5 + 4, (int)var3);
         break;
      case USHORT:
      case SHORT:
         this.buffer.putShort(var5, (short)((int)var3));
         this.buffer.putShort(var5 + 2, (short)((int)var1));
         break;
      case UBYTE:
      case BYTE:
         this.buffer.put(var5, (byte)((int)var3));
         this.buffer.put(var5 + 1, (byte)((int)var1));
      }

      this.nextElement();
      return this;
   }

   public BufferBuilder uv2(int var1, int var2) {
      int var3 = this.vertices * this.format.getVertexSize() + this.format.getOffset(this.elementIndex);
      switch(this.currentElement.getType()) {
      case FLOAT:
         this.buffer.putFloat(var3, (float)var1);
         this.buffer.putFloat(var3 + 4, (float)var2);
         break;
      case UINT:
      case INT:
         this.buffer.putInt(var3, var1);
         this.buffer.putInt(var3 + 4, var2);
         break;
      case USHORT:
      case SHORT:
         this.buffer.putShort(var3, (short)var2);
         this.buffer.putShort(var3 + 2, (short)var1);
         break;
      case UBYTE:
      case BYTE:
         this.buffer.put(var3, (byte)var2);
         this.buffer.put(var3 + 1, (byte)var1);
      }

      this.nextElement();
      return this;
   }

   public void faceTex2(int var1, int var2, int var3, int var4) {
      int var5 = (this.vertices - 4) * this.format.getIntegerSize() + this.format.getUvOffset(1) / 4;
      int var6 = this.format.getVertexSize() >> 2;
      this.intBuffer.put(var5, var1);
      this.intBuffer.put(var5 + var6, var2);
      this.intBuffer.put(var5 + var6 * 2, var3);
      this.intBuffer.put(var5 + var6 * 3, var4);
   }

   public void postProcessFacePosition(double var1, double var3, double var5) {
      int var7 = this.format.getIntegerSize();
      int var8 = (this.vertices - 4) * var7;

      for(int var9 = 0; var9 < 4; ++var9) {
         int var10 = var8 + var9 * var7;
         int var11 = var10 + 1;
         int var12 = var11 + 1;
         this.intBuffer.put(var10, Float.floatToRawIntBits((float)(var1 + this.xo) + Float.intBitsToFloat(this.intBuffer.get(var10))));
         this.intBuffer.put(var11, Float.floatToRawIntBits((float)(var3 + this.yo) + Float.intBitsToFloat(this.intBuffer.get(var11))));
         this.intBuffer.put(var12, Float.floatToRawIntBits((float)(var5 + this.zo) + Float.intBitsToFloat(this.intBuffer.get(var12))));
      }

   }

   private int getStartingColorIndex(int i) {
      return ((this.vertices - i) * this.format.getVertexSize() + this.format.getColorOffset()) / 4;
   }

   public void faceTint(float var1, float var2, float var3, int var4) {
      int var5 = this.getStartingColorIndex(var4);
      int var6 = -1;
      if(!this.noColor) {
         var6 = this.intBuffer.get(var5);
         if(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            int var7 = (int)((float)(var6 & 255) * var1);
            int var8 = (int)((float)(var6 >> 8 & 255) * var2);
            int var9 = (int)((float)(var6 >> 16 & 255) * var3);
            var6 = var6 & -16777216;
            var6 = var6 | var9 << 16 | var8 << 8 | var7;
         } else {
            int var7 = (int)((float)(var6 >> 24 & 255) * var1);
            int var8 = (int)((float)(var6 >> 16 & 255) * var2);
            int var9 = (int)((float)(var6 >> 8 & 255) * var3);
            var6 = var6 & 255;
            var6 = var6 | var7 << 24 | var8 << 16 | var9 << 8;
         }
      }

      this.intBuffer.put(var5, var6);
   }

   private void fixupVertexColor(int var1, int var2) {
      int var3 = this.getStartingColorIndex(var2);
      int var4 = var1 >> 16 & 255;
      int var5 = var1 >> 8 & 255;
      int var6 = var1 & 255;
      this.putColor(var3, var4, var5, var6);
   }

   public void fixupVertexColor(float var1, float var2, float var3, int var4) {
      int var5 = this.getStartingColorIndex(var4);
      int var6 = clamp((int)(var1 * 255.0F), 0, 255);
      int var7 = clamp((int)(var2 * 255.0F), 0, 255);
      int var8 = clamp((int)(var3 * 255.0F), 0, 255);
      this.putColor(var5, var6, var7, var8);
   }

   private static int clamp(int var0, int var1, int var2) {
      return var0 < var1?var1:(var0 > var2?var2:var0);
   }

   private void putColor(int var1, int var2, int var3, int var4) {
      if(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
         this.intBuffer.put(var1, -16777216 | var4 << 16 | var3 << 8 | var2);
      } else {
         this.intBuffer.put(var1, var2 << 24 | var3 << 16 | var4 << 8 | 255);
      }

   }

   public void noColor() {
      this.noColor = true;
   }

   public BufferBuilder color(float var1, float var2, float var3, float var4) {
      return this.color((int)(var1 * 255.0F), (int)(var2 * 255.0F), (int)(var3 * 255.0F), (int)(var4 * 255.0F));
   }

   public BufferBuilder color(int var1, int var2, int var3, int var4) {
      if(this.noColor) {
         return this;
      } else {
         int var5 = this.vertices * this.format.getVertexSize() + this.format.getOffset(this.elementIndex);
         switch(this.currentElement.getType()) {
         case FLOAT:
            this.buffer.putFloat(var5, (float)var1 / 255.0F);
            this.buffer.putFloat(var5 + 4, (float)var2 / 255.0F);
            this.buffer.putFloat(var5 + 8, (float)var3 / 255.0F);
            this.buffer.putFloat(var5 + 12, (float)var4 / 255.0F);
            break;
         case UINT:
         case INT:
            this.buffer.putFloat(var5, (float)var1);
            this.buffer.putFloat(var5 + 4, (float)var2);
            this.buffer.putFloat(var5 + 8, (float)var3);
            this.buffer.putFloat(var5 + 12, (float)var4);
            break;
         case USHORT:
         case SHORT:
            this.buffer.putShort(var5, (short)var1);
            this.buffer.putShort(var5 + 2, (short)var2);
            this.buffer.putShort(var5 + 4, (short)var3);
            this.buffer.putShort(var5 + 6, (short)var4);
            break;
         case UBYTE:
         case BYTE:
            if(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
               this.buffer.put(var5, (byte)var1);
               this.buffer.put(var5 + 1, (byte)var2);
               this.buffer.put(var5 + 2, (byte)var3);
               this.buffer.put(var5 + 3, (byte)var4);
            } else {
               this.buffer.put(var5, (byte)var4);
               this.buffer.put(var5 + 1, (byte)var3);
               this.buffer.put(var5 + 2, (byte)var2);
               this.buffer.put(var5 + 3, (byte)var1);
            }
         }

         this.nextElement();
         return this;
      }
   }

   public void putBulkData(int[] ints) {
      this.ensureCapacity(ints.length * 4 + this.format.getVertexSize());
      this.intBuffer.position(this.getBufferIndex());
      this.intBuffer.put(ints);
      this.vertices += ints.length / this.format.getIntegerSize();
   }

   public void endVertex() {
      ++this.vertices;
      this.ensureCapacity(this.format.getVertexSize());
   }

   public BufferBuilder vertex(double var1, double var3, double var5) {
      int var7 = this.vertices * this.format.getVertexSize() + this.format.getOffset(this.elementIndex);
      switch(this.currentElement.getType()) {
      case FLOAT:
         this.buffer.putFloat(var7, (float)(var1 + this.xo));
         this.buffer.putFloat(var7 + 4, (float)(var3 + this.yo));
         this.buffer.putFloat(var7 + 8, (float)(var5 + this.zo));
         break;
      case UINT:
      case INT:
         this.buffer.putInt(var7, Float.floatToRawIntBits((float)(var1 + this.xo)));
         this.buffer.putInt(var7 + 4, Float.floatToRawIntBits((float)(var3 + this.yo)));
         this.buffer.putInt(var7 + 8, Float.floatToRawIntBits((float)(var5 + this.zo)));
         break;
      case USHORT:
      case SHORT:
         this.buffer.putShort(var7, (short)((int)(var1 + this.xo)));
         this.buffer.putShort(var7 + 2, (short)((int)(var3 + this.yo)));
         this.buffer.putShort(var7 + 4, (short)((int)(var5 + this.zo)));
         break;
      case UBYTE:
      case BYTE:
         this.buffer.put(var7, (byte)((int)(var1 + this.xo)));
         this.buffer.put(var7 + 1, (byte)((int)(var3 + this.yo)));
         this.buffer.put(var7 + 2, (byte)((int)(var5 + this.zo)));
      }

      this.nextElement();
      return this;
   }

   public void postNormal(float var1, float var2, float var3) {
      int var4 = (byte)((int)(var1 * 127.0F)) & 255;
      int var5 = (byte)((int)(var2 * 127.0F)) & 255;
      int var6 = (byte)((int)(var3 * 127.0F)) & 255;
      int var7 = var4 | var5 << 8 | var6 << 16;
      int var8 = this.format.getVertexSize() >> 2;
      int var9 = (this.vertices - 4) * var8 + this.format.getNormalOffset() / 4;
      this.intBuffer.put(var9, var7);
      this.intBuffer.put(var9 + var8, var7);
      this.intBuffer.put(var9 + var8 * 2, var7);
      this.intBuffer.put(var9 + var8 * 3, var7);
   }

   private void nextElement() {
      ++this.elementIndex;
      this.elementIndex %= this.format.getElementCount();
      this.currentElement = this.format.getElement(this.elementIndex);
      if(this.currentElement.getUsage() == VertexFormatElement.Usage.PADDING) {
         this.nextElement();
      }

   }

   public BufferBuilder normal(float var1, float var2, float var3) {
      int var4 = this.vertices * this.format.getVertexSize() + this.format.getOffset(this.elementIndex);
      switch(this.currentElement.getType()) {
      case FLOAT:
         this.buffer.putFloat(var4, var1);
         this.buffer.putFloat(var4 + 4, var2);
         this.buffer.putFloat(var4 + 8, var3);
         break;
      case UINT:
      case INT:
         this.buffer.putInt(var4, (int)var1);
         this.buffer.putInt(var4 + 4, (int)var2);
         this.buffer.putInt(var4 + 8, (int)var3);
         break;
      case USHORT:
      case SHORT:
         this.buffer.putShort(var4, (short)((int)var1 * 32767 & '\uffff'));
         this.buffer.putShort(var4 + 2, (short)((int)var2 * 32767 & '\uffff'));
         this.buffer.putShort(var4 + 4, (short)((int)var3 * 32767 & '\uffff'));
         break;
      case UBYTE:
      case BYTE:
         this.buffer.put(var4, (byte)((int)var1 * 127 & 255));
         this.buffer.put(var4 + 1, (byte)((int)var2 * 127 & 255));
         this.buffer.put(var4 + 2, (byte)((int)var3 * 127 & 255));
      }

      this.nextElement();
      return this;
   }

   public void offset(double xo, double yo, double zo) {
      this.xo = xo;
      this.yo = yo;
      this.zo = zo;
   }

   public void end() {
      if(!this.building) {
         throw new IllegalStateException("Not building!");
      } else {
         this.building = false;
         this.buffer.position(0);
         this.buffer.limit(this.getBufferIndex() * 4);
      }
   }

   public ByteBuffer getBuffer() {
      return this.buffer;
   }

   public VertexFormat getVertexFormat() {
      return this.format;
   }

   public int getVertexCount() {
      return this.vertices;
   }

   public int getDrawMode() {
      return this.mode;
   }

   public void fixupQuadColor(int i) {
      for(int var2 = 0; var2 < 4; ++var2) {
         this.fixupVertexColor(i, var2 + 1);
      }

   }

   public void fixupQuadColor(float var1, float var2, float var3) {
      for(int var4 = 0; var4 < 4; ++var4) {
         this.fixupVertexColor(var1, var2, var3, var4 + 1);
      }

   }

   @ClientJarOnly
   public class State {
      private final int[] array;
      private final VertexFormat format;

      public State(int[] array, VertexFormat format) {
         this.array = array;
         this.format = format;
      }

      public int[] array() {
         return this.array;
      }

      public int vertices() {
         return this.array.length / this.format.getIntegerSize();
      }

      public VertexFormat getFormat() {
         return this.format;
      }
   }
}
