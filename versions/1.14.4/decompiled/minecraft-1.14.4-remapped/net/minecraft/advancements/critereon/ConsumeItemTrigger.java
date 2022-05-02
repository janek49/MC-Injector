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
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;

public class ConsumeItemTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("consume_item");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      ConsumeItemTrigger.PlayerListeners var3 = (ConsumeItemTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new ConsumeItemTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      ConsumeItemTrigger.PlayerListeners var3 = (ConsumeItemTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public ConsumeItemTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      return new ConsumeItemTrigger.TriggerInstance(ItemPredicate.fromJson(jsonObject.get("item")));
   }

   public void trigger(ServerPlayer serverPlayer, ItemStack itemStack) {
      ConsumeItemTrigger.PlayerListeners var3 = (ConsumeItemTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
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
         List<CriterionTrigger.Listener<ConsumeItemTrigger.TriggerInstance>> var2 = null;

         for(CriterionTrigger.Listener<ConsumeItemTrigger.TriggerInstance> var4 : this.listeners) {
            if(((ConsumeItemTrigger.TriggerInstance)var4.getTriggerInstance()).matches(itemStack)) {
               if(var2 == null) {
                  var2 = Lists.newArrayList();
               }

               var2.add(var4);
            }
         }

         if(var2 != null) {
            for(CriterionTrigger.Listener<ConsumeItemTrigger.TriggerInstance> var4 : var2) {
               var4.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(ItemPredicate item) {
         super(ConsumeItemTrigger.ID);
         this.item = item;
      }

      public static ConsumeItemTrigger.TriggerInstance usedItem() {
         return new ConsumeItemTrigger.TriggerInstance(ItemPredicate.ANY);
      }

      public static ConsumeItemTrigger.TriggerInstance usedItem(ItemLike itemLike) {
         return new ConsumeItemTrigger.TriggerInstance(new ItemPredicate((Tag)null, itemLike.asItem(), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, new EnchantmentPredicate[0], (Potion)null, NbtPredicate.ANY));
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
