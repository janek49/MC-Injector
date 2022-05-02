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
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ItemDurabilityTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("item_durability_changed");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      ItemDurabilityTrigger.PlayerListeners var3 = (ItemDurabilityTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new ItemDurabilityTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      ItemDurabilityTrigger.PlayerListeners var3 = (ItemDurabilityTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public ItemDurabilityTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      ItemPredicate var3 = ItemPredicate.fromJson(jsonObject.get("item"));
      MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(jsonObject.get("durability"));
      MinMaxBounds.Ints var5 = MinMaxBounds.Ints.fromJson(jsonObject.get("delta"));
      return new ItemDurabilityTrigger.TriggerInstance(var3, var4, var5);
   }

   public void trigger(ServerPlayer serverPlayer, ItemStack itemStack, int var3) {
      ItemDurabilityTrigger.PlayerListeners var4 = (ItemDurabilityTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var4 != null) {
         var4.trigger(itemStack, var3);
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

      public void trigger(ItemStack itemStack, int var2) {
         List<CriterionTrigger.Listener<ItemDurabilityTrigger.TriggerInstance>> var3 = null;

         for(CriterionTrigger.Listener<ItemDurabilityTrigger.TriggerInstance> var5 : this.listeners) {
            if(((ItemDurabilityTrigger.TriggerInstance)var5.getTriggerInstance()).matches(itemStack, var2)) {
               if(var3 == null) {
                  var3 = Lists.newArrayList();
               }

               var3.add(var5);
            }
         }

         if(var3 != null) {
            for(CriterionTrigger.Listener<ItemDurabilityTrigger.TriggerInstance> var5 : var3) {
               var5.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.Ints durability;
      private final MinMaxBounds.Ints delta;

      public TriggerInstance(ItemPredicate item, MinMaxBounds.Ints durability, MinMaxBounds.Ints delta) {
         super(ItemDurabilityTrigger.ID);
         this.item = item;
         this.durability = durability;
         this.delta = delta;
      }

      public static ItemDurabilityTrigger.TriggerInstance changedDurability(ItemPredicate itemPredicate, MinMaxBounds.Ints minMaxBounds$Ints) {
         return new ItemDurabilityTrigger.TriggerInstance(itemPredicate, minMaxBounds$Ints, MinMaxBounds.Ints.ANY);
      }

      public boolean matches(ItemStack itemStack, int var2) {
         return !this.item.matches(itemStack)?false:(!this.durability.matches(itemStack.getMaxDamage() - var2)?false:this.delta.matches(itemStack.getDamageValue() - var2));
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.item.serializeToJson());
         var1.add("durability", this.durability.serializeToJson());
         var1.add("delta", this.delta.serializeToJson());
         return var1;
      }
   }
}
