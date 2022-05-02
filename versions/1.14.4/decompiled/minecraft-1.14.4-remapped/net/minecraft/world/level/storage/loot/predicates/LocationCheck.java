package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LocationCheck implements LootItemCondition {
   private final LocationPredicate predicate;

   private LocationCheck(LocationPredicate predicate) {
      this.predicate = predicate;
   }

   public boolean test(LootContext lootContext) {
      BlockPos var2 = (BlockPos)lootContext.getParamOrNull(LootContextParams.BLOCK_POS);
      return var2 != null && this.predicate.matches(lootContext.getLevel(), (float)var2.getX(), (float)var2.getY(), (float)var2.getZ());
   }

   public static LootItemCondition.Builder checkLocation(LocationPredicate.Builder locationPredicate$Builder) {
      return () -> {
         return new LocationCheck(locationPredicate$Builder.build());
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      public Serializer() {
         super(new ResourceLocation("location_check"), LocationCheck.class);
      }

      public void serialize(JsonObject jsonObject, LocationCheck locationCheck, JsonSerializationContext jsonSerializationContext) {
         jsonObject.add("predicate", locationCheck.predicate.serializeToJson());
      }

      public LocationCheck deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         LocationPredicate var3 = LocationPredicate.fromJson(jsonObject.get("predicate"));
         return new LocationCheck(var3);
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
