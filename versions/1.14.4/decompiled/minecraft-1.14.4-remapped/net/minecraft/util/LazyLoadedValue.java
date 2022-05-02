package net.minecraft.util;

import java.util.function.Supplier;

public class LazyLoadedValue {
   private Supplier factory;
   private Object value;

   public LazyLoadedValue(Supplier factory) {
      this.factory = factory;
   }

   public Object get() {
      Supplier<T> var1 = this.factory;
      if(var1 != null) {
         this.value = var1.get();
         this.factory = null;
      }

      return this.value;
   }
}
