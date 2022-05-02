package com.mojang.blaze3d.vertex;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class VertexFormat {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List elements;
   private final List offsets;
   private int vertexSize;
   private int colorOffset;
   private final List texOffset;
   private int normalOffset;

   public VertexFormat(VertexFormat vertexFormat) {
      this();

      for(int var2 = 0; var2 < vertexFormat.getElementCount(); ++var2) {
         this.addElement(vertexFormat.getElement(var2));
      }

      this.vertexSize = vertexFormat.getVertexSize();
   }

   public VertexFormat() {
      this.elements = Lists.newArrayList();
      this.offsets = Lists.newArrayList();
      this.colorOffset = -1;
      this.texOffset = Lists.newArrayList();
      this.normalOffset = -1;
   }

   public void clear() {
      this.elements.clear();
      this.offsets.clear();
      this.colorOffset = -1;
      this.texOffset.clear();
      this.normalOffset = -1;
      this.vertexSize = 0;
   }

   public VertexFormat addElement(VertexFormatElement vertexFormatElement) {
      if(vertexFormatElement.isPosition() && this.hasPositionElement()) {
         LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
         return this;
      } else {
         this.elements.add(vertexFormatElement);
         this.offsets.add(Integer.valueOf(this.vertexSize));
         switch(vertexFormatElement.getUsage()) {
         case NORMAL:
            this.normalOffset = this.vertexSize;
            break;
         case COLOR:
            this.colorOffset = this.vertexSize;
            break;
         case UV:
            this.texOffset.add(vertexFormatElement.getIndex(), Integer.valueOf(this.vertexSize));
         }

         this.vertexSize += vertexFormatElement.getByteSize();
         return this;
      }
   }

   public boolean hasNormal() {
      return this.normalOffset >= 0;
   }

   public int getNormalOffset() {
      return this.normalOffset;
   }

   public boolean hasColor() {
      return this.colorOffset >= 0;
   }

   public int getColorOffset() {
      return this.colorOffset;
   }

   public boolean hasUv(int i) {
      return this.texOffset.size() - 1 >= i;
   }

   public int getUvOffset(int i) {
      return ((Integer)this.texOffset.get(i)).intValue();
   }

   public String toString() {
      String string = "format: " + this.elements.size() + " elements: ";

      for(int var2 = 0; var2 < this.elements.size(); ++var2) {
         string = string + ((VertexFormatElement)this.elements.get(var2)).toString();
         if(var2 != this.elements.size() - 1) {
            string = string + " ";
         }
      }

      return string;
   }

   private boolean hasPositionElement() {
      int var1 = 0;

      for(int var2 = this.elements.size(); var1 < var2; ++var1) {
         VertexFormatElement var3 = (VertexFormatElement)this.elements.get(var1);
         if(var3.isPosition()) {
            return true;
         }
      }

      return false;
   }

   public int getIntegerSize() {
      return this.getVertexSize() / 4;
   }

   public int getVertexSize() {
      return this.vertexSize;
   }

   public List getElements() {
      return this.elements;
   }

   public int getElementCount() {
      return this.elements.size();
   }

   public VertexFormatElement getElement(int i) {
      return (VertexFormatElement)this.elements.get(i);
   }

   public int getOffset(int i) {
      return ((Integer)this.offsets.get(i)).intValue();
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object != null && this.getClass() == object.getClass()) {
         VertexFormat var2 = (VertexFormat)object;
         return this.vertexSize != var2.vertexSize?false:(!this.elements.equals(var2.elements)?false:this.offsets.equals(var2.offsets));
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.elements.hashCode();
      var1 = 31 * var1 + this.offsets.hashCode();
      var1 = 31 * var1 + this.vertexSize;
      return var1;
   }
}
