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
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class UsedTotemTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("used_totem");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      UsedTotemTrigger.PlayerListeners var3 = (UsedTotemTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new UsedTotemTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      UsedTotemTrigger.PlayerListeners var3 = (UsedTotemTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public UsedTotemTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      ItemPredicate var3 = ItemPredicate.fromJson(jsonObject.get("item"));
      return new UsedTotemTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer serverPlayer, ItemStack itemStack) {
      UsedTotemTrigger.PlayerListeners var3 = (UsedTotemTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var3 != null) {
         var3.trigger(itemStack);
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

      public void trigger(ItemStack itemStack) {
         List<CriterionTrigger.Listener<UsedTotemTrigger.TriggerInstance>> var2 = null;

         for(CriterionTrigger.Listener<UsedTotemTrigger.TriggerInstance> var4 : this.listeners) {
            if(((UsedTotemTrigger.TriggerInstance)var4.getTriggerInstance()).matches(itemStack)) {
               if(var2 == null) {
                  var2 = Lists.newArrayList();
               }

               var2.add(var4);
            }
         }

         if(var2 != null) {
            for(CriterionTrigger.Listener<UsedTotemTrigger.TriggerInstance> var4 : var2) {
               var4.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(ItemPredicate item) {
         super(UsedTotemTrigger.ID);
         this.item = item;
      }

      public static UsedTotemTrigger.TriggerInstance usedTotem(ItemLike itemLike) {
         return new UsedTotemTrigger.TriggerInstance(ItemPredicate.Builder.item().of(itemLike).build());
      }

      public boolean matches(ItemStack itemStack) {
         return this.item.matches(itemStack);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.item.serializeToJson());
         return var1;
      }
   }
}
