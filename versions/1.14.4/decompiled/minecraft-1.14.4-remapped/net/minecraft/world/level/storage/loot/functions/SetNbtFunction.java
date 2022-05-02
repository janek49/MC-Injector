package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetNbtFunction extends LootItemConditionalFunction {
   private final CompoundTag tag;

   private SetNbtFunction(LootItemCondition[] lootItemConditions, CompoundTag tag) {
      super(lootItemConditions);
      this.tag = tag;
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      var1.getOrCreateTag().merge(this.tag);
      return var1;
   }

   public static LootItemConditionalFunction.Builder setTag(CompoundTag tag) {
      return simpleBuilder((lootItemConditions) -> {
         return new SetNbtFunction(lootItemConditions, tag);
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      public Serializer() {
         super(new ResourceLocation("set_nbt"), SetNbtFunction.class);
      }

      public void serialize(JsonObject jsonObject, SetNbtFunction setNbtFunction, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)setNbtFunction, jsonSerializationContext);
         jsonObject.addProperty("tag", setNbtFunction.tag.toString());
      }

      public SetNbtFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         try {
            CompoundTag var4 = TagParser.parseTag(GsonHelper.getAsString(jsonObject, "tag"));
            return new SetNbtFunction(lootItemConditions, var4);
         } catch (CommandSyntaxException var5) {
            throw new JsonSyntaxException(var5.getMessage());
         }
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
