package net.minecraft.world.level.storage.loot.entries;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootContext;

@FunctionalInterface
interface ComposableEntryContainer {
   ComposableEntryContainer ALWAYS_FALSE = (lootContext, consumer) -> {
      return false;
   };
   ComposableEntryContainer ALWAYS_TRUE = (lootContext, consumer) -> {
      return true;
   };

   boolean expand(LootContext var1, Consumer var2);

   default ComposableEntryContainer and(ComposableEntryContainer composableEntryContainer) {
      Objects.requireNonNull(composableEntryContainer);
      return (lootContext, consumer) -> {
         return this.expand(lootContext, consumer) && composableEntryContainer.expand(lootContext, consumer);
      };
   }

   default ComposableEntryContainer or(ComposableEntryContainer composableEntryContainer) {
      Objects.requireNonNull(composableEntryContainer);
      return (lootContext, consumer) -> {
         return this.expand(lootContext, consumer) || composableEntryContainer.expand(lootContext, consumer);
      };
   }
}
