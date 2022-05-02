package com.mojang.blaze3d.vertex;

import com.fox2code.repacker.ClientJarOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class VertexFormatElement {
   private static final Logger LOGGER = LogManager.getLogger();
   private final VertexFormatElement.Type type;
   private final VertexFormatElement.Usage usage;
   private final int index;
   private final int count;

   public VertexFormatElement(int index, VertexFormatElement.Type type, VertexFormatElement.Usage usage, int count) {
      if(this.supportsUsage(index, usage)) {
         this.usage = usage;
      } else {
         LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
         this.usage = VertexFormatElement.Usage.UV;
      }

      this.type = type;
      this.index = index;
      this.count = count;
   }

   private final boolean supportsUsage(int var1, VertexFormatElement.Usage vertexFormatElement$Usage) {
      return var1 == 0 || vertexFormatElement$Usage == VertexFormatElement.Usage.UV;
   }

   public final VertexFormatElement.Type getType() {
      return this.type;
   }

   public final VertexFormatElement.Usage getUsage() {
      return this.usage;
   }

   public final int getCount() {
      return this.count;
   }

   public final int getIndex() {
      return this.index;
   }

   public String toString() {
      return this.count + "," + this.usage.getName() + "," + this.type.getName();
   }

   public final int getByteSize() {
      return this.type.getSize() * this.count;
   }

   public final boolean isPosition() {
      return this.usage == VertexFormatElement.Usage.POSITION;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object != null && this.getClass() == object.getClass()) {
         VertexFormatElement var2 = (VertexFormatElement)object;
         return this.count != var2.count?false:(this.index != var2.index?false:(this.type != var2.type?false:this.usage == var2.usage));
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.type.hashCode();
      var1 = 31 * var1 + this.usage.hashCode();
      var1 = 31 * var1 + this.index;
      var1 = 31 * var1 + this.count;
      return var1;
   }

   @ClientJarOnly
   public static enum Type {
      FLOAT(4, "Float", 5126),
      UBYTE(1, "Unsigned Byte", 5121),
      BYTE(1, "Byte", 5120),
      USHORT(2, "Unsigned Short", 5123),
      SHORT(2, "Short", 5122),
      UINT(4, "Unsigned Int", 5125),
      INT(4, "Int", 5124);

      private final int size;
      private final String name;
      private final int glType;

      private Type(int size, String name, int glType) {
         this.size = size;
         this.name = name;
         this.glType = glType;
      }

      public int getSize() {
         return this.size;
      }

      public String getName() {
         return this.name;
      }

      public int getGlType() {
         return this.glType;
      }
   }

   @ClientJarOnly
   public static enum Usage {
      POSITION("Position"),
      NORMAL("Normal"),
      COLOR("Vertex Color"),
      UV("UV"),
      MATRIX("Bone Matrix"),
      BLEND_WEIGHT("Blend Weight"),
      PADDING("Padding");

      private final String name;

      private Usage(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }
   }
}
