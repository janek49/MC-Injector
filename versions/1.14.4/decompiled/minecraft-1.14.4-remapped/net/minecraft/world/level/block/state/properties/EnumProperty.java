package net.minecraft.world.level.block.state.properties;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.AbstractProperty;

public class EnumProperty extends AbstractProperty {
   private final ImmutableSet values;
   private final Map names = Maps.newHashMap();

   protected EnumProperty(String string, Class class, Collection collection) {
      super(string, class);
      this.values = ImmutableSet.copyOf(collection);

      for(T var5 : collection) {
         String var6 = ((StringRepresentable)var5).getSerializedName();
         if(this.names.containsKey(var6)) {
            throw new IllegalArgumentException("Multiple values have the same name \'" + var6 + "\'");
         }

         this.names.put(var6, var5);
      }

   }

   public Collection getPossibleValues() {
      return this.values;
   }

   public Optional getValue(String string) {
      return Optional.ofNullable(this.names.get(string));
   }

   public String getName(Enum enum) {
      return ((StringRepresentable)enum).getSerializedName();
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object instanceof EnumProperty && super.equals(object)) {
         EnumProperty<?> var2 = (EnumProperty)object;
         return this.values.equals(var2.values) && this.names.equals(var2.names);
      } else {
         return false;
      }
   }

   public int generateHashCode() {
      int var1 = super.generateHashCode();
      var1 = 31 * var1 + this.values.hashCode();
      var1 = 31 * var1 + this.names.hashCode();
      return var1;
   }

   public static EnumProperty create(String string, Class class) {
      return create(string, class, (Predicate)Predicates.alwaysTrue());
   }

   public static EnumProperty create(String string, Class class, Predicate predicate) {
      return create(string, class, (Collection)Arrays.stream(class.getEnumConstants()).filter(predicate).collect(Collectors.toList()));
   }

   public static EnumProperty create(String string, Class class, Enum... enums) {
      return create(string, class, (Collection)Lists.newArrayList(enums));
   }

   public static EnumProperty create(String string, Class class, Collection collection) {
      return new EnumProperty(string, class, collection);
   }
}
