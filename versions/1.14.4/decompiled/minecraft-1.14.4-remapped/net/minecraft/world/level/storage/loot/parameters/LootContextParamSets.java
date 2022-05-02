package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootContextParamSets {
   private static final BiMap REGISTRY = HashBiMap.create();
   public static final LootContextParamSet EMPTY = register("empty", (lootContextParamSet$Builder) -> {
   });
   public static final LootContextParamSet CHEST = register("chest", (lootContextParamSet$Builder) -> {
      lootContextParamSet$Builder.required(LootContextParams.BLOCK_POS).optional(LootContextParams.THIS_ENTITY);
   });
   public static final LootContextParamSet FISHING = register("fishing", (lootContextParamSet$Builder) -> {
      lootContextParamSet$Builder.required(LootContextParams.BLOCK_POS).required(LootContextParams.TOOL);
   });
   public static final LootContextParamSet ENTITY = register("entity", (lootContextParamSet$Builder) -> {
      lootContextParamSet$Builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.BLOCK_POS).required(LootContextParams.DAMAGE_SOURCE).optional(LootContextParams.KILLER_ENTITY).optional(LootContextParams.DIRECT_KILLER_ENTITY).optional(LootContextParams.LAST_DAMAGE_PLAYER);
   });
   public static final LootContextParamSet GIFT = register("gift", (lootContextParamSet$Builder) -> {
      lootContextParamSet$Builder.required(LootContextParams.BLOCK_POS).required(LootContextParams.THIS_ENTITY);
   });
   public static final LootContextParamSet ADVANCEMENT_REWARD = register("advancement_reward", (lootContextParamSet$Builder) -> {
      lootContextParamSet$Builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.BLOCK_POS);
   });
   public static final LootContextParamSet ALL_PARAMS = register("generic", (lootContextParamSet$Builder) -> {
      lootContextParamSet$Builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.LAST_DAMAGE_PLAYER).required(LootContextParams.DAMAGE_SOURCE).required(LootContextParams.KILLER_ENTITY).required(LootContextParams.DIRECT_KILLER_ENTITY).required(LootContextParams.BLOCK_POS).required(LootContextParams.BLOCK_STATE).required(LootContextParams.BLOCK_ENTITY).required(LootContextParams.TOOL).required(LootContextParams.EXPLOSION_RADIUS);
   });
   public static final LootContextParamSet BLOCK = register("block", (lootContextParamSet$Builder) -> {
      lootContextParamSet$Builder.required(LootContextParams.BLOCK_STATE).required(LootContextParams.BLOCK_POS).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY).optional(LootContextParams.BLOCK_ENTITY).optional(LootContextParams.EXPLOSION_RADIUS);
   });

   private static LootContextParamSet register(String string, Consumer consumer) {
      LootContextParamSet.Builder var2 = new LootContextParamSet.Builder();
      consumer.accept(var2);
      LootContextParamSet var3 = var2.build();
      ResourceLocation var4 = new ResourceLocation(string);
      LootContextParamSet var5 = (LootContextParamSet)REGISTRY.put(var4, var3);
      if(var5 != null) {
         throw new IllegalStateException("Loot table parameter set " + var4 + " is already registered");
      } else {
         return var3;
      }
   }

   @Nullable
   public static LootContextParamSet get(ResourceLocation resourceLocation) {
      return (LootContextParamSet)REGISTRY.get(resourceLocation);
   }

   @Nullable
   public static ResourceLocation getKey(LootContextParamSet lootContextParamSet) {
      return (ResourceLocation)REGISTRY.inverse().get(lootContextParamSet);
   }
}
