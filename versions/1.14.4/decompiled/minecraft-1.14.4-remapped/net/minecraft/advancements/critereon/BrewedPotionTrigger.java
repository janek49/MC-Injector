package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.alchemy.Potion;

public class BrewedPotionTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("brewed_potion");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      BrewedPotionTrigger.PlayerListeners var3 = (BrewedPotionTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new BrewedPotionTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      BrewedPotionTrigger.PlayerListeners var3 = (BrewedPotionTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public BrewedPotionTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      Potion var3 = null;
      if(jsonObject.has("potion")) {
         ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "potion"));
         var3 = (Potion)Registry.POTION.getOptional(var4).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown potion \'" + var4 + "\'");
         });
      }

      return new BrewedPotionTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer serverPlayer, Potion potion) {
      BrewedPotionTrigger.PlayerListeners var3 = (BrewedPotionTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var3 != null) {
         var3.trigger(potion);
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

      public void trigger(Potion potion) {
         List<CriterionTrigger.Listener<BrewedPotionTrigger.TriggerInstance>> var2 = null;

         for(CriterionTrigger.Listener<BrewedPotionTrigger.TriggerInstance> var4 : this.listeners) {
            if(((BrewedPotionTrigger.TriggerInstance)var4.getTriggerInstance()).matches(potion)) {
               if(var2 == null) {
                  var2 = Lists.newArrayList();
               }

               var2.add(var4);
            }
         }

         if(var2 != null) {
            for(CriterionTrigger.Listener<BrewedPotionTrigger.TriggerInstance> var4 : var2) {
               var4.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Potion potion;

      public TriggerInstance(@Nullable Potion potion) {
         super(BrewedPotionTrigger.ID);
         this.potion = potion;
      }

      public static BrewedPotionTrigger.TriggerInstance brewedPotion() {
         return new BrewedPotionTrigger.TriggerInstance((Potion)null);
      }

      public boolean matches(Potion potion) {
         return this.potion == null || this.potion == potion;
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         if(this.potion != null) {
            var1.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
         }

         return var1;
      }
   }
}
