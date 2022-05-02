package net.minecraft.world.level.storage.loot.entries;

import net.minecraft.world.level.storage.loot.entries.ComposableEntryContainer;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EntryGroup extends CompositeEntryBase {
   EntryGroup(LootPoolEntryContainer[] lootPoolEntryContainers, LootItemCondition[] lootItemConditions) {
      super(lootPoolEntryContainers, lootItemConditions);
   }

   protected ComposableEntryContainer compose(ComposableEntryContainer[] composableEntryContainers) {
      switch(composableEntryContainers.length) {
      case 0:
         return ALWAYS_TRUE;
      case 1:
         return composableEntryContainers[0];
      case 2:
         ComposableEntryContainer var2 = composableEntryContainers[0];
         ComposableEntryContainer var3 = composableEntryContainers[1];
         return (lootContext, consumer) -> {
            var2.expand(lootContext, consumer);
            var3.expand(lootContext, consumer);
            return true;
         };
      default:
         return (lootContext, consumer) -> {
            for(ComposableEntryContainer var6 : composableEntryContainers) {
               var6.expand(lootContext, consumer);
            }

            return true;
         };
      }
   }
}
