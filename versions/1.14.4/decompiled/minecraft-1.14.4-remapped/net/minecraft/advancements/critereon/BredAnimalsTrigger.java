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
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.animal.Animal;

public class BredAnimalsTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("bred_animals");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      BredAnimalsTrigger.PlayerListeners var3 = (BredAnimalsTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new BredAnimalsTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      BredAnimalsTrigger.PlayerListeners var3 = (BredAnimalsTrigger.PlayerListeners)this.players.get(playerAdvancements);
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

   public BredAnimalsTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      EntityPredicate var3 = EntityPredicate.fromJson(jsonObject.get("parent"));
      EntityPredicate var4 = EntityPredicate.fromJson(jsonObject.get("partner"));
      EntityPredicate var5 = EntityPredicate.fromJson(jsonObject.get("child"));
      return new BredAnimalsTrigger.TriggerInstance(var3, var4, var5);
   }

   public void trigger(ServerPlayer serverPlayer, Animal var2, @Nullable Animal var3, @Nullable AgableMob agableMob) {
      BredAnimalsTrigger.PlayerListeners var5 = (BredAnimalsTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var5 != null) {
         var5.trigger(serverPlayer, var2, var3, agableMob);
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

      public void trigger(ServerPlayer serverPlayer, Animal var2, @Nullable Animal var3, @Nullable AgableMob agableMob) {
         List<CriterionTrigger.Listener<BredAnimalsTrigger.TriggerInstance>> var5 = null;

         for(CriterionTrigger.Listener<BredAnimalsTrigger.TriggerInstance> var7 : this.listeners) {
            if(((BredAnimalsTrigger.TriggerInstance)var7.getTriggerInstance()).matches(serverPlayer, var2, var3, agableMob)) {
               if(var5 == null) {
                  var5 = Lists.newArrayList();
               }

               var5.add(var7);
            }
         }

         if(var5 != null) {
            for(CriterionTrigger.Listener<BredAnimalsTrigger.TriggerInstance> var7 : var5) {
               var7.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate parent;
      private final EntityPredicate partner;
      private final EntityPredicate child;

      public TriggerInstance(EntityPredicate parent, EntityPredicate partner, EntityPredicate child) {
         super(BredAnimalsTrigger.ID);
         this.parent = parent;
         this.partner = partner;
         this.child = child;
      }

      public static BredAnimalsTrigger.TriggerInstance bredAnimals() {
         return new BredAnimalsTrigger.TriggerInstance(EntityPredicate.ANY, EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public static BredAnimalsTrigger.TriggerInstance bredAnimals(EntityPredicate.Builder entityPredicate$Builder) {
         return new BredAnimalsTrigger.TriggerInstance(entityPredicate$Builder.build(), EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public boolean matches(ServerPlayer serverPlayer, Animal var2, @Nullable Animal var3, @Nullable AgableMob agableMob) {
         return !this.child.matches(serverPlayer, agableMob)?false:this.parent.matches(serverPlayer, var2) && this.partner.matches(serverPlayer, var3) || this.parent.matches(serverPlayer, var3) && this.partner.matches(serverPlayer, var2);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("parent", this.parent.serializeToJson());
         var1.add("partner", this.partner.serializeToJson());
         var1.add("child", this.child.serializeToJson());
         return var1;
      }
   }
}
