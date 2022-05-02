package net.minecraft.core;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.IdMap;

public class IdMapper implements IdMap {
   private int nextId;
   private final IdentityHashMap tToId;
   private final List idToT;

   public IdMapper() {
      this(512);
   }

   public IdMapper(int i) {
      this.idToT = Lists.newArrayListWithExpectedSize(i);
      this.tToId = new IdentityHashMap(i);
   }

   public void addMapping(Object object, int var2) {
      this.tToId.put(object, Integer.valueOf(var2));

      while(this.idToT.size() <= var2) {
         this.idToT.add((Object)null);
      }

      this.idToT.set(var2, object);
      if(this.nextId <= var2) {
         this.nextId = var2 + 1;
      }

   }

   public void add(Object object) {
      this.addMapping(object, this.nextId);
   }

   public int getId(Object object) {
      Integer var2 = (Integer)this.tToId.get(object);
      return var2 == null?-1:var2.intValue();
   }

   @Nullable
   public final Object byId(int id) {
      return id >= 0 && id < this.idToT.size()?this.idToT.get(id):null;
   }

   public Iterator iterator() {
      return Iterators.filter(this.idToT.iterator(), Predicates.notNull());
   }

   public int size() {
      return this.tToId.size();
   }
}
