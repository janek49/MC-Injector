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
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;

public class TradeTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("villager_trade");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      TradeTrigger.PlayerListeners var3 = (TradeTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new TradeTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      TradeTrigger.PlayerListeners var3 = (TradeTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public TradeTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      EntityPredicate var3 = EntityPredicate.fromJson(jsonObject.get("villager"));
      ItemPredicate var4 = ItemPredicate.fromJson(jsonObject.get("item"));
      return new TradeTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer serverPlayer, AbstractVillager abstractVillager, ItemStack itemStack) {
      TradeTrigger.PlayerListeners var4 = (TradeTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var4 != null) {
         var4.trigger(serverPlayer, abstractVillager, itemStack);
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

      public void trigger(ServerPlayer serverPlayer, AbstractVillager abstractVillager, ItemStack itemStack) {
         List<CriterionTrigger.Listener<TradeTrigger.TriggerInstance>> var4 = null;

         for(CriterionTrigger.Listener<TradeTrigger.TriggerInstance> var6 : this.listeners) {
            if(((TradeTrigger.TriggerInstance)var6.getTriggerInstance()).matches(serverPlayer, abstractVillager, itemStack)) {
               if(var4 == null) {
                  var4 = Lists.newArrayList();
               }

               var4.add(var6);
            }
         }

         if(var4 != null) {
            for(CriterionTrigger.Listener<TradeTrigger.TriggerInstance> var6 : var4) {
               var6.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate villager;
      private final ItemPredicate item;

      public TriggerInstance(EntityPredicate villager, ItemPredicate item) {
         super(TradeTrigger.ID);
         this.villager = villager;
         this.item = item;
      }

      public static TradeTrigger.TriggerInstance tradedWithVillager() {
         return new TradeTrigger.TriggerInstance(EntityPredicate.ANY, ItemPredicate.ANY);
      }

      public boolean matches(ServerPlayer serverPlayer, AbstractVillager abstractVillager, ItemStack itemStack) {
         return !this.villager.matches(serverPlayer, abstractVillager)?false:this.item.matches(itemStack);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.item.serializeToJson());
         var1.add("villager", this.villager.serializeToJson());
         return var1;
      }
   }
}
