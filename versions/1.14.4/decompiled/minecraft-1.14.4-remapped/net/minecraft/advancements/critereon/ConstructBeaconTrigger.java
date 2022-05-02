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
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

public class ConstructBeaconTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("construct_beacon");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      ConstructBeaconTrigger.PlayerListeners var3 = (ConstructBeaconTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new ConstructBeaconTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      ConstructBeaconTrigger.PlayerListeners var3 = (ConstructBeaconTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public ConstructBeaconTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      MinMaxBounds.Ints var3 = MinMaxBounds.Ints.fromJson(jsonObject.get("level"));
      return new ConstructBeaconTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer serverPlayer, BeaconBlockEntity beaconBlockEntity) {
      ConstructBeaconTrigger.PlayerListeners var3 = (ConstructBeaconTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var3 != null) {
         var3.trigger(beaconBlockEntity);
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

      public void trigger(BeaconBlockEntity beaconBlockEntity) {
         List<CriterionTrigger.Listener<ConstructBeaconTrigger.TriggerInstance>> var2 = null;

         for(CriterionTrigger.Listener<ConstructBeaconTrigger.TriggerInstance> var4 : this.listeners) {
            if(((ConstructBeaconTrigger.TriggerInstance)var4.getTriggerInstance()).matches(beaconBlockEntity)) {
               if(var2 == null) {
                  var2 = Lists.newArrayList();
               }

               var2.add(var4);
            }
         }

         if(var2 != null) {
            for(CriterionTrigger.Listener<ConstructBeaconTrigger.TriggerInstance> var4 : var2) {
               var4.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Ints level;

      public TriggerInstance(MinMaxBounds.Ints level) {
         super(ConstructBeaconTrigger.ID);
         this.level = level;
      }

      public static ConstructBeaconTrigger.TriggerInstance constructedBeacon(MinMaxBounds.Ints minMaxBounds$Ints) {
         return new ConstructBeaconTrigger.TriggerInstance(minMaxBounds$Ints);
      }

      public boolean matches(BeaconBlockEntity beaconBlockEntity) {
         return this.level.matches(beaconBlockEntity.getLevels());
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("level", this.level.serializeToJson());
         return var1;
      }
   }
}
