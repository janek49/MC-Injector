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
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;

public class CuredZombieVillagerTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("cured_zombie_villager");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      CuredZombieVillagerTrigger.PlayerListeners var3 = (CuredZombieVillagerTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new CuredZombieVillagerTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      CuredZombieVillagerTrigger.PlayerListeners var3 = (CuredZombieVillagerTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public CuredZombieVillagerTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      EntityPredicate var3 = EntityPredicate.fromJson(jsonObject.get("zombie"));
      EntityPredicate var4 = EntityPredicate.fromJson(jsonObject.get("villager"));
      return new CuredZombieVillagerTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer serverPlayer, Zombie zombie, Villager villager) {
      CuredZombieVillagerTrigger.PlayerListeners var4 = (CuredZombieVillagerTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var4 != null) {
         var4.trigger(serverPlayer, zombie, villager);
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

      public void trigger(ServerPlayer serverPlayer, Zombie zombie, Villager villager) {
         List<CriterionTrigger.Listener<CuredZombieVillagerTrigger.TriggerInstance>> var4 = null;

         for(CriterionTrigger.Listener<CuredZombieVillagerTrigger.TriggerInstance> var6 : this.listeners) {
            if(((CuredZombieVillagerTrigger.TriggerInstance)var6.getTriggerInstance()).matches(serverPlayer, zombie, villager)) {
               if(var4 == null) {
                  var4 = Lists.newArrayList();
               }

               var4.add(var6);
            }
         }

         if(var4 != null) {
            for(CriterionTrigger.Listener<CuredZombieVillagerTrigger.TriggerInstance> var6 : var4) {
               var6.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate zombie;
      private final EntityPredicate villager;

      public TriggerInstance(EntityPredicate zombie, EntityPredicate villager) {
         super(CuredZombieVillagerTrigger.ID);
         this.zombie = zombie;
         this.villager = villager;
      }

      public static CuredZombieVillagerTrigger.TriggerInstance curedZombieVillager() {
         return new CuredZombieVillagerTrigger.TriggerInstance(EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public boolean matches(ServerPlayer serverPlayer, Zombie zombie, Villager villager) {
         return !this.zombie.matches(serverPlayer, zombie)?false:this.villager.matches(serverPlayer, villager);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("zombie", this.zombie.serializeToJson());
         var1.add("villager", this.villager.serializeToJson());
         return var1;
      }
   }
}
