package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class InsensitiveStringMap implements Map {
   private final Map map = Maps.newLinkedHashMap();

   public int size() {
      return this.map.size();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public boolean containsKey(Object object) {
      return this.map.containsKey(object.toString().toLowerCase(Locale.ROOT));
   }

   public boolean containsValue(Object object) {
      return this.map.containsValue(object);
   }

   public Object get(Object object) {
      return this.map.get(object.toString().toLowerCase(Locale.ROOT));
   }

   public Object put(String string, Object var2) {
      return this.map.put(string.toLowerCase(Locale.ROOT), var2);
   }

   public Object remove(Object object) {
      return this.map.remove(object.toString().toLowerCase(Locale.ROOT));
   }

   public void putAll(Map map) {
      for(Entry<? extends String, ? extends V> var3 : map.entrySet()) {
         this.put((String)var3.getKey(), var3.getValue());
      }

   }

   public void clear() {
      this.map.clear();
   }

   public Set keySet() {
      return this.map.keySet();
   }

   public Collection values() {
      return this.map.values();
   }

   public Set entrySet() {
      return this.map.entrySet();
   }

   // $FF: synthetic method
   public Object put(Object var1, Object var2) {
      return this.put((String)var1, var2);
   }
}
