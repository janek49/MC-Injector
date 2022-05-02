package net.minecraft.world.entity.ai.attributes;

import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.BaseAttribute;

public class RangedAttribute extends BaseAttribute {
   private final double minValue;
   private final double maxValue;
   private String importLegacyName;

   public RangedAttribute(@Nullable Attribute attribute, String string, double var3, double minValue, double maxValue) {
      super(attribute, string, var3);
      this.minValue = minValue;
      this.maxValue = maxValue;
      if(minValue > maxValue) {
         throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
      } else if(var3 < minValue) {
         throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
      } else if(var3 > maxValue) {
         throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
      }
   }

   public RangedAttribute importLegacyName(String importLegacyName) {
      this.importLegacyName = importLegacyName;
      return this;
   }

   public String getImportLegacyName() {
      return this.importLegacyName;
   }

   public double sanitizeValue(double d) {
      d = Mth.clamp(d, this.minValue, this.maxValue);
      return d;
   }
}
