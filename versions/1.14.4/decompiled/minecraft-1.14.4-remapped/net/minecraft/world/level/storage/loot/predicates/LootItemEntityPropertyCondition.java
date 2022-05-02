package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

public class LootItemEntityPropertyCondition implements LootItemCondition {
   private final EntityPredicate predicate;
   private final LootContext.EntityTarget entityTarget;

   private LootItemEntityPropertyCondition(EntityPredicate predicate, LootContext.EntityTarget entityTarget) {
      this.predicate = predicate;
      this.entityTarget = entityTarget;
   }

   public Set getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.BLOCK_POS, this.entityTarget.getParam());
   }

   public boolean test(LootContext lootContext) {
      Entity var2 = (Entity)lootContext.getParamOrNull(this.entityTarget.getParam());
      BlockPos var3 = (BlockPos)lootContext.getParamOrNull(LootContextParams.BLOCK_POS);
      return var3 != null && this.predicate.matches(lootContext.getLevel(), new Vec3(var3), var2);
   }

   public static LootItemCondition.Builder entityPresent(LootContext.EntityTarget lootContext$EntityTarget) {
      return hasProperties(lootContext$EntityTarget, EntityPredicate.Builder.entity());
   }

   public static LootItemCondition.Builder hasProperties(LootContext.EntityTarget lootContext$EntityTarget, EntityPredicate.Builder entityPredicate$Builder) {
      return () -> {
         return new LootItemEntityPropertyCondition(entityPredicate$Builder.build(), lootContext$EntityTarget);
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      protected Serializer() {
         super(new ResourceLocation("entity_properties"), LootItemEntityPropertyCondition.class);
      }

      public void serialize(JsonObject jsonObject, LootItemEntityPropertyCondition lootItemEntityPropertyCondition, JsonSerializationContext jsonSerializationContext) {
         jsonObject.add("predicate", lootItemEntityPropertyCondition.predicate.serializeToJson());
         jsonObject.add("entity", jsonSerializationContext.serialize(lootItemEntityPropertyCondition.entityTarget));
      }

      public LootItemEntityPropertyCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         EntityPredicate var3 = EntityPredicate.fromJson(jsonObject.get("predicate"));
         return new LootItemEntityPropertyCondition(var3, (LootContext.EntityTarget)GsonHelper.getAsObject(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class));
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
