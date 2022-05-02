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
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class NetherTravelTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("nether_travel");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      NetherTravelTrigger.PlayerListeners var3 = (NetherTravelTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new NetherTravelTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      NetherTravelTrigger.PlayerListeners var3 = (NetherTravelTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public NetherTravelTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      LocationPredicate var3 = LocationPredicate.fromJson(jsonObject.get("entered"));
      LocationPredicate var4 = LocationPredicate.fromJson(jsonObject.get("exited"));
      DistancePredicate var5 = DistancePredicate.fromJson(jsonObject.get("distance"));
      return new NetherTravelTrigger.TriggerInstance(var3, var4, var5);
   }

   public void trigger(ServerPlayer serverPlayer, Vec3 vec3) {
      NetherTravelTrigger.PlayerListeners var3 = (NetherTravelTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var3 != null) {
         var3.trigger(serverPlayer.getLevel(), vec3, serverPlayer.x, serverPlayer.y, serverPlayer.z);
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

      public void trigger(ServerLevel serverLevel, Vec3 vec3, double var3, double var5, double var7) {
         List<CriterionTrigger.Listener<NetherTravelTrigger.TriggerInstance>> var9 = null;

         for(CriterionTrigger.Listener<NetherTravelTrigger.TriggerInstance> var11 : this.listeners) {
            if(((NetherTravelTrigger.TriggerInstance)var11.getTriggerInstance()).matches(serverLevel, vec3, var3, var5, var7)) {
               if(var9 == null) {
                  var9 = Lists.newArrayList();
               }

               var9.add(var11);
            }
         }

         if(var9 != null) {
            for(CriterionTrigger.Listener<NetherTravelTrigger.TriggerInstance> var11 : var9) {
               var11.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final LocationPredicate entered;
      private final LocationPredicate exited;
      private final DistancePredicate distance;

      public TriggerInstance(LocationPredicate entered, LocationPredicate exited, DistancePredicate distance) {
         super(NetherTravelTrigger.ID);
         this.entered = entered;
         this.exited = exited;
         this.distance = distance;
      }

      public static NetherTravelTrigger.TriggerInstance travelledThroughNether(DistancePredicate distancePredicate) {
         return new NetherTravelTrigger.TriggerInstance(LocationPredicate.ANY, LocationPredicate.ANY, distancePredicate);
      }

      public boolean matches(ServerLevel serverLevel, Vec3 vec3, double var3, double var5, double var7) {
         return !this.entered.matches(serverLevel, vec3.x, vec3.y, vec3.z)?false:(!this.exited.matches(serverLevel, var3, var5, var7)?false:this.distance.matches(vec3.x, vec3.y, vec3.z, var3, var5, var7));
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("entered", this.entered.serializeToJson());
         var1.add("exited", this.exited.serializeToJson());
         var1.add("distance", this.distance.serializeToJson());
         return var1;
      }
   }
}
