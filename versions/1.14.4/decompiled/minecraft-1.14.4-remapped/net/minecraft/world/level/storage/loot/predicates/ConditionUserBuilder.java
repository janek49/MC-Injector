package net.minecraft.world.level.storage.loot.predicates;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public interface ConditionUserBuilder {
   Object when(LootItemCondition.Builder var1);

   Object unwrap();
}
