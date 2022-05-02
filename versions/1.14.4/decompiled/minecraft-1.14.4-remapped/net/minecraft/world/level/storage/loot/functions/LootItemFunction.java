package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface LootItemFunction extends LootContextUser, BiFunction {
   static default Consumer decorate(BiFunction biFunction, Consumer var1, LootContext lootContext) {
      return (itemStack) -> {
         var1.accept(biFunction.apply(itemStack, lootContext));
      };
   }

   public interface Builder {
      LootItemFunction build();
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

      public Class getFunctionClass() {
         return this.clazz;
      }

      public abstract void serialize(JsonObject var1, LootItemFunction var2, JsonSerializationContext var3);

      public abstract LootItemFunction deserialize(JsonObject var1, JsonDeserializationContext var2);
   }
}
