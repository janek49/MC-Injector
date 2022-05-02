package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

public class DamageSourceCondition implements LootItemCondition {
   private final DamageSourcePredicate predicate;

   private DamageSourceCondition(DamageSourcePredicate predicate) {
      this.predicate = predicate;
   }

   public Set getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.BLOCK_POS, LootContextParams.DAMAGE_SOURCE);
   }

   public boolean test(LootContext lootContext) {
      DamageSource var2 = (DamageSource)lootContext.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
      BlockPos var3 = (BlockPos)lootContext.getParamOrNull(LootContextParams.BLOCK_POS);
      return var3 != null && var2 != null && this.predicate.matches(lootContext.getLevel(), new Vec3(var3), var2);
   }

   public static LootItemCondition.Builder hasDamageSource(DamageSourcePredicate.Builder damageSourcePredicate$Builder) {
      return () -> {
         return new DamageSourceCondition(damageSourcePredicate$Builder.build());
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      protected Serializer() {
         super(new ResourceLocation("damage_source_properties"), DamageSourceCondition.class);
      }

      public void serialize(JsonObject jsonObject, DamageSourceCondition damageSourceCondition, JsonSerializationContext jsonSerializationContext) {
         jsonObject.add("predicate", damageSourceCondition.predicate.serializeToJson());
      }

      public DamageSourceCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         DamageSourcePredicate var3 = DamageSourcePredicate.fromJson(jsonObject.get("predicate"));
         return new DamageSourceCondition(var3);
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
