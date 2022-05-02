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
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class SummonedEntityTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("summoned_entity");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      SummonedEntityTrigger.PlayerListeners var3 = (SummonedEntityTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new SummonedEntityTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      SummonedEntityTrigger.PlayerListeners var3 = (SummonedEntityTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public SummonedEntityTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      EntityPredicate var3 = EntityPredicate.fromJson(jsonObject.get("entity"));
      return new SummonedEntityTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer serverPlayer, Entity entity) {
      SummonedEntityTrigger.PlayerListeners var3 = (SummonedEntityTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var3 != null) {
         var3.trigger(serverPlayer, entity);
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

      public void trigger(ServerPlayer serverPlayer, Entity entity) {
         List<CriterionTrigger.Listener<SummonedEntityTrigger.TriggerInstance>> var3 = null;

         for(CriterionTrigger.Listener<SummonedEntityTrigger.TriggerInstance> var5 : this.listeners) {
            if(((SummonedEntityTrigger.TriggerInstance)var5.getTriggerInstance()).matches(serverPlayer, entity)) {
               if(var3 == null) {
                  var3 = Lists.newArrayList();
               }

               var3.add(var5);
            }
         }

         if(var3 != null) {
            for(CriterionTrigger.Listener<SummonedEntityTrigger.TriggerInstance> var5 : var3) {
               var5.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate entity;

      public TriggerInstance(EntityPredicate entity) {
         super(SummonedEntityTrigger.ID);
         this.entity = entity;
      }

      public static SummonedEntityTrigger.TriggerInstance summonedEntity(EntityPredicate.Builder entityPredicate$Builder) {
         return new SummonedEntityTrigger.TriggerInstance(entityPredicate$Builder.build());
      }

      public boolean matches(ServerPlayer serverPlayer, Entity entity) {
         return this.entity.matches(serverPlayer, entity);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("entity", this.entity.serializeToJson());
         return var1;
      }
   }
}
