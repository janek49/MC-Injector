package net.minecraft.world.level.storage.loot.functions;

import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public interface FunctionUserBuilder {
   Object apply(LootItemFunction.Builder var1);

   Object unwrap();
}
