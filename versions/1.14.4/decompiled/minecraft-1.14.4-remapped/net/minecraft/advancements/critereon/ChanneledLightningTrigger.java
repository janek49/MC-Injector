package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
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

public class ChanneledLightningTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("channeled_lightning");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      ChanneledLightningTrigger.PlayerListeners var3 = (ChanneledLightningTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new ChanneledLightningTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      ChanneledLightningTrigger.PlayerListeners var3 = (ChanneledLightningTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public ChanneledLightningTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      EntityPredicate[] vars3 = EntityPredicate.fromJsonArray(jsonObject.get("victims"));
      return new ChanneledLightningTrigger.TriggerInstance(vars3);
   }

   public void trigger(ServerPlayer serverPlayer, Collection collection) {
      ChanneledLightningTrigger.PlayerListeners var3 = (ChanneledLightningTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var3 != null) {
         var3.trigger(serverPlayer, collection);
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

      public void trigger(ServerPlayer serverPlayer, Collection collection) {
         List<CriterionTrigger.Listener<ChanneledLightningTrigger.TriggerInstance>> var3 = null;

         for(CriterionTrigger.Listener<ChanneledLightningTrigger.TriggerInstance> var5 : this.listeners) {
            if(((ChanneledLightningTrigger.TriggerInstance)var5.getTriggerInstance()).matches(serverPlayer, collection)) {
               if(var3 == null) {
                  var3 = Lists.newArrayList();
               }

               var3.add(var5);
            }
         }

         if(var3 != null) {
            for(CriterionTrigger.Listener<ChanneledLightningTrigger.TriggerInstance> var5 : var3) {
               var5.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate[] victims;

      public TriggerInstance(EntityPredicate[] victims) {
         super(ChanneledLightningTrigger.ID);
         this.victims = victims;
      }

      public static ChanneledLightningTrigger.TriggerInstance channeledLightning(EntityPredicate... entityPredicates) {
         return new ChanneledLightningTrigger.TriggerInstance(entityPredicates);
      }

      public boolean matches(ServerPlayer serverPlayer, Collection collection) {
         for(EntityPredicate var6 : this.victims) {
            boolean var7 = false;

            for(Entity var9 : collection) {
               if(var6.matches(serverPlayer, var9)) {
                  var7 = true;
                  break;
               }
            }

            if(!var7) {
               return false;
            }
         }

         return true;
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("victims", EntityPredicate.serializeArrayToJson(this.victims));
         return var1;
      }
   }
}
