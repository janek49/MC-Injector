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
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class FishingRodHookedTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("fishing_rod_hooked");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      FishingRodHookedTrigger.PlayerListeners var3 = (FishingRodHookedTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new FishingRodHookedTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      FishingRodHookedTrigger.PlayerListeners var3 = (FishingRodHookedTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public FishingRodHookedTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      ItemPredicate var3 = ItemPredicate.fromJson(jsonObject.get("rod"));
      EntityPredicate var4 = EntityPredicate.fromJson(jsonObject.get("entity"));
      ItemPredicate var5 = ItemPredicate.fromJson(jsonObject.get("item"));
      return new FishingRodHookedTrigger.TriggerInstance(var3, var4, var5);
   }

   public void trigger(ServerPlayer serverPlayer, ItemStack itemStack, FishingHook fishingHook, Collection collection) {
      FishingRodHookedTrigger.PlayerListeners var5 = (FishingRodHookedTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var5 != null) {
         var5.trigger(serverPlayer, itemStack, fishingHook, collection);
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

      public void trigger(ServerPlayer serverPlayer, ItemStack itemStack, FishingHook fishingHook, Collection collection) {
         List<CriterionTrigger.Listener<FishingRodHookedTrigger.TriggerInstance>> var5 = null;

         for(CriterionTrigger.Listener<FishingRodHookedTrigger.TriggerInstance> var7 : this.listeners) {
            if(((FishingRodHookedTrigger.TriggerInstance)var7.getTriggerInstance()).matches(serverPlayer, itemStack, fishingHook, collection)) {
               if(var5 == null) {
                  var5 = Lists.newArrayList();
               }

               var5.add(var7);
            }
         }

         if(var5 != null) {
            for(CriterionTrigger.Listener<FishingRodHookedTrigger.TriggerInstance> var7 : var5) {
               var7.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate rod;
      private final EntityPredicate entity;
      private final ItemPredicate item;

      public TriggerInstance(ItemPredicate rod, EntityPredicate entity, ItemPredicate item) {
         super(FishingRodHookedTrigger.ID);
         this.rod = rod;
         this.entity = entity;
         this.item = item;
      }

      public static FishingRodHookedTrigger.TriggerInstance fishedItem(ItemPredicate var0, EntityPredicate entityPredicate, ItemPredicate var2) {
         return new FishingRodHookedTrigger.TriggerInstance(var0, entityPredicate, var2);
      }

      public boolean matches(ServerPlayer serverPlayer, ItemStack itemStack, FishingHook fishingHook, Collection collection) {
         if(!this.rod.matches(itemStack)) {
            return false;
         } else if(!this.entity.matches(serverPlayer, fishingHook.hookedIn)) {
            return false;
         } else {
            if(this.item != ItemPredicate.ANY) {
               boolean var5 = false;
               if(fishingHook.hookedIn instanceof ItemEntity) {
                  ItemEntity var6 = (ItemEntity)fishingHook.hookedIn;
                  if(this.item.matches(var6.getItem())) {
                     var5 = true;
                  }
               }

               for(ItemStack var7 : collection) {
                  if(this.item.matches(var7)) {
                     var5 = true;
                     break;
                  }
               }

               if(!var5) {
                  return false;
               }
            }

            return true;
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("rod", this.rod.serializeToJson());
         var1.add("entity", this.entity.serializeToJson());
         var1.add("item", this.item.serializeToJson());
         return var1;
      }
   }
}
