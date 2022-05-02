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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class EntityHurtPlayerTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("entity_hurt_player");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      EntityHurtPlayerTrigger.PlayerListeners var3 = (EntityHurtPlayerTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new EntityHurtPlayerTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      EntityHurtPlayerTrigger.PlayerListeners var3 = (EntityHurtPlayerTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public EntityHurtPlayerTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      DamagePredicate var3 = DamagePredicate.fromJson(jsonObject.get("damage"));
      return new EntityHurtPlayerTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer serverPlayer, DamageSource damageSource, float var3, float var4, boolean var5) {
      EntityHurtPlayerTrigger.PlayerListeners var6 = (EntityHurtPlayerTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var6 != null) {
         var6.trigger(serverPlayer, damageSource, var3, var4, var5);
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

      public void trigger(ServerPlayer serverPlayer, DamageSource damageSource, float var3, float var4, boolean var5) {
         List<CriterionTrigger.Listener<EntityHurtPlayerTrigger.TriggerInstance>> var6 = null;

         for(CriterionTrigger.Listener<EntityHurtPlayerTrigger.TriggerInstance> var8 : this.listeners) {
            if(((EntityHurtPlayerTrigger.TriggerInstance)var8.getTriggerInstance()).matches(serverPlayer, damageSource, var3, var4, var5)) {
               if(var6 == null) {
                  var6 = Lists.newArrayList();
               }

               var6.add(var8);
            }
         }

         if(var6 != null) {
            for(CriterionTrigger.Listener<EntityHurtPlayerTrigger.TriggerInstance> var8 : var6) {
               var8.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final DamagePredicate damage;

      public TriggerInstance(DamagePredicate damage) {
         super(EntityHurtPlayerTrigger.ID);
         this.damage = damage;
      }

      public static EntityHurtPlayerTrigger.TriggerInstance entityHurtPlayer(DamagePredicate.Builder damagePredicate$Builder) {
         return new EntityHurtPlayerTrigger.TriggerInstance(damagePredicate$Builder.build());
      }

      public boolean matches(ServerPlayer serverPlayer, DamageSource damageSource, float var3, float var4, boolean var5) {
         return this.damage.matches(serverPlayer, damageSource, var3, var4, var5);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("damage", this.damage.serializeToJson());
         return var1;
      }
   }
}
