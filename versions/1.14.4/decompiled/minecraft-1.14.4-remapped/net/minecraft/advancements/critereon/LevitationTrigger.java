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
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class LevitationTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("levitation");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      LevitationTrigger.PlayerListeners var3 = (LevitationTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new LevitationTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      LevitationTrigger.PlayerListeners var3 = (LevitationTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public LevitationTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      DistancePredicate var3 = DistancePredicate.fromJson(jsonObject.get("distance"));
      MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(jsonObject.get("duration"));
      return new LevitationTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer serverPlayer, Vec3 vec3, int var3) {
      LevitationTrigger.PlayerListeners var4 = (LevitationTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var4 != null) {
         var4.trigger(serverPlayer, vec3, var3);
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

      public void trigger(ServerPlayer serverPlayer, Vec3 vec3, int var3) {
         List<CriterionTrigger.Listener<LevitationTrigger.TriggerInstance>> var4 = null;

         for(CriterionTrigger.Listener<LevitationTrigger.TriggerInstance> var6 : this.listeners) {
            if(((LevitationTrigger.TriggerInstance)var6.getTriggerInstance()).matches(serverPlayer, vec3, var3)) {
               if(var4 == null) {
                  var4 = Lists.newArrayList();
               }

               var4.add(var6);
            }
         }

         if(var4 != null) {
            for(CriterionTrigger.Listener<LevitationTrigger.TriggerInstance> var6 : var4) {
               var6.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final DistancePredicate distance;
      private final MinMaxBounds.Ints duration;

      public TriggerInstance(DistancePredicate distance, MinMaxBounds.Ints duration) {
         super(LevitationTrigger.ID);
         this.distance = distance;
         this.duration = duration;
      }

      public static LevitationTrigger.TriggerInstance levitated(DistancePredicate distancePredicate) {
         return new LevitationTrigger.TriggerInstance(distancePredicate, MinMaxBounds.Ints.ANY);
      }

      public boolean matches(ServerPlayer serverPlayer, Vec3 vec3, int var3) {
         return !this.distance.matches(vec3.x, vec3.y, vec3.z, serverPlayer.x, serverPlayer.y, serverPlayer.z)?false:this.duration.matches(var3);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("distance", this.distance.serializeToJson());
         var1.add("duration", this.duration.serializeToJson());
         return var1;
      }
   }
}
