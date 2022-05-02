package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetLoreFunction extends LootItemConditionalFunction {
   private final boolean replace;
   private final List lore;
   @Nullable
   private final LootContext.EntityTarget resolutionContext;

   public SetLoreFunction(LootItemCondition[] lootItemConditions, boolean replace, List list, @Nullable LootContext.EntityTarget resolutionContext) {
      super(lootItemConditions);
      this.replace = replace;
      this.lore = ImmutableList.copyOf(list);
      this.resolutionContext = resolutionContext;
   }

   public Set getReferencedContextParams() {
      return this.resolutionContext != null?ImmutableSet.of(this.resolutionContext.getParam()):ImmutableSet.of();
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      ListTag var3 = this.getLoreTag(var1, !this.lore.isEmpty());
      if(var3 != null) {
         if(this.replace) {
            var3.clear();
         }

         UnaryOperator<Component> var4 = SetNameFunction.createResolver(lootContext, this.resolutionContext);
         this.lore.stream().map(var4).map(Component.Serializer::toJson).map(StringTag::<init>).forEach(var3::add);
      }

      return var1;
   }

   @Nullable
   private ListTag getLoreTag(ItemStack itemStack, boolean var2) {
      CompoundTag var3;
      if(itemStack.hasTag()) {
         var3 = itemStack.getTag();
      } else {
         if(!var2) {
            return null;
         }

         var3 = new CompoundTag();
         itemStack.setTag(var3);
      }

      CompoundTag var4;
      if(var3.contains("display", 10)) {
         var4 = var3.getCompound("display");
      } else {
         if(!var2) {
            return null;
         }

         var4 = new CompoundTag();
         var3.put("display", var4);
      }

      if(var4.contains("Lore", 9)) {
         return var4.getList("Lore", 8);
      } else if(var2) {
         ListTag var5 = new ListTag();
         var4.put("Lore", var5);
         return var5;
      } else {
         return null;
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      public Serializer() {
         super(new ResourceLocation("set_lore"), SetLoreFunction.class);
      }

      public void serialize(JsonObject jsonObject, SetLoreFunction setLoreFunction, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)setLoreFunction, jsonSerializationContext);
         jsonObject.addProperty("replace", Boolean.valueOf(setLoreFunction.replace));
         JsonArray var4 = new JsonArray();

         for(Component var6 : setLoreFunction.lore) {
            var4.add(Component.Serializer.toJsonTree(var6));
         }

         jsonObject.add("lore", var4);
         if(setLoreFunction.resolutionContext != null) {
            jsonObject.add("entity", jsonSerializationContext.serialize(setLoreFunction.resolutionContext));
         }

      }

      public SetLoreFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         boolean var4 = GsonHelper.getAsBoolean(jsonObject, "replace", false);
         List<Component> var5 = (List)Streams.stream(GsonHelper.getAsJsonArray(jsonObject, "lore")).map(Component.Serializer::fromJson).collect(ImmutableList.toImmutableList());
         LootContext.EntityTarget var6 = (LootContext.EntityTarget)GsonHelper.getAsObject(jsonObject, "entity", (Object)null, jsonDeserializationContext, LootContext.EntityTarget.class);
         return new SetLoreFunction(lootItemConditions, var4, var5, var6);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
