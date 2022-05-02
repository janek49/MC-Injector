package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import net.minecraft.world.level.block.state.properties.AbstractProperty;

public class IntegerProperty extends AbstractProperty {
   private final ImmutableSet values;

   protected IntegerProperty(String string, int var2, int var3) {
      super(string, Integer.class);
      if(var2 < 0) {
         throw new IllegalArgumentException("Min value of " + string + " must be 0 or greater");
      } else if(var3 <= var2) {
         throw new IllegalArgumentException("Max value of " + string + " must be greater than min (" + var2 + ")");
      } else {
         Set<Integer> var4 = Sets.newHashSet();

         for(int var5 = var2; var5 <= var3; ++var5) {
            var4.add(Integer.valueOf(var5));
         }

         this.values = ImmutableSet.copyOf(var4);
      }
   }

   public Collection getPossibleValues() {
      return this.values;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object instanceof IntegerProperty && super.equals(object)) {
         IntegerProperty var2 = (IntegerProperty)object;
         return this.values.equals(var2.values);
      } else {
         return false;
      }
   }

   public int generateHashCode() {
      return 31 * super.generateHashCode() + this.values.hashCode();
   }

   public static IntegerProperty create(String string, int var1, int var2) {
      return new IntegerProperty(string, var1, var2);
   }

   public Optional getValue(String string) {
      try {
         Integer var2 = Integer.valueOf(string);
         return this.values.contains(var2)?Optional.of(var2):Optional.empty();
      } catch (NumberFormatException var3) {
         return Optional.empty();
      }
   }

   public String getName(Integer integer) {
      return integer.toString();
   }
}
