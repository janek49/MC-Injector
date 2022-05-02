package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyNameFunction extends LootItemConditionalFunction {
   private final CopyNameFunction.NameSource source;

   private CopyNameFunction(LootItemCondition[] lootItemConditions, CopyNameFunction.NameSource source) {
      super(lootItemConditions);
      this.source = source;
   }

   public Set getReferencedContextParams() {
      return ImmutableSet.of(this.source.param);
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      Object var3 = lootContext.getParamOrNull(this.source.param);
      if(var3 instanceof Nameable) {
         Nameable var4 = (Nameable)var3;
         if(var4.hasCustomName()) {
            var1.setHoverName(var4.getDisplayName());
         }
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder copyName(CopyNameFunction.NameSource copyNameFunction$NameSource) {
      return simpleBuilder((lootItemConditions) -> {
         return new CopyNameFunction(lootItemConditions, copyNameFunction$NameSource);
      });
   }

   public static enum NameSource {
      THIS("this", LootContextParams.THIS_ENTITY),
      KILLER("killer", LootContextParams.KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER),
      BLOCK_ENTITY("block_entity", LootContextParams.BLOCK_ENTITY);

      public final String name;
      public final LootContextParam param;

      private NameSource(String name, LootContextParam param) {
         this.name = name;
         this.param = param;
      }

      public static CopyNameFunction.NameSource getByName(String name) {
         for(CopyNameFunction.NameSource var4 : values()) {
            if(var4.name.equals(name)) {
               return var4;
            }
         }

         throw new IllegalArgumentException("Invalid name source " + name);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      public Serializer() {
         super(new ResourceLocation("copy_name"), CopyNameFunction.class);
      }

      public void serialize(JsonObject jsonObject, CopyNameFunction copyNameFunction, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)copyNameFunction, jsonSerializationContext);
         jsonObject.addProperty("source", copyNameFunction.source.name);
      }

      public CopyNameFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         CopyNameFunction.NameSource var4 = CopyNameFunction.NameSource.getByName(GsonHelper.getAsString(jsonObject, "source"));
         return new CopyNameFunction(lootItemConditions, var4);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
