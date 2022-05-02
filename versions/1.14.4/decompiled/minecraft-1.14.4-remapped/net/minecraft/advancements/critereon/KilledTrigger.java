package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class KilledTrigger implements CriterionTrigger {
   private final Map players = Maps.newHashMap();
   private final ResourceLocation id;

   public KilledTrigger(ResourceLocation id) {
      this.id = id;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      KilledTrigger.PlayerListeners var3 = (KilledTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new KilledTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      KilledTrigger.PlayerListeners var3 = (KilledTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 != null) {
         var3.removeListener(criterionTrigger$Listener);
         if(var3.isEmpty()) {
            this.players.remove(playerAdvancements);
         }
      }

   }

   public void removePlayerListeners(PlayerAdvancements playerAdvancements) {
      this.players.remove(playerAdvancements);
   }

   public KilledTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      return new KilledTrigger.TriggerInstance(this.id, EntityPredicate.fromJson(jsonObject.get("entity")), DamageSourcePredicate.fromJson(jsonObject.get("killing_blow")));
   }

   public void trigger(ServerPlayer serverPlayer, Entity entity, DamageSource damageSource) {
      KilledTrigger.PlayerListeners var4 = (KilledTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var4 != null) {
         var4.trigger(serverPlayer, entity, damageSource);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements player) {
         this.player = player;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener criterionTrigger$Listener) {
         this.listeners.add(criterionTrigger$Listener);
      }

      public void removeListener(CriterionTrigger.Listener criterionTrigger$Listener) {
         this.listeners.remove(criterionTrigger$Listener);
      }

      public void trigger(ServerPlayer serverPlayer, Entity entity, DamageSource damageSource) {
         List<CriterionTrigger.Listener<KilledTrigger.TriggerInstance>> var4 = null;

         for(CriterionTrigger.Listener<KilledTrigger.TriggerInstance> var6 : this.listeners) {
            if(((KilledTrigger.TriggerInstance)var6.getTriggerInstance()).matches(serverPlayer, entity, damageSource)) {
               if(var4 == null) {
                  var4 = Lists.newArrayList();
               }

               var4.add(var6);
            }
         }

         if(var4 != null) {
            for(CriterionTrigger.Listener<KilledTrigger.TriggerInstance> var6 : var4) {
               var6.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate entityPredicate;
      private final DamageSourcePredicate killingBlow;

      public TriggerInstance(ResourceLocation resourceLocation, EntityPredicate entityPredicate, DamageSourcePredicate killingBlow) {
         super(resourceLocation);
         this.entityPredicate = entityPredicate;
         this.killingBlow = killingBlow;
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate.Builder entityPredicate$Builder) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, entityPredicate$Builder.build(), DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity() {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.ANY, DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate.Builder entityPredicate$Builder, DamageSourcePredicate.Builder damageSourcePredicate$Builder) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, entityPredicate$Builder.build(), damageSourcePredicate$Builder.build());
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer() {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.ANY, DamageSourcePredicate.ANY);
      }

      public boolean matches(ServerPlayer serverPlayer, Entity entity, DamageSource damageSource) {
         return !this.killingBlow.matches(serverPlayer, damageSource)?false:this.entityPredicate.matches(serverPlayer, entity);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("entity", this.entityPredicate.serializeToJson());
         var1.add("killing_blow", this.killingBlow.serializeToJson());
         return var1;
      }
   }
}
