package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;

public class InventoryChangeTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("inventory_changed");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      InventoryChangeTrigger.PlayerListeners var3 = (InventoryChangeTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new InventoryChangeTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      InventoryChangeTrigger.PlayerListeners var3 = (InventoryChangeTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public InventoryChangeTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      JsonObject jsonObject = GsonHelper.getAsJsonObject(jsonObject, "slots", new JsonObject());
      MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(jsonObject.get("occupied"));
      MinMaxBounds.Ints var5 = MinMaxBounds.Ints.fromJson(jsonObject.get("full"));
      MinMaxBounds.Ints var6 = MinMaxBounds.Ints.fromJson(jsonObject.get("empty"));
      ItemPredicate[] vars7 = ItemPredicate.fromJsonArray(jsonObject.get("items"));
      return new InventoryChangeTrigger.TriggerInstance(var4, var5, var6, vars7);
   }

   public void trigger(ServerPlayer serverPlayer, Inventory inventory) {
      InventoryChangeTrigger.PlayerListeners var3 = (InventoryChangeTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var3 != null) {
         var3.trigger(inventory);
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

      public void trigger(Inventory inventory) {
         List<CriterionTrigger.Listener<InventoryChangeTrigger.TriggerInstance>> var2 = null;

         for(CriterionTrigger.Listener<InventoryChangeTrigger.TriggerInstance> var4 : this.listeners) {
            if(((InventoryChangeTrigger.TriggerInstance)var4.getTriggerInstance()).matches(inventory)) {
               if(var2 == null) {
                  var2 = Lists.newArrayList();
               }

               var2.add(var4);
            }
         }

         if(var2 != null) {
            for(CriterionTrigger.Listener<InventoryChangeTrigger.TriggerInstance> var4 : var2) {
               var4.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Ints slotsOccupied;
      private final MinMaxBounds.Ints slotsFull;
      private final MinMaxBounds.Ints slotsEmpty;
      private final ItemPredicate[] predicates;

      public TriggerInstance(MinMaxBounds.Ints slotsOccupied, MinMaxBounds.Ints slotsFull, MinMaxBounds.Ints slotsEmpty, ItemPredicate[] predicates) {
         super(InventoryChangeTrigger.ID);
         this.slotsOccupied = slotsOccupied;
         this.slotsFull = slotsFull;
         this.slotsEmpty = slotsEmpty;
         this.predicates = predicates;
      }

      public static InventoryChangeTrigger.TriggerInstance hasItem(ItemPredicate... itemPredicates) {
         return new InventoryChangeTrigger.TriggerInstance(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, itemPredicates);
      }

      public static InventoryChangeTrigger.TriggerInstance hasItem(ItemLike... itemLikes) {
         ItemPredicate[] vars1 = new ItemPredicate[itemLikes.length];

         for(int var2 = 0; var2 < itemLikes.length; ++var2) {
            vars1[var2] = new ItemPredicate((Tag)null, itemLikes[var2].asItem(), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, new EnchantmentPredicate[0], (Potion)null, NbtPredicate.ANY);
         }

         return hasItem(vars1);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         if(!this.slotsOccupied.isAny() || !this.slotsFull.isAny() || !this.slotsEmpty.isAny()) {
            JsonObject var2 = new JsonObject();
            var2.add("occupied", this.slotsOccupied.serializeToJson());
            var2.add("full", this.slotsFull.serializeToJson());
            var2.add("empty", this.slotsEmpty.serializeToJson());
            var1.add("slots", var2);
         }

         if(this.predicates.length > 0) {
            JsonArray var2 = new JsonArray();

            for(ItemPredicate var6 : this.predicates) {
               var2.add(var6.serializeToJson());
            }

            var1.add("items", var2);
         }

         return var1;
      }

      public boolean matches(Inventory inventory) {
         int var2 = 0;
         int var3 = 0;
         int var4 = 0;
         List<ItemPredicate> var5 = Lists.newArrayList(this.predicates);

         for(int var6 = 0; var6 < inventory.getContainerSize(); ++var6) {
            ItemStack var7 = inventory.getItem(var6);
            if(var7.isEmpty()) {
               ++var3;
            } else {
               ++var4;
               if(var7.getCount() >= var7.getMaxStackSize()) {
                  ++var2;
               }

               Iterator<ItemPredicate> var8 = var5.iterator();

               while(var8.hasNext()) {
                  ItemPredicate var9 = (ItemPredicate)var8.next();
                  if(var9.matches(var7)) {
                     var8.remove();
                  }
               }
            }
         }

         if(!this.slotsFull.matches(var2)) {
            return false;
         } else if(!this.slotsEmpty.matches(var3)) {
            return false;
         } else if(!this.slotsOccupied.matches(var4)) {
            return false;
         } else if(!var5.isEmpty()) {
            return false;
         } else {
            return true;
         }
      }
   }
}
