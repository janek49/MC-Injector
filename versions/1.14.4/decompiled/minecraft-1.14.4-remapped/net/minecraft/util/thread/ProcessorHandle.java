package net.minecraft.util.thread;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ProcessorHandle extends AutoCloseable {
   String name();

   void tell(Object var1);

   default void close() {
   }

   default CompletableFuture ask(Function function) {
      CompletableFuture<Source> completableFuture = new CompletableFuture();
      completableFuture.getClass();
      Msg var3 = function.apply(of("ask future procesor handle", completableFuture::complete));
      this.tell(var3);
      return completableFuture;
   }

   static default ProcessorHandle of(final String string, final Consumer consumer) {
      return new ProcessorHandle() {
         public String name() {
            return string;
         }

         public void tell(Object object) {
            consumer.accept(object);
         }

         public String toString() {
            return string;
         }
      };
   }
}
