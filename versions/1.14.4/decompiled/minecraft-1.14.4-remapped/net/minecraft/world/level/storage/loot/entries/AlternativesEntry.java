package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.entries.ComposableEntryContainer;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativesEntry extends CompositeEntryBase {
   AlternativesEntry(LootPoolEntryContainer[] lootPoolEntryContainers, LootItemCondition[] lootItemConditions) {
      super(lootPoolEntryContainers, lootItemConditions);
   }

   protected ComposableEntryContainer compose(ComposableEntryContainer[] composableEntryContainers) {
      switch(composableEntryContainers.length) {
      case 0:
         return ALWAYS_FALSE;
      case 1:
         return composableEntryContainers[0];
      case 2:
         return composableEntryContainers[0].or(composableEntryContainers[1]);
      default:
         return (lootContext, consumer) -> {
            for(ComposableEntryContainer var6 : composableEntryContainers) {
               if(var6.expand(lootContext, consumer)) {
                  return true;
               }
            }

            return false;
         };
      }
   }

   public void validate(LootTableProblemCollector lootTableProblemCollector, Function function, Set set, LootContextParamSet lootContextParamSet) {
      super.validate(lootTableProblemCollector, function, set, lootContextParamSet);

      for(int var5 = 0; var5 < this.children.length - 1; ++var5) {
         if(ArrayUtils.isEmpty(this.children[var5].conditions)) {
            lootTableProblemCollector.reportProblem("Unreachable entry!");
         }
      }

   }

   public static AlternativesEntry.Builder alternatives(LootPoolEntryContainer.Builder... lootPoolEntryContainer$Builders) {
      return new AlternativesEntry.Builder(lootPoolEntryContainer$Builders);
   }

   public static class Builder extends LootPoolEntryContainer.Builder {
      private final List entries = Lists.newArrayList();

      public Builder(LootPoolEntryContainer.Builder... lootPoolEntryContainer$Builders) {
         for(LootPoolEntryContainer.Builder<?> var5 : lootPoolEntryContainer$Builders) {
            this.entries.add(var5.build());
         }

      }

      protected AlternativesEntry.Builder getThis() {
         return this;
      }

      public AlternativesEntry.Builder otherwise(LootPoolEntryContainer.Builder lootPoolEntryContainer$Builder) {
         this.entries.add(lootPoolEntryContainer$Builder.build());
         return this;
      }

      public LootPoolEntryContainer build() {
         return new AlternativesEntry((LootPoolEntryContainer[])this.entries.toArray(new LootPoolEntryContainer[0]), this.getConditions());
      }

      // $FF: synthetic method
      protected LootPoolEntryContainer.Builder getThis() {
         return this.getThis();
      }
   }
}
