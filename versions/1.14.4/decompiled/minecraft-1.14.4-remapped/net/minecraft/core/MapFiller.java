package net.minecraft.core;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapFiller {
   public static Map linkedHashMapFrom(Iterable var0, Iterable var1) {
      return from(var0, var1, Maps.newLinkedHashMap());
   }

   public static Map from(Iterable var0, Iterable var1, Map var2) {
      Iterator<V> var3 = var1.iterator();

      for(K var5 : var0) {
         var2.put(var5, var3.next());
      }

      if(var3.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return var2;
      }
   }
}
