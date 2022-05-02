package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassInstanceMultiMap extends AbstractCollection {
   private final Map byClass = Maps.newHashMap();
   private final Class baseClass;
   private final List allInstances = Lists.newArrayList();

   public ClassInstanceMultiMap(Class baseClass) {
      this.baseClass = baseClass;
      this.byClass.put(baseClass, this.allInstances);
   }

   public boolean add(Object object) {
      boolean var2 = false;

      for(Entry<Class<?>, List<T>> var4 : this.byClass.entrySet()) {
         if(((Class)var4.getKey()).isInstance(object)) {
            var2 |= ((List)var4.getValue()).add(object);
         }
      }

      return var2;
   }

   public boolean remove(Object object) {
      boolean var2 = false;

      for(Entry<Class<?>, List<T>> var4 : this.byClass.entrySet()) {
         if(((Class)var4.getKey()).isInstance(object)) {
            List<T> var5 = (List)var4.getValue();
            var2 |= var5.remove(object);
         }
      }

      return var2;
   }

   public boolean contains(Object object) {
      return this.find(object.getClass()).contains(object);
   }

   public Collection find(Class class) {
      if(!this.baseClass.isAssignableFrom(class)) {
         throw new IllegalArgumentException("Don\'t know how to search for " + class);
      } else {
         List<T> var2 = (List)this.byClass.computeIfAbsent(class, (class) -> {
            Stream var10000 = this.allInstances.stream();
            class.getClass();
            return (List)var10000.filter(class::isInstance).collect(Collectors.toList());
         });
         return Collections.unmodifiableCollection(var2);
      }
   }

   public Iterator iterator() {
      return (Iterator)(this.allInstances.isEmpty()?Collections.emptyIterator():Iterators.unmodifiableIterator(this.allInstances.iterator()));
   }

   public int size() {
      return this.allInstances.size();
   }
}
