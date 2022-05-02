package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

public class UsedEnderEyeTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("used_ender_eye");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      UsedEnderEyeTrigger.PlayerListeners var3 = (UsedEnderEyeTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new UsedEnderEyeTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      UsedEnderEyeTrigger.PlayerListeners var3 = (UsedEnderEyeTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public UsedEnderEyeTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      MinMaxBounds.Floats var3 = MinMaxBounds.Floats.fromJson(jsonObject.get("distance"));
      return new UsedEnderEyeTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer serverPlayer, BlockPos blockPos) {
      UsedEnderEyeTrigger.PlayerListeners var3 = (UsedEnderEyeTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var3 != null) {
         double var4 = serverPlayer.x - (double)blockPos.getX();
         double var6 = serverPlayer.z - (double)blockPos.getZ();
         var3.trigger(var4 * var4 + var6 * var6);
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

      public void trigger(double d) {
         List<CriterionTrigger.Listener<UsedEnderEyeTrigger.TriggerInstance>> var3 = null;

         for(CriterionTrigger.Listener<UsedEnderEyeTrigger.TriggerInstance> var5 : this.listeners) {
            if(((UsedEnderEyeTrigger.TriggerInstance)var5.getTriggerInstance()).matches(d)) {
               if(var3 == null) {
                  var3 = Lists.newArrayList();
               }

               var3.add(var5);
            }
         }

         if(var3 != null) {
            for(CriterionTrigger.Listener<UsedEnderEyeTrigger.TriggerInstance> var5 : var3) {
               var5.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Floats level;

      public TriggerInstance(MinMaxBounds.Floats level) {
         super(UsedEnderEyeTrigger.ID);
         this.level = level;
      }

      public boolean matches(double d) {
         return this.level.matchesSqr(d);
      }
   }
}
