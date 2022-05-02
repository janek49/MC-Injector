package net.minecraft.world.level.block.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.MapFiller;
import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

public class StateDefinition {
   private static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
   private final Object owner;
   private final ImmutableSortedMap propertiesByName;
   private final ImmutableList states;

   protected StateDefinition(Object owner, StateDefinition.Factory stateDefinition$Factory, Map map) {
      this.owner = owner;
      this.propertiesByName = ImmutableSortedMap.copyOf(map);
      Map<Map<Property<?>, Comparable<?>>, A> map = Maps.newLinkedHashMap();
      List<A> var5 = Lists.newArrayList();
      Stream<List<Comparable<?>>> var6 = Stream.of(Collections.emptyList());

      Property<?> var8;
      for(UnmodifiableIterator var7 = this.propertiesByName.values().iterator(); var7.hasNext(); var6 = var6.flatMap((list) -> {
         return var8.getPossibleValues().stream().map((comparable) -> {
            List<Comparable<?>> var2 = Lists.newArrayList(list);
            var2.add(comparable);
            return var2;
         });
      })) {
         var8 = (Property)var7.next();
      }

      var6.forEach((var5x) -> {
         Map<Property<?>, Comparable<?>> map = MapFiller.linkedHashMapFrom(this.propertiesByName.values(), var5x);
         A var7 = stateDefinition$Factory.create(owner, ImmutableMap.copyOf(map));
         map.put(map, var7);
         var5.add(var7);
      });

      for(A var8 : var5) {
         var8.populateNeighbours(map);
      }

      this.states = ImmutableList.copyOf(var5);
   }

   public ImmutableList getPossibleStates() {
      return this.states;
   }

   public StateHolder any() {
      return (StateHolder)this.states.get(0);
   }

   public Object getOwner() {
      return this.owner;
   }

   public Collection getProperties() {
      return this.propertiesByName.values();
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("block", this.owner).add("properties", this.propertiesByName.values().stream().map(Property::getName).collect(Collectors.toList())).toString();
   }

   @Nullable
   public Property getProperty(String string) {
      return (Property)this.propertiesByName.get(string);
   }

   public static class Builder {
      private final Object owner;
      private final Map properties = Maps.newHashMap();

      public Builder(Object owner) {
         this.owner = owner;
      }

      public StateDefinition.Builder add(Property... propertys) {
         for(Property<?> var5 : propertys) {
            this.validateProperty(var5);
            this.properties.put(var5.getName(), var5);
         }

         return this;
      }

      private void validateProperty(Property property) {
         String var2 = property.getName();
         if(!StateDefinition.NAME_PATTERN.matcher(var2).matches()) {
            throw new IllegalArgumentException(this.owner + " has invalidly named property: " + var2);
         } else {
            Collection<T> var3 = property.getPossibleValues();
            if(var3.size() <= 1) {
               throw new IllegalArgumentException(this.owner + " attempted use property " + var2 + " with <= 1 possible values");
            } else {
               for(T var5 : var3) {
                  String var6 = property.getName(var5);
                  if(!StateDefinition.NAME_PATTERN.matcher(var6).matches()) {
                     throw new IllegalArgumentException(this.owner + " has property: " + var2 + " with invalidly named value: " + var6);
                  }
               }

               if(this.properties.containsKey(var2)) {
                  throw new IllegalArgumentException(this.owner + " has duplicate property: " + var2);
               }
            }
         }
      }

      public StateDefinition create(StateDefinition.Factory stateDefinition$Factory) {
         return new StateDefinition(this.owner, stateDefinition$Factory, this.properties);
      }
   }

   public interface Factory {
      AbstractStateHolder create(Object var1, ImmutableMap var2);
   }
}
