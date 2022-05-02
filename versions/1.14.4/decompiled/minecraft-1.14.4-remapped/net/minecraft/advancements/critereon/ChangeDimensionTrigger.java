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
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.dimension.DimensionType;

public class ChangeDimensionTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("changed_dimension");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      ChangeDimensionTrigger.PlayerListeners var3 = (ChangeDimensionTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new ChangeDimensionTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      ChangeDimensionTrigger.PlayerListeners var3 = (ChangeDimensionTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public ChangeDimensionTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      DimensionType var3 = jsonObject.has("from")?DimensionType.getByName(new ResourceLocation(GsonHelper.getAsString(jsonObject, "from"))):null;
      DimensionType var4 = jsonObject.has("to")?DimensionType.getByName(new ResourceLocation(GsonHelper.getAsString(jsonObject, "to"))):null;
      return new ChangeDimensionTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer serverPlayer, DimensionType var2, DimensionType var3) {
      ChangeDimensionTrigger.PlayerListeners var4 = (ChangeDimensionTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var4 != null) {
         var4.trigger(var2, var3);
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

      public void trigger(DimensionType var1, DimensionType var2) {
         List<CriterionTrigger.Listener<ChangeDimensionTrigger.TriggerInstance>> var3 = null;

         for(CriterionTrigger.Listener<ChangeDimensionTrigger.TriggerInstance> var5 : this.listeners) {
            if(((ChangeDimensionTrigger.TriggerInstance)var5.getTriggerInstance()).matches(var1, var2)) {
               if(var3 == null) {
                  var3 = Lists.newArrayList();
               }

               var3.add(var5);
            }
         }

         if(var3 != null) {
            for(CriterionTrigger.Listener<ChangeDimensionTrigger.TriggerInstance> var5 : var3) {
               var5.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      @Nullable
      private final DimensionType from;
      @Nullable
      private final DimensionType to;

      public TriggerInstance(@Nullable DimensionType from, @Nullable DimensionType to) {
         super(ChangeDimensionTrigger.ID);
         this.from = from;
         this.to = to;
      }

      public static ChangeDimensionTrigger.TriggerInstance changedDimensionTo(DimensionType dimensionType) {
         return new ChangeDimensionTrigger.TriggerInstance((DimensionType)null, dimensionType);
      }

      public boolean matches(DimensionType var1, DimensionType var2) {
         return this.from != null && this.from != var1?false:this.to == null || this.to == var2;
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         if(this.from != null) {
            var1.addProperty("from", DimensionType.getName(this.from).toString());
         }

         if(this.to != null) {
            var1.addProperty("to", DimensionType.getName(this.to).toString());
         }

         return var1;
      }
   }
}
