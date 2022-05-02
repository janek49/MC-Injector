package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ExplosionCondition implements LootItemCondition {
   private static final ExplosionCondition INSTANCE = new ExplosionCondition();

   public Set getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.EXPLOSION_RADIUS);
   }

   public boolean test(LootContext lootContext) {
      Float var2 = (Float)lootContext.getParamOrNull(LootContextParams.EXPLOSION_RADIUS);
      if(var2 != null) {
         Random var3 = lootContext.getRandom();
         float var4 = 1.0F / var2.floatValue();
         return var3.nextFloat() <= var4;
      } else {
         return true;
      }
   }

   public static LootItemCondition.Builder survivesExplosion() {
      return () -> {
         return INSTANCE;
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      protected Serializer() {
         super(new ResourceLocation("survives_explosion"), ExplosionCondition.class);
      }

      public void serialize(JsonObject jsonObject, ExplosionCondition explosionCondition, JsonSerializationContext jsonSerializationContext) {
      }

      public ExplosionCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         return ExplosionCondition.INSTANCE;
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
