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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeUnlockedTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("recipe_unlocked");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      RecipeUnlockedTrigger.PlayerListeners var3 = (RecipeUnlockedTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new RecipeUnlockedTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      RecipeUnlockedTrigger.PlayerListeners var3 = (RecipeUnlockedTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public RecipeUnlockedTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      ResourceLocation var3 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "recipe"));
      return new RecipeUnlockedTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer serverPlayer, Recipe recipe) {
      RecipeUnlockedTrigger.PlayerListeners var3 = (RecipeUnlockedTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var3 != null) {
         var3.trigger(recipe);
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

      public void trigger(Recipe recipe) {
         List<CriterionTrigger.Listener<RecipeUnlockedTrigger.TriggerInstance>> var2 = null;

         for(CriterionTrigger.Listener<RecipeUnlockedTrigger.TriggerInstance> var4 : this.listeners) {
            if(((RecipeUnlockedTrigger.TriggerInstance)var4.getTriggerInstance()).matches(recipe)) {
               if(var2 == null) {
                  var2 = Lists.newArrayList();
               }

               var2.add(var4);
            }
         }

         if(var2 != null) {
            for(CriterionTrigger.Listener<RecipeUnlockedTrigger.TriggerInstance> var4 : var2) {
               var4.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ResourceLocation recipe;

      public TriggerInstance(ResourceLocation recipe) {
         super(RecipeUnlockedTrigger.ID);
         this.recipe = recipe;
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.addProperty("recipe", this.recipe.toString());
         return var1;
      }

      public boolean matches(Recipe recipe) {
         return this.recipe.equals(recipe.getId());
      }
   }
}
