package net.minecraft.core;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class NonNullList extends AbstractList {
   private final List list;
   private final Object defaultValue;

   public static NonNullList create() {
      return new NonNullList();
   }

   public static NonNullList withSize(int var0, Object object) {
      Validate.notNull(object);
      Object[] vars2 = new Object[var0];
      Arrays.fill(vars2, object);
      return new NonNullList(Arrays.asList(vars2), object);
   }

   @SafeVarargs
   public static NonNullList of(Object var0, Object... objects) {
      return new NonNullList(Arrays.asList(objects), var0);
   }

   protected NonNullList() {
      this(new ArrayList(), (Object)null);
   }

   protected NonNullList(List list, @Nullable Object defaultValue) {
      this.list = list;
      this.defaultValue = defaultValue;
   }

   @Nonnull
   public Object get(int i) {
      return this.list.get(i);
   }

   public Object set(int var1, Object var2) {
      Validate.notNull(var2);
      return this.list.set(var1, var2);
   }

   public void add(int var1, Object object) {
      Validate.notNull(object);
      this.list.add(var1, object);
   }

   public Object remove(int i) {
      return this.list.remove(i);
   }

   public int size() {
      return this.list.size();
   }

   public void clear() {
      if(this.defaultValue == null) {
         super.clear();
      } else {
         for(int var1 = 0; var1 < this.size(); ++var1) {
            this.set(var1, this.defaultValue);
         }
      }

   }
}
