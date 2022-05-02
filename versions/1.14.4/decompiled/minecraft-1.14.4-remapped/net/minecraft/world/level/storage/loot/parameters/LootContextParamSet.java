package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class LootContextParamSet {
   private final Set required;
   private final Set all;

   private LootContextParamSet(Set var1, Set var2) {
      this.required = ImmutableSet.copyOf(var1);
      this.all = ImmutableSet.copyOf(Sets.union(var1, var2));
   }

   public Set getRequired() {
      return this.required;
   }

   public Set getAllowed() {
      return this.all;
   }

   public String toString() {
      return "[" + Joiner.on(", ").join(this.all.stream().map((lootContextParam) -> {
         return (this.required.contains(lootContextParam)?"!":"") + lootContextParam.getName();
      }).iterator()) + "]";
   }

   public void validateUser(LootTableProblemCollector lootTableProblemCollector, LootContextUser lootContextUser) {
      Set<LootContextParam<?>> var3 = lootContextUser.getReferencedContextParams();
      Set<LootContextParam<?>> var4 = Sets.difference(var3, this.all);
      if(!var4.isEmpty()) {
         lootTableProblemCollector.reportProblem("Parameters " + var4 + " are not provided in this context");
      }

   }

   public static class Builder {
      private final Set required = Sets.newIdentityHashSet();
      private final Set optional = Sets.newIdentityHashSet();

      public LootContextParamSet.Builder required(LootContextParam lootContextParam) {
         if(this.optional.contains(lootContextParam)) {
            throw new IllegalArgumentException("Parameter " + lootContextParam.getName() + " is already optional");
         } else {
            this.required.add(lootContextParam);
            return this;
         }
      }

      public LootContextParamSet.Builder optional(LootContextParam lootContextParam) {
         if(this.required.contains(lootContextParam)) {
            throw new IllegalArgumentException("Parameter " + lootContextParam.getName() + " is already required");
         } else {
            this.optional.add(lootContextParam);
            return this;
         }
      }

      public LootContextParamSet build() {
         return new LootContextParamSet(this.required, this.optional);
      }
   }
}
