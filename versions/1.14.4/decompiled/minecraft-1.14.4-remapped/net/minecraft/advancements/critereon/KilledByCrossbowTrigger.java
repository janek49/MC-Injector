package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class KilledByCrossbowTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("killed_by_crossbow");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      KilledByCrossbowTrigger.PlayerListeners var3 = (KilledByCrossbowTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new KilledByCrossbowTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      KilledByCrossbowTrigger.PlayerListeners var3 = (KilledByCrossbowTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public KilledByCrossbowTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      EntityPredicate[] vars3 = EntityPredicate.fromJsonArray(jsonObject.get("victims"));
      MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(jsonObject.get("unique_entity_types"));
      return new KilledByCrossbowTrigger.TriggerInstance(vars3, var4);
   }

   public void trigger(ServerPlayer serverPlayer, Collection collection, int var3) {
      KilledByCrossbowTrigger.PlayerListeners var4 = (KilledByCrossbowTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var4 != null) {
         var4.trigger(serverPlayer, collection, var3);
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

      public void trigger(ServerPlayer serverPlayer, Collection collection, int var3) {
         List<CriterionTrigger.Listener<KilledByCrossbowTrigger.TriggerInstance>> var4 = null;

         for(CriterionTrigger.Listener<KilledByCrossbowTrigger.TriggerInstance> var6 : this.listeners) {
            if(((KilledByCrossbowTrigger.TriggerInstance)var6.getTriggerInstance()).matches(serverPlayer, collection, var3)) {
               if(var4 == null) {
                  var4 = Lists.newArrayList();
               }

               var4.add(var6);
            }
         }

         if(var4 != null) {
            for(CriterionTrigger.Listener<KilledByCrossbowTrigger.TriggerInstance> var6 : var4) {
               var6.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate[] victims;
      private final MinMaxBounds.Ints uniqueEntityTypes;

      public TriggerInstance(EntityPredicate[] victims, MinMaxBounds.Ints uniqueEntityTypes) {
         super(KilledByCrossbowTrigger.ID);
         this.victims = victims;
         this.uniqueEntityTypes = uniqueEntityTypes;
      }

      public static KilledByCrossbowTrigger.TriggerInstance crossbowKilled(EntityPredicate.Builder... entityPredicate$Builders) {
         EntityPredicate[] vars1 = new EntityPredicate[entityPredicate$Builders.length];

         for(int var2 = 0; var2 < entityPredicate$Builders.length; ++var2) {
            EntityPredicate.Builder var3 = entityPredicate$Builders[var2];
            vars1[var2] = var3.build();
         }

         return new KilledByCrossbowTrigger.TriggerInstance(vars1, MinMaxBounds.Ints.ANY);
      }

      public static KilledByCrossbowTrigger.TriggerInstance crossbowKilled(MinMaxBounds.Ints minMaxBounds$Ints) {
         EntityPredicate[] vars1 = new EntityPredicate[0];
         return new KilledByCrossbowTrigger.TriggerInstance(vars1, minMaxBounds$Ints);
      }

      public boolean matches(ServerPlayer serverPlayer, Collection collection, int var3) {
         if(this.victims.length > 0) {
            List<Entity> var4 = Lists.newArrayList(collection);

            for(EntityPredicate var8 : this.victims) {
               boolean var9 = false;
               Iterator<Entity> var10 = var4.iterator();

               while(var10.hasNext()) {
                  Entity var11 = (Entity)var10.next();
                  if(var8.matches(serverPlayer, var11)) {
                     var10.remove();
                     var9 = true;
                     break;
                  }
               }

               if(!var9) {
                  return false;
               }
            }
         }

         if(this.uniqueEntityTypes == MinMaxBounds.Ints.ANY) {
            return true;
         } else {
            Set<EntityType<?>> var4 = Sets.newHashSet();

            for(Entity var6 : collection) {
               var4.add(var6.getType());
            }

            return this.uniqueEntityTypes.matches(var4.size()) && this.uniqueEntityTypes.matches(var3);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("victims", EntityPredicate.serializeArrayToJson(this.victims));
         var1.add("unique_entity_types", this.uniqueEntityTypes.serializeToJson());
         return var1;
      }
   }
}
