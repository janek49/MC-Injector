package net.minecraft.world.level.storage.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.function.Supplier;

public class LootTableProblemCollector {
   private final Multimap problems;
   private final Supplier context;
   private String contextCache;

   public LootTableProblemCollector() {
      this(HashMultimap.create(), () -> {
         return "";
      });
   }

   public LootTableProblemCollector(Multimap problems, Supplier context) {
      this.problems = problems;
      this.context = context;
   }

   private String getContext() {
      if(this.contextCache == null) {
         this.contextCache = (String)this.context.get();
      }

      return this.contextCache;
   }

   public void reportProblem(String string) {
      this.problems.put(this.getContext(), string);
   }

   public LootTableProblemCollector forChild(String string) {
      return new LootTableProblemCollector(this.problems, () -> {
         return this.getContext() + string;
      });
   }

   public Multimap getProblems() {
      return ImmutableMultimap.copyOf(this.problems);
   }
}
