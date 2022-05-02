package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public class EntityHasScoreCondition implements LootItemCondition {
   private final Map scores;
   private final LootContext.EntityTarget entityTarget;

   private EntityHasScoreCondition(Map map, LootContext.EntityTarget entityTarget) {
      this.scores = ImmutableMap.copyOf(map);
      this.entityTarget = entityTarget;
   }

   public Set getReferencedContextParams() {
      return ImmutableSet.of(this.entityTarget.getParam());
   }

   public boolean test(LootContext lootContext) {
      Entity var2 = (Entity)lootContext.getParamOrNull(this.entityTarget.getParam());
      if(var2 == null) {
         return false;
      } else {
         Scoreboard var3 = var2.level.getScoreboard();

         for(Entry<String, RandomValueBounds> var5 : this.scores.entrySet()) {
            if(!this.hasScore(var2, var3, (String)var5.getKey(), (RandomValueBounds)var5.getValue())) {
               return false;
            }
         }

         return true;
      }
   }

   protected boolean hasScore(Entity entity, Scoreboard scoreboard, String string, RandomValueBounds randomValueBounds) {
      Objective var5 = scoreboard.getObjective(string);
      if(var5 == null) {
         return false;
      } else {
         String var6 = entity.getScoreboardName();
         return !scoreboard.hasPlayerScore(var6, var5)?false:randomValueBounds.matchesValue(scoreboard.getOrCreatePlayerScore(var6, var5).getScore());
      }
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      protected Serializer() {
         super(new ResourceLocation("entity_scores"), EntityHasScoreCondition.class);
      }

      public void serialize(JsonObject jsonObject, EntityHasScoreCondition entityHasScoreCondition, JsonSerializationContext jsonSerializationContext) {
         JsonObject jsonObject = new JsonObject();

         for(Entry<String, RandomValueBounds> var6 : entityHasScoreCondition.scores.entrySet()) {
            jsonObject.add((String)var6.getKey(), jsonSerializationContext.serialize(var6.getValue()));
         }

         jsonObject.add("scores", jsonObject);
         jsonObject.add("entity", jsonSerializationContext.serialize(entityHasScoreCondition.entityTarget));
      }

      public EntityHasScoreCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         Set<Entry<String, JsonElement>> var3 = GsonHelper.getAsJsonObject(jsonObject, "scores").entrySet();
         Map<String, RandomValueBounds> var4 = Maps.newLinkedHashMap();

         for(Entry<String, JsonElement> var6 : var3) {
            var4.put(var6.getKey(), GsonHelper.convertToObject((JsonElement)var6.getValue(), "score", jsonDeserializationContext, RandomValueBounds.class));
         }

         return new EntityHasScoreCondition(var4, (LootContext.EntityTarget)GsonHelper.getAsObject(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class));
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
