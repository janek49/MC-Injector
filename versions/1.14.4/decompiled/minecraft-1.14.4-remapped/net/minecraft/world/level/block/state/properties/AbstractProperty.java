package net.minecraft.world.level.block.state.properties;

import com.google.common.base.MoreObjects;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class AbstractProperty implements Property {
   private final Class clazz;
   private final String name;
   private Integer hashCode;

   protected AbstractProperty(String name, Class clazz) {
      this.clazz = clazz;
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public Class getValueClass() {
      return this.clazz;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.clazz).add("values", this.getPossibleValues()).toString();
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof AbstractProperty)) {
         return false;
      } else {
         AbstractProperty<?> var2 = (AbstractProperty)object;
         return this.clazz.equals(var2.clazz) && this.name.equals(var2.name);
      }
   }

   public final int hashCode() {
      if(this.hashCode == null) {
         this.hashCode = Integer.valueOf(this.generateHashCode());
      }

      return this.hashCode.intValue();
   }

   public int generateHashCode() {
      return 31 * this.clazz.hashCode() + this.name.hashCode();
   }
}
