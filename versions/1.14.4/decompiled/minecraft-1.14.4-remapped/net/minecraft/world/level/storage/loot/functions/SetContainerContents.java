package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerContents extends LootItemConditionalFunction {
   private final List entries;

   private SetContainerContents(LootItemCondition[] lootItemConditions, List list) {
      super(lootItemConditions);
      this.entries = ImmutableList.copyOf(list);
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      if(var1.isEmpty()) {
         return var1;
      } else {
         NonNullList<ItemStack> var3 = NonNullList.create();
         this.entries.forEach((lootPoolEntryContainer) -> {
            lootPoolEntryContainer.expand(lootContext, (lootPoolEntry) -> {
               var3.getClass();
               lootPoolEntry.createItemStack(LootTable.createStackSplitter(var3::add), lootContext);
            });
         });
         CompoundTag var4 = new CompoundTag();
         ContainerHelper.saveAllItems(var4, var3);
         CompoundTag var5 = var1.getOrCreateTag();
         var5.put("BlockEntityTag", var4.merge(var5.getCompound("BlockEntityTag")));
         return var1;
      }
   }

   public void validate(LootTableProblemCollector lootTableProblemCollector, Function function, Set set, LootContextParamSet lootContextParamSet) {
      super.validate(lootTableProblemCollector, function, set, lootContextParamSet);

      for(int var5 = 0; var5 < this.entries.size(); ++var5) {
         ((LootPoolEntryContainer)this.entries.get(var5)).validate(lootTableProblemCollector.forChild(".entry[" + var5 + "]"), function, set, lootContextParamSet);
      }

   }

   public static SetContainerContents.Builder setContents() {
      return new SetContainerContents.Builder();
   }

   public static class Builder extends LootItemConditionalFunction.Builder {
      private final List entries = Lists.newArrayList();

      protected SetContainerContents.Builder getThis() {
         return this;
      }

      public SetContainerContents.Builder withEntry(LootPoolEntryContainer.Builder lootPoolEntryContainer$Builder) {
         this.entries.add(lootPoolEntryContainer$Builder.build());
         return this;
      }

      public LootItemFunction build() {
         return new SetContainerContents(this.getConditions(), this.entries);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      protected Serializer() {
         super(new ResourceLocation("set_contents"), SetContainerContents.class);
      }

      public void serialize(JsonObject jsonObject, SetContainerContents setContainerContents, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)setContainerContents, jsonSerializationContext);
         jsonObject.add("entries", jsonSerializationContext.serialize(setContainerContents.entries));
      }

      public SetContainerContents deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         LootPoolEntryContainer[] vars4 = (LootPoolEntryContainer[])GsonHelper.getAsObject(jsonObject, "entries", jsonDeserializationContext, LootPoolEntryContainer[].class);
         return new SetContainerContents(lootItemConditions, Arrays.asList(vars4));
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
