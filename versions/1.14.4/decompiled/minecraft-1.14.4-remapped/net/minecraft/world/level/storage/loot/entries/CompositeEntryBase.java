package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.entries.ComposableEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class CompositeEntryBase extends LootPoolEntryContainer {
   protected final LootPoolEntryContainer[] children;
   private final ComposableEntryContainer composedChildren;

   protected CompositeEntryBase(LootPoolEntryContainer[] children, LootItemCondition[] lootItemConditions) {
      super(lootItemConditions);
      this.children = children;
      this.composedChildren = this.compose(children);
   }

   public void validate(LootTableProblemCollector lootTableProblemCollector, Function function, Set set, LootContextParamSet lootContextParamSet) {
      super.validate(lootTableProblemCollector, function, set, lootContextParamSet);
      if(this.children.length == 0) {
         lootTableProblemCollector.reportProblem("Empty children list");
      }

      for(int var5 = 0; var5 < this.children.length; ++var5) {
         this.children[var5].validate(lootTableProblemCollector.forChild(".entry[" + var5 + "]"), function, set, lootContextParamSet);
      }

   }

   protected abstract ComposableEntryContainer compose(ComposableEntryContainer[] var1);

   public final boolean expand(LootContext lootContext, Consumer consumer) {
      return !this.canRun(lootContext)?false:this.composedChildren.expand(lootContext, consumer);
   }

   public static CompositeEntryBase.Serializer createSerializer(final ResourceLocation resourceLocation, final Class class, final CompositeEntryBase.CompositeEntryConstructor compositeEntryBase$CompositeEntryConstructor) {
      return new CompositeEntryBase.Serializer(resourceLocation, class) {
         protected CompositeEntryBase deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootPoolEntryContainer[] lootPoolEntryContainers, LootItemCondition[] lootItemConditions) {
            return compositeEntryBase$CompositeEntryConstructor.create(lootPoolEntryContainers, lootItemConditions);
         }
      };
   }

   @FunctionalInterface
   public interface CompositeEntryConstructor {
      CompositeEntryBase create(LootPoolEntryContainer[] var1, LootItemCondition[] var2);
   }

   public abstract static class Serializer extends LootPoolEntryContainer.Serializer {
      public Serializer(ResourceLocation resourceLocation, Class class) {
         super(resourceLocation, class);
      }

      public void serialize(JsonObject jsonObject, CompositeEntryBase compositeEntryBase, JsonSerializationContext jsonSerializationContext) {
         jsonObject.add("children", jsonSerializationContext.serialize(compositeEntryBase.children));
      }

      public final CompositeEntryBase deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         LootPoolEntryContainer[] vars4 = (LootPoolEntryContainer[])GsonHelper.getAsObject(jsonObject, "children", jsonDeserializationContext, LootPoolEntryContainer[].class);
         return this.deserialize(jsonObject, jsonDeserializationContext, vars4, lootItemConditions);
      }

      protected abstract CompositeEntryBase deserialize(JsonObject var1, JsonDeserializationContext var2, LootPoolEntryContainer[] var3, LootItemCondition[] var4);

      // $FF: synthetic method
      public LootPoolEntryContainer deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
