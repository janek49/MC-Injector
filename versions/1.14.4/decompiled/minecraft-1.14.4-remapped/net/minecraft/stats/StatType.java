package net.minecraft.stats;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatFormatter;

public class StatType implements Iterable {
   private final Registry registry;
   private final Map map = new IdentityHashMap();

   public StatType(Registry registry) {
      this.registry = registry;
   }

   public boolean contains(Object object) {
      return this.map.containsKey(object);
   }

   public Stat get(Object object, StatFormatter statFormatter) {
      return (Stat)this.map.computeIfAbsent(object, (object) -> {
         return new Stat(this, object, statFormatter);
      });
   }

   public Registry getRegistry() {
      return this.registry;
   }

   public Iterator iterator() {
      return this.map.values().iterator();
   }

   public Stat get(Object object) {
      return this.get(object, StatFormatter.DEFAULT);
   }

   public String getTranslationKey() {
      return "stat_type." + Registry.STAT_TYPE.getKey(this).toString().replace(':', '.');
   }
}
