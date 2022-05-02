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
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class EffectsChangedTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("effects_changed");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      EffectsChangedTrigger.PlayerListeners var3 = (EffectsChangedTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new EffectsChangedTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      EffectsChangedTrigger.PlayerListeners var3 = (EffectsChangedTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public EffectsChangedTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      MobEffectsPredicate var3 = MobEffectsPredicate.fromJson(jsonObject.get("effects"));
      return new EffectsChangedTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer serverPlayer) {
      EffectsChangedTrigger.PlayerListeners var2 = (EffectsChangedTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var2 != null) {
         var2.trigger(serverPlayer);
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

      public void trigger(ServerPlayer serverPlayer) {
         List<CriterionTrigger.Listener<EffectsChangedTrigger.TriggerInstance>> var2 = null;

         for(CriterionTrigger.Listener<EffectsChangedTrigger.TriggerInstance> var4 : this.listeners) {
            if(((EffectsChangedTrigger.TriggerInstance)var4.getTriggerInstance()).matches(serverPlayer)) {
               if(var2 == null) {
                  var2 = Lists.newArrayList();
               }

               var2.add(var4);
            }
         }

         if(var2 != null) {
            for(CriterionTrigger.Listener<EffectsChangedTrigger.TriggerInstance> var4 : var2) {
               var4.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MobEffectsPredicate effects;

      public TriggerInstance(MobEffectsPredicate effects) {
         super(EffectsChangedTrigger.ID);
         this.effects = effects;
      }

      public static EffectsChangedTrigger.TriggerInstance hasEffects(MobEffectsPredicate mobEffectsPredicate) {
         return new EffectsChangedTrigger.TriggerInstance(mobEffectsPredicate);
      }

      public boolean matches(ServerPlayer serverPlayer) {
         return this.effects.matches((LivingEntity)serverPlayer);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("effects", this.effects.serializeToJson());
         return var1;
      }
   }
}
