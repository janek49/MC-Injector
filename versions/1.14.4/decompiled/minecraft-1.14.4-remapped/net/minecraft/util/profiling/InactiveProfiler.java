package net.minecraft.util.profiling;

import java.util.function.Supplier;
import net.minecraft.util.profiling.EmptyProfileResults;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.ProfileResults;

public class InactiveProfiler implements ProfileCollector {
   public static final InactiveProfiler INACTIVE = new InactiveProfiler();

   public void startTick() {
   }

   public void endTick() {
   }

   public void push(String string) {
   }

   public void push(Supplier supplier) {
   }

   public void pop() {
   }

   public void popPush(String string) {
   }

   public void popPush(Supplier supplier) {
   }

   public ProfileResults getResults() {
      return EmptyProfileResults.EMPTY;
   }
}
