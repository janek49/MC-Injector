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
import net.minecraft.advancements.critereon.DamagePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class PlayerHurtEntityTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("player_hurt_entity");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      PlayerHurtEntityTrigger.PlayerListeners var3 = (PlayerHurtEntityTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new PlayerHurtEntityTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      PlayerHurtEntityTrigger.PlayerListeners var3 = (PlayerHurtEntityTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public PlayerHurtEntityTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      DamagePredicate var3 = DamagePredicate.fromJson(jsonObject.get("damage"));
      EntityPredicate var4 = EntityPredicate.fromJson(jsonObject.get("entity"));
      return new PlayerHurtEntityTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer serverPlayer, Entity entity, DamageSource damageSource, float var4, float var5, boolean var6) {
      PlayerHurtEntityTrigger.PlayerListeners var7 = (PlayerHurtEntityTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var7 != null) {
         var7.trigger(serverPlayer, entity, damageSource, var4, var5, var6);
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

      public void trigger(ServerPlayer serverPlayer, Entity entity, DamageSource damageSource, float var4, float var5, boolean var6) {
         List<CriterionTrigger.Listener<PlayerHurtEntityTrigger.TriggerInstance>> var7 = null;

         for(CriterionTrigger.Listener<PlayerHurtEntityTrigger.TriggerInstance> var9 : this.listeners) {
            if(((PlayerHurtEntityTrigger.TriggerInstance)var9.getTriggerInstance()).matches(serverPlayer, entity, damageSource, var4, var5, var6)) {
               if(var7 == null) {
                  var7 = Lists.newArrayList();
               }

               var7.add(var9);
            }
         }

         if(var7 != null) {
            for(CriterionTrigger.Listener<PlayerHurtEntityTrigger.TriggerInstance> var9 : var7) {
               var9.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final DamagePredicate damage;
      private final EntityPredicate entity;

      public TriggerInstance(DamagePredicate damage, EntityPredicate entity) {
         super(PlayerHurtEntityTrigger.ID);
         this.damage = damage;
         this.entity = entity;
      }

      public static PlayerHurtEntityTrigger.TriggerInstance playerHurtEntity(DamagePredicate.Builder damagePredicate$Builder) {
         return new PlayerHurtEntityTrigger.TriggerInstance(damagePredicate$Builder.build(), EntityPredicate.ANY);
      }

      public boolean matches(ServerPlayer serverPlayer, Entity entity, DamageSource damageSource, float var4, float var5, boolean var6) {
         return !this.damage.matches(serverPlayer, damageSource, var4, var5, var6)?false:this.entity.matches(serverPlayer, entity);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("damage", this.damage.serializeToJson());
         var1.add("entity", this.entity.serializeToJson());
         return var1;
      }
   }
}
