package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public interface LootContextUser {
   default Set getReferencedContextParams() {
      return ImmutableSet.of();
   }

   default void validate(LootTableProblemCollector lootTableProblemCollector, Function function, Set set, LootContextParamSet lootContextParamSet) {
      lootContextParamSet.validateUser(lootTableProblemCollector, this);
   }
}
