package net.minecraft.world.level.storage.loot.entries;

import net.minecraft.world.level.storage.loot.entries.ComposableEntryContainer;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SequentialEntry extends CompositeEntryBase {
   SequentialEntry(LootPoolEntryContainer[] lootPoolEntryContainers, LootItemCondition[] lootItemConditions) {
      super(lootPoolEntryContainers, lootItemConditions);
   }

   protected ComposableEntryContainer compose(ComposableEntryContainer[] composableEntryContainers) {
      switch(composableEntryContainers.length) {
      case 0:
         return ALWAYS_TRUE;
      case 1:
         return composableEntryContainers[0];
      case 2:
         return composableEntryContainers[0].and(composableEntryContainers[1]);
      default:
         return (lootContext, consumer) -> {
            for(ComposableEntryContainer var6 : composableEntryContainers) {
               if(!var6.expand(lootContext, consumer)) {
                  return false;
               }
            }

            return true;
         };
      }
   }
}
