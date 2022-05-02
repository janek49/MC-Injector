package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.world.level.block.state.properties.AbstractProperty;

public class BooleanProperty extends AbstractProperty {
   private final ImmutableSet values = ImmutableSet.of(Boolean.valueOf(true), Boolean.valueOf(false));

   protected BooleanProperty(String string) {
      super(string, Boolean.class);
   }

   public Collection getPossibleValues() {
      return this.values;
   }

   public static BooleanProperty create(String string) {
      return new BooleanProperty(string);
   }

   public Optional getValue(String string) {
      return !"true".equals(string) && !"false".equals(string)?Optional.empty():Optional.of(Boolean.valueOf(string));
   }

   public String getName(Boolean boolean) {
      return boolean.toString();
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object instanceof BooleanProperty && super.equals(object)) {
         BooleanProperty var2 = (BooleanProperty)object;
         return this.values.equals(var2.values);
      } else {
         return false;
      }
   }

   public int generateHashCode() {
      return 31 * super.generateHashCode() + this.values.hashCode();
   }
}
