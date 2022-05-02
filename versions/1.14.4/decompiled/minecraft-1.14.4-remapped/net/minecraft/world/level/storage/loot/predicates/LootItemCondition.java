package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.predicates.AlternativeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;

@FunctionalInterface
public interface LootItemCondition extends LootContextUser, Predicate {
   @FunctionalInterface
   public interface Builder {
      LootItemCondition build();

      default LootItemCondition.Builder invert() {
         return InvertedLootItemCondition.invert(this);
      }

      default AlternativeLootItemCondition.Builder or(LootItemCondition.Builder lootItemCondition$Builder) {
         return AlternativeLootItemCondition.alternative(new LootItemCondition.Builder[]{this, lootItemCondition$Builder});
      }
   }

   public abstract static class Serializer {
      private final ResourceLocation name;
      private final Class clazz;

      protected Serializer(ResourceLocation name, Class clazz) {
         this.name = name;
         this.clazz = clazz;
      }

      public ResourceLocation getName() {
         return this.name;
      }

      public Class getPredicateClass() {
         return this.clazz;
      }

      public abstract void serialize(JsonObject var1, LootItemCondition var2, JsonSerializationContext var3);

      public abstract LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2);
   }
}
