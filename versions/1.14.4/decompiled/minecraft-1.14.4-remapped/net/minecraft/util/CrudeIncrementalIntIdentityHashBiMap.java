package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.IdMap;
import net.minecraft.util.Mth;

public class CrudeIncrementalIntIdentityHashBiMap implements IdMap {
   private static final Object EMPTY_SLOT = null;
   private Object[] keys;
   private int[] values;
   private Object[] byId;
   private int nextId;
   private int size;

   public CrudeIncrementalIntIdentityHashBiMap(int keys) {
      keys = (int)((float)keys / 0.8F);
      this.keys = (Object[])(new Object[keys]);
      this.values = new int[keys];
      this.byId = (Object[])(new Object[keys]);
   }

   public int getId(@Nullable Object object) {
      return this.getValue(this.indexOf(object, this.hash(object)));
   }

   @Nullable
   public Object byId(int id) {
      return id >= 0 && id < this.byId.length?this.byId[id]:null;
   }

   private int getValue(int i) {
      return i == -1?-1:this.values[i];
   }

   public int add(Object object) {
      int var2 = this.nextId();
      this.addMapping(object, var2);
      return var2;
   }

   private int nextId() {
      while(this.nextId < this.byId.length && this.byId[this.nextId] != null) {
         ++this.nextId;
      }

      return this.nextId;
   }

   private void grow(int keys) {
      K[] vars2 = this.keys;
      int[] vars3 = this.values;
      this.keys = (Object[])(new Object[keys]);
      this.values = new int[keys];
      this.byId = (Object[])(new Object[keys]);
      this.nextId = 0;
      this.size = 0;

      for(int var4 = 0; var4 < vars2.length; ++var4) {
         if(vars2[var4] != null) {
            this.addMapping(vars2[var4], vars3[var4]);
         }
      }

   }

   public void addMapping(Object object, int var2) {
      int var3 = Math.max(var2, this.size + 1);
      if((float)var3 >= (float)this.keys.length * 0.8F) {
         int var4;
         for(var4 = this.keys.length << 1; var4 < var2; var4 <<= 1) {
            ;
         }

         this.grow(var4);
      }

      int var4 = this.findEmpty(this.hash(object));
      this.keys[var4] = object;
      this.values[var4] = var2;
      this.byId[var2] = object;
      ++this.size;
      if(var2 == this.nextId) {
         ++this.nextId;
      }

   }

   private int hash(@Nullable Object object) {
      return (Mth.murmurHash3Mixer(System.identityHashCode(object)) & Integer.MAX_VALUE) % this.keys.length;
   }

   private int indexOf(@Nullable Object object, int var2) {
      for(int var3 = var2; var3 < this.keys.length; ++var3) {
         if(this.keys[var3] == object) {
            return var3;
         }

         if(this.keys[var3] == EMPTY_SLOT) {
            return -1;
         }
      }

      for(int var3 = 0; var3 < var2; ++var3) {
         if(this.keys[var3] == object) {
            return var3;
         }

         if(this.keys[var3] == EMPTY_SLOT) {
            return -1;
         }
      }

      return -1;
   }

   private int findEmpty(int i) {
      for(int var2 = i; var2 < this.keys.length; ++var2) {
         if(this.keys[var2] == EMPTY_SLOT) {
            return var2;
         }
      }

      for(int var2 = 0; var2 < i; ++var2) {
         if(this.keys[var2] == EMPTY_SLOT) {
            return var2;
         }
      }

      throw new RuntimeException("Overflowed :(");
   }

   public Iterator iterator() {
      return Iterators.filter(Iterators.forArray(this.byId), Predicates.notNull());
   }

   public void clear() {
      Arrays.fill(this.keys, (Object)null);
      Arrays.fill(this.byId, (Object)null);
      this.nextId = 0;
      this.size = 0;
   }

   public int size() {
      return this.size;
   }
}
