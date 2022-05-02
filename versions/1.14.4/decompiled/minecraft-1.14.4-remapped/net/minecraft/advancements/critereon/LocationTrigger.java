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
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class LocationTrigger implements CriterionTrigger {
   private final ResourceLocation id;
   private final Map players = Maps.newHashMap();

   public LocationTrigger(ResourceLocation id) {
      this.id = id;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      LocationTrigger.PlayerListeners var3 = (LocationTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new LocationTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      LocationTrigger.PlayerListeners var3 = (LocationTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public LocationTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      LocationPredicate var3 = LocationPredicate.fromJson(jsonObject);
      return new LocationTrigger.TriggerInstance(this.id, var3);
   }

   public void trigger(ServerPlayer serverPlayer) {
      LocationTrigger.PlayerListeners var2 = (LocationTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var2 != null) {
         var2.trigger(serverPlayer.getLevel(), serverPlayer.x, serverPlayer.y, serverPlayer.z);
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

      public void trigger(ServerLevel serverLevel, double var2, double var4, double var6) {
         List<CriterionTrigger.Listener<LocationTrigger.TriggerInstance>> var8 = null;

         for(CriterionTrigger.Listener<LocationTrigger.TriggerInstance> var10 : this.listeners) {
            if(((LocationTrigger.TriggerInstance)var10.getTriggerInstance()).matches(serverLevel, var2, var4, var6)) {
               if(var8 == null) {
                  var8 = Lists.newArrayList();
               }

               var8.add(var10);
            }
         }

         if(var8 != null) {
            for(CriterionTrigger.Listener<LocationTrigger.TriggerInstance> var10 : var8) {
               var10.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final LocationPredicate location;

      public TriggerInstance(ResourceLocation resourceLocation, LocationPredicate location) {
         super(resourceLocation);
         this.location = location;
      }

      public static LocationTrigger.TriggerInstance located(LocationPredicate locationPredicate) {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.LOCATION.id, locationPredicate);
      }

      public static LocationTrigger.TriggerInstance sleptInBed() {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.SLEPT_IN_BED.id, LocationPredicate.ANY);
      }

      public static LocationTrigger.TriggerInstance raidWon() {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.RAID_WIN.id, LocationPredicate.ANY);
      }

      public boolean matches(ServerLevel serverLevel, double var2, double var4, double var6) {
         return this.location.matches(serverLevel, var2, var4, var6);
      }

      public JsonElement serializeToJson() {
         return this.location.serializeToJson();
      }
   }
}
