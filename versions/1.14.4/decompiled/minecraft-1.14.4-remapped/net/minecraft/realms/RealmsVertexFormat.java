package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.List;
import net.minecraft.realms.RealmsVertexFormatElement;

@ClientJarOnly
public class RealmsVertexFormat {
   private VertexFormat v;

   public RealmsVertexFormat(VertexFormat v) {
      this.v = v;
   }

   public RealmsVertexFormat from(VertexFormat v) {
      this.v = v;
      return this;
   }

   public VertexFormat getVertexFormat() {
      return this.v;
   }

   public void clear() {
      this.v.clear();
   }

   public int getUvOffset(int i) {
      return this.v.getUvOffset(i);
   }

   public int getElementCount() {
      return this.v.getElementCount();
   }

   public boolean hasColor() {
      return this.v.hasColor();
   }

   public boolean hasUv(int i) {
      return this.v.hasUv(i);
   }

   public RealmsVertexFormatElement getElement(int i) {
      return new RealmsVertexFormatElement(this.v.getElement(i));
   }

   public RealmsVertexFormat addElement(RealmsVertexFormatElement realmsVertexFormatElement) {
      return this.from(this.v.addElement(realmsVertexFormatElement.getVertexFormatElement()));
   }

   public int getColorOffset() {
      return this.v.getColorOffset();
   }

   public List getElements() {
      List<RealmsVertexFormatElement> list = Lists.newArrayList();

      for(VertexFormatElement var3 : this.v.getElements()) {
         list.add(new RealmsVertexFormatElement(var3));
      }

      return list;
   }

   public boolean hasNormal() {
      return this.v.hasNormal();
   }

   public int getVertexSize() {
      return this.v.getVertexSize();
   }

   public int getOffset(int i) {
      return this.v.getOffset(i);
   }

   public int getNormalOffset() {
      return this.v.getNormalOffset();
   }

   public int getIntegerSize() {
      return this.v.getIntegerSize();
   }

   public boolean equals(Object object) {
      return this.v.equals(object);
   }

   public int hashCode() {
      return this.v.hashCode();
   }

   public String toString() {
      return this.v.toString();
   }
}
