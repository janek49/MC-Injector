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
import net.minecraft.world.entity.animal.Animal;

public class TameAnimalTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("tame_animal");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      TameAnimalTrigger.PlayerListeners var3 = (TameAnimalTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new TameAnimalTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      TameAnimalTrigger.PlayerListeners var3 = (TameAnimalTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public TameAnimalTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      EntityPredicate var3 = EntityPredicate.fromJson(jsonObject.get("entity"));
      return new TameAnimalTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer serverPlayer, Animal animal) {
      TameAnimalTrigger.PlayerListeners var3 = (TameAnimalTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var3 != null) {
         var3.trigger(serverPlayer, animal);
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

      public void trigger(ServerPlayer serverPlayer, Animal animal) {
         List<CriterionTrigger.Listener<TameAnimalTrigger.TriggerInstance>> var3 = null;

         for(CriterionTrigger.Listener<TameAnimalTrigger.TriggerInstance> var5 : this.listeners) {
            if(((TameAnimalTrigger.TriggerInstance)var5.getTriggerInstance()).matches(serverPlayer, animal)) {
               if(var3 == null) {
                  var3 = Lists.newArrayList();
               }

               var3.add(var5);
            }
         }

         if(var3 != null) {
            for(CriterionTrigger.Listener<TameAnimalTrigger.TriggerInstance> var5 : var3) {
               var5.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate entity;

      public TriggerInstance(EntityPredicate entity) {
         super(TameAnimalTrigger.ID);
         this.entity = entity;
      }

      public static TameAnimalTrigger.TriggerInstance tamedAnimal() {
         return new TameAnimalTrigger.TriggerInstance(EntityPredicate.ANY);
      }

      public static TameAnimalTrigger.TriggerInstance tamedAnimal(EntityPredicate entityPredicate) {
         return new TameAnimalTrigger.TriggerInstance(entityPredicate);
      }

      public boolean matches(ServerPlayer serverPlayer, Animal animal) {
         return this.entity.matches(serverPlayer, animal);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("entity", this.entity.serializeToJson());
         return var1;
      }
   }
}
