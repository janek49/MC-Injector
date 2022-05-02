package net.minecraft.util.profiling;

import java.util.function.Supplier;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;

public interface ProfileCollector extends ProfilerFiller {
   void push(String var1);

   void push(Supplier var1);

   void pop();

   void popPush(String var1);

   void popPush(Supplier var1);

   ProfileResults getResults();
}
