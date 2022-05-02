package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.vertex.VertexFormatElement;

@ClientJarOnly
public class RealmsVertexFormatElement {
   private final VertexFormatElement v;

   public RealmsVertexFormatElement(VertexFormatElement v) {
      this.v = v;
   }

   public VertexFormatElement getVertexFormatElement() {
      return this.v;
   }

   public boolean isPosition() {
      return this.v.isPosition();
   }

   public int getIndex() {
      return this.v.getIndex();
   }

   public int getByteSize() {
      return this.v.getByteSize();
   }

   public int getCount() {
      return this.v.getCount();
   }

   public int hashCode() {
      return this.v.hashCode();
   }

   public boolean equals(Object object) {
      return this.v.equals(object);
   }

   public String toString() {
      return this.v.toString();
   }
}
