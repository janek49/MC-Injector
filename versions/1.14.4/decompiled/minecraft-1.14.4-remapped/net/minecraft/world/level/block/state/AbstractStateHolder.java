package net.minecraft.world.level.block.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class AbstractStateHolder implements StateHolder {
   private static final Function PROPERTY_ENTRY_TO_STRING_FUNCTION = new Function() {
      public String apply(@Nullable Entry map$Entry) {
         if(map$Entry == null) {
            return "<NULL>";
         } else {
            Property<?> var2 = (Property)map$Entry.getKey();
            return var2.getName() + "=" + this.getName(var2, (Comparable)map$Entry.getValue());
         }
      }

      private String getName(Property property, Comparable comparable) {
         return property.getName(comparable);
      }

      // $FF: synthetic method
      public Object apply(@Nullable Object var1) {
         return this.apply((Entry)var1);
      }
   };
   protected final Object owner;
   private final ImmutableMap values;
   private final int hashCode;
   private Table neighbours;

   protected AbstractStateHolder(Object owner, ImmutableMap values) {
      this.owner = owner;
      this.values = values;
      this.hashCode = values.hashCode();
   }

   public Object cycle(Property property) {
      return this.setValue(property, (Comparable)findNextInCollection(property.getPossibleValues(), this.getValue(property)));
   }

   protected static Object findNextInCollection(Collection collection, Object var1) {
      Iterator<T> var2 = collection.iterator();

      while(var2.hasNext()) {
         if(var2.next().equals(var1)) {
            if(var2.hasNext()) {
               return var2.next();
            }

            return collection.iterator().next();
         }
      }

      return var2.next();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.owner);
      if(!this.getValues().isEmpty()) {
         var1.append('[');
         var1.append((String)this.getValues().entrySet().stream().map(PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
         var1.append(']');
      }

      return var1.toString();
   }

   public Collection getProperties() {
      return Collections.unmodifiableCollection(this.values.keySet());
   }

   public boolean hasProperty(Property property) {
      return this.values.containsKey(property);
   }

   public Comparable getValue(Property property) {
      Comparable<?> comparable = (Comparable)this.values.get(property);
      if(comparable == null) {
         throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.owner);
      } else {
         return (Comparable)property.getValueClass().cast(comparable);
      }
   }

   public Object setValue(Property property, Comparable comparable) {
      Comparable<?> comparable = (Comparable)this.values.get(property);
      if(comparable == null) {
         throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.owner);
      } else if(comparable == comparable) {
         return this;
      } else {
         S var4 = this.neighbours.get(property, comparable);
         if(var4 == null) {
            throw new IllegalArgumentException("Cannot set property " + property + " to " + comparable + " on " + this.owner + ", it is not an allowed value");
         } else {
            return var4;
         }
      }
   }

   public void populateNeighbours(Map map) {
      if(this.neighbours != null) {
         throw new IllegalStateException();
      } else {
         Table<Property<?>, Comparable<?>, S> var2 = HashBasedTable.create();
         UnmodifiableIterator var3 = this.values.entrySet().iterator();

         while(var3.hasNext()) {
            Entry<Property<?>, Comparable<?>> var4 = (Entry)var3.next();
            Property<?> var5 = (Property)var4.getKey();

            for(Comparable<?> var7 : var5.getPossibleValues()) {
               if(var7 != var4.getValue()) {
                  var2.put(var5, var7, map.get(this.makeNeighbourValues(var5, var7)));
               }
            }
         }

         this.neighbours = (Table)(var2.isEmpty()?var2:ArrayTable.create(var2));
      }
   }

   private Map makeNeighbourValues(Property property, Comparable comparable) {
      Map<Property<?>, Comparable<?>> map = Maps.newHashMap(this.values);
      map.put(property, comparable);
      return map;
   }

   public ImmutableMap getValues() {
      return this.values;
   }

   public boolean equals(Object object) {
      return this == object;
   }

   public int hashCode() {
      return this.hashCode;
   }
}
